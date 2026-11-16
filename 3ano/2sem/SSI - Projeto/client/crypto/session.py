"""client/crypto/session.py
ECDH session establishment and symmetric KDF chain.

Session model
-------------
Each directional conversation (Alice to Bob, Bob to Alice) has an independent
session.  Alice initiates her send-session by performing ECDH with Bob's
long-term X25519 public key and an ephemeral key.  Bob, on receiving the
first message, derives the same chain key by inverting the ECDH.  He then
establishes his own send-session the same way when he first replies.

This means `CryptoContext` maintains two sessions per conversation:
  - `send_sessions[conv_id]` : the session I initiated        (my outbound chain)
  - `recv_sessions[conv_id]` : the session the peer initiated (their inbound chain)

KDF chain (forward secrecy)
---------------------------
For each message:
    MK      = HMAC-SHA256(chain_key, b'\x01')  # 32-byte message key
    CK_next = HMAC-SHA256(chain_key, b'\x02')  # next chain key

MK is used once then discarded.  Compromise of `CK_n` reveals only messages
from index n onward because all earlier MKs have been deleted.

The counter is monotonic per session direction.
"""

from __future__ import annotations

import os, struct
from dataclasses import dataclass

from cryptography.hazmat.primitives import hashes, hmac as crypto_hmac
from cryptography.hazmat.primitives.asymmetric.ed25519 import (
    Ed25519PrivateKey, Ed25519PublicKey
)
from cryptography.hazmat.primitives.asymmetric.x25519 import (
    X25519PrivateKey, X25519PublicKey
)
from cryptography.hazmat.primitives.kdf.hkdf import HKDF

from client.crypto      import aead
from client.crypto.keys import (
    generate_x25519, x25519_pub_from_bytes, x25519_pub_to_bytes
)

from shared.protocol import Envelope


# ----------------------------------------------------------------------------
# SessionState
# ----------------------------------------------------------------------------

class SessionState:
    """All mutable state for one directional KDF chain.

    Attributes
    ----------
    session_id : Hex string agreed at session-init time.
    conv_id    : Conversation identifier matching `Envelope.conv_id`.
    peer       : The other party's username.
    chain_key  : Current 32-byte chain key.  Advances with every message.
    counter    : Monotonic count of messages on this session.
    """

    __slots__ = (
        "session_id", "conv_id", "peer",
        "chain_key", "counter"
    )

    def __init__(
        self,
        session_id: str, conv_id: str,
        peer: str, chain_key: bytes,
        counter: int = 0,
    ) -> None:
        self.session_id   = session_id
        self.conv_id      = conv_id
        self.peer         = peer
        self.chain_key    = chain_key
        self.counter      = counter

    def to_dict(self) -> dict:
        return {
            "session_id": self.session_id,
            "conv_id":    self.conv_id,
            "peer":       self.peer,
            "chain_key":  self.chain_key.hex(),
            "counter":    self.counter,
        }

    @classmethod
    def from_dict(cls, d: dict) -> "SessionState":
        return cls(
            session_id = d["session_id"],
            conv_id    = d["conv_id"],
            peer       = d["peer"],
            chain_key  = bytes.fromhex(d["chain_key"]),
            counter    = d.get("counter", 0),
        )


@dataclass
class BootstrapData:
    """Container for the session initialization
    material sent in the first message.
    """

    ek_pub_bytes : bytes
    sig_bytes    : bytes
    spk_id       : int | None
    opk_id       : str | None


# ----------------------------------------------------------------------------
# Internal helpers
# ----------------------------------------------------------------------------

def _hkdf(ikm: bytes, salt: bytes, info: bytes) -> bytes:
    """32-byte HKDF-SHA256 output."""
    return HKDF(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        info=info,
    ).derive(ikm)


def _hmac(key: bytes, data: bytes) -> bytes:
    """HMAC-SHA256."""
    h = crypto_hmac.HMAC(key, hashes.SHA256())
    h.update(data)

    return h.finalize()


def _kdf_chain(chain_key: bytes) -> tuple[bytes, bytes]:
    """Derive (MK, CK_next) from chain_key."""
    mk      = _hmac(chain_key, b"\x01")
    ck_next = _hmac(chain_key, b"\x02")

    return mk, ck_next


# ----------------------------------------------------------------------------
# Associated data
# ----------------------------------------------------------------------------

def build_ad(sender: str, conv_id: str, counter: int) -> bytes:
    """Build the AEAD associated data bytes."""
    s = sender  .encode()
    c = conv_id .encode()

    return (
        b"SSIChat"
        + struct.pack(">H", len(s)) + s
        + struct.pack(">H", len(c)) + c
        + struct.pack(">Q", counter)
    )


# ----------------------------------------------------------------------------
# Session establishment
# ----------------------------------------------------------------------------

_HKDF_SALT    = b"\x00" * 32
_SESSION_INFO = b"SSIChat-session"
_CHAIN_INFO   = b"SSIChat-chain"


def establish_session(
    my_ed25519_priv: Ed25519PrivateKey,
    peer_x25519_pub: X25519PublicKey,
    peer_username: str, conv_id: str,
    spk_id: int | None = None,
    opk_id: str | None = None,
) -> tuple[SessionState, BootstrapData]:
    """Initiator path: ECDH + key derivation + signature."""

    ek_priv, ek_pub = generate_x25519()
    ek_pub_bytes    = x25519_pub_to_bytes(ek_pub)

    shared    = ek_priv.exchange(peer_x25519_pub)
    sk        = _hkdf(shared, _HKDF_SALT, _SESSION_INFO)
    chain_key = _hkdf(sk, _HKDF_SALT, _CHAIN_INFO)

    session_id = os.urandom(16).hex()

    # Signature binds session_id + ephemeral public key + peer identity + prekeys.
    # The peer verifies this using Alice's Ed25519 public key fetched from
    # the server, preventing a MITM from substituting a different EK_pub.

    spk_id_str = str(spk_id) if spk_id is not None else ""
    opk_id_str = str(opk_id) if opk_id is not None else ""

    sig_payload = (
        session_id.encode()    +
        ek_pub_bytes           +
        peer_username.encode() +
        spk_id_str.encode()    +
        opk_id_str.encode()
    )
    sig_bytes   = my_ed25519_priv.sign(sig_payload)

    state = SessionState(
        session_id=session_id,
        conv_id=conv_id,
        peer=peer_username,
        chain_key=chain_key,
        counter=0,
    )

    bootstrap = BootstrapData(
        ek_pub_bytes=ek_pub_bytes,
        sig_bytes=sig_bytes,
        spk_id=spk_id,
        opk_id=opk_id,
    )

    return state, bootstrap


def accept_session(
    my_x25519_priv: X25519PrivateKey,
    peer_ed25519_pub: Ed25519PublicKey,
    peer_username: str,
    conv_id: str,
    ek_pub_bytes: bytes,
    sig_bytes: bytes,
    session_id: str,
    my_username: str,
    spk_id: int | None = None,
    opk_id: str | None = None,
) -> SessionState:
    """Responder path: verify signature, derive the matching chain key."""

    spk_id_str = str(spk_id) if spk_id is not None else ""
    opk_id_str = str(opk_id) if opk_id is not None else ""

    sig_payload = (
        session_id.encode()  +
        ek_pub_bytes         +
        my_username.encode() +
        spk_id_str.encode()  +
        opk_id_str.encode()
    )
    peer_ed25519_pub.verify(sig_bytes, sig_payload)

    ek_pub    = x25519_pub_from_bytes(ek_pub_bytes)
    shared    = my_x25519_priv.exchange(ek_pub)
    sk        = _hkdf(shared, _HKDF_SALT, _SESSION_INFO)
    chain_key = _hkdf(sk, _HKDF_SALT, _CHAIN_INFO)

    return SessionState(
        session_id=session_id,
        conv_id=conv_id,
        peer=peer_username,
        chain_key=chain_key,
        counter=0,
    )


# ----------------------------------------------------------------------------
# Encrypt / decrypt
# ----------------------------------------------------------------------------

def encrypt_message(
    state: SessionState,
    plaintext: bytes,
    my_username: str,
) -> tuple[SessionState, Envelope]:
    """Derive a message key, encrypt, advance the chain.

    Returns the new `SessionState` (with advanced chain_key and counter)
    and an `Envelope` with `ek_pub=None` and `sig=None` (not a session-init message).
    The caller sets bootstrap fields if this is the first message of a new session.
    """
    mk, ck_next = _kdf_chain(state.chain_key)
    ad          = build_ad(my_username, state.conv_id, state.counter)
    nonce, ct   = aead.encrypt(mk, plaintext, ad)

    envelope = Envelope(
        session_id = state.session_id,
        conv_id    = state.conv_id,
        sender     = my_username,
        counter    = state.counter,
        nonce      = Envelope.b64_encode(nonce),
        ciphertext = Envelope.b64_encode(ct),
        ek_pub     = None,
        sig        = None,
    )

    new_state = SessionState(
        session_id = state.session_id,
        conv_id    = state.conv_id,
        peer       = state.peer,
        chain_key  = ck_next,
        counter    = state.counter + 1,
    )

    return new_state, envelope


def decrypt_message(
    state: SessionState,
    envelope: Envelope,
    peer_username: str,
) -> tuple[SessionState, bytes]:
    """Verify counter, derive message key, decrypt.

    Returns the new `SessionState` (counter advanced) and the plaintext.
    """
    if envelope.counter != state.counter:
        raise ValueError(
            f"Counter mismatch: expected {state.counter}, got {envelope.counter}"
        )

    mk, ck_next = _kdf_chain(state.chain_key)
    ad          = build_ad(peer_username, state.conv_id, envelope.counter)
    nonce       = Envelope.b64_decode(envelope.nonce)
    ct          = Envelope.b64_decode(envelope.ciphertext)
    plaintext   = aead.decrypt(mk, nonce, ct, ad)

    new_state = SessionState(
        session_id = state.session_id,
        conv_id    = state.conv_id,
        peer       = state.peer,
        chain_key  = ck_next,
        counter    = state.counter + 1,
    )

    return new_state, plaintext
