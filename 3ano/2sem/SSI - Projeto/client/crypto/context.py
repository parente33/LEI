"""client/crypto/context.py
Each side independently establishes their send-session by generating an
ephemeral X25519 key, performing ECDH with the peer's long-term X25519 key,
and deriving a symmetric chain key.  The peer derives the same chain key on
the first received message.

Peer public keys are cached in memory after the first PREKEY message.
They are NOT stored in the vault, they are fetched from the server and
re-verified on every session start.

Vault persistence
-----------------
The vault stores only secret material: the long-term private keys and the
current state of all active sessions.  Call `save()` after any state mutation
that should survive a restart.
"""

from __future__ import annotations

from cryptography.hazmat.primitives.asymmetric.ed25519 import (
    Ed25519PrivateKey, Ed25519PublicKey
)
from cryptography.hazmat.primitives.asymmetric.x25519 import (
    X25519PrivateKey, X25519PublicKey
)

from client.crypto.keys    import *
from client.crypto.session import *

from shared.protocol import Envelope


class CryptoContext:
    """Owns all long-term keys and active session states.

    Constructed either from a fresh key generation (new user or new device)
    or by loading and decrypting an existing vault.
    """

    def __init__(
        self,
        ed25519_priv: Ed25519PrivateKey, x25519_priv: X25519PrivateKey,
        send_sessions: dict[str, SessionState] | None = None,
        recv_sessions: dict[str, SessionState] | None = None,
    ) -> None:
        self._ed25519_priv = ed25519_priv
        self._x25519_priv  = x25519_priv

        # Derived public keys, exposed for upload to server.
        self.ed25519_pub : Ed25519PublicKey = ed25519_priv.public_key()
        self.x25519_pub  : X25519PublicKey  = x25519_priv.public_key()

        self._send_sessions: dict[str, SessionState] = send_sessions or {}
        self._recv_sessions: dict[str, SessionState] = recv_sessions or {}

        # In-memory cache of peer public keys.
        self._peer_ed25519 : dict[str, Ed25519PublicKey] = {}
        self._peer_x25519  : dict[str, X25519PublicKey]  = {}

    # ------------------------------------------------------------------------
    # Construction
    # ------------------------------------------------------------------------

    @classmethod
    def generate(cls) -> "CryptoContext":
        """Generate fresh identity keys for a new user or new device."""
        ed_priv, _ = generate_ed25519()
        x_priv,  _ = generate_x25519()

        return cls(ed_priv, x_priv)

    @classmethod
    def from_state(cls, data: dict) -> "CryptoContext":
        """Reconstruct CryptoContext from decrypted vault dictionary."""
        ed_priv  = ed25519_priv_from_bytes(bytes.fromhex(data["ed25519_priv"]))
        x_priv   = x25519_priv_from_bytes(bytes.fromhex(data["x25519_priv"]))

        send_sessions = {
            cid: SessionState.from_dict(s)
            for cid, s in data.get("send_sessions", {}).items()
        }

        recv_sessions = {
            cid: SessionState.from_dict(s)
            for cid, s in data.get("recv_sessions", {}).items()
        }

        return cls(ed_priv, x_priv, send_sessions, recv_sessions)

    def dump_state(self) -> dict:
        """Export all secret material as a dictionary for vault persistence."""
        return {
            "ed25519_priv"  : ed25519_priv_to_bytes(self._ed25519_priv).hex(),
            "x25519_priv"   : x25519_priv_to_bytes(self._x25519_priv).hex(),

            "send_sessions" : {
                cid: s.to_dict() for cid, s in self._send_sessions.items()
            },

            "recv_sessions" : {
                cid: s.to_dict() for cid, s in self._recv_sessions.items()
            },
        }

    # ------------------------------------------------------------------------
    # Public key helpers
    # ------------------------------------------------------------------------

    def ed25519_pub_bytes(self) -> bytes:
        return ed25519_pub_to_bytes(self.ed25519_pub)

    def x25519_pub_bytes(self) -> bytes:
        return x25519_pub_to_bytes(self.x25519_pub)

    def cache_peer_pubkeys(
        self, username: str,
        ed25519_pub_bytes: bytes, x25519_pub_bytes: bytes,
    ) -> None:
        """Store a peer's public keys in the in-memory cache."""

        if len(ed25519_pub_bytes) != 32:
            raise ValueError(
                f"cache_peer_pubkeys: ed25519 key for {username!r} "
                f"is {len(ed25519_pub_bytes)} bytes, expected 32"
            )

        if len(x25519_pub_bytes) != 32:
            raise ValueError(
                f"cache_peer_pubkeys: x25519 key for {username!r} "
                f"is {len(x25519_pub_bytes)} bytes, expected 32"
            )

        self._peer_ed25519[username] = ed25519_pub_from_bytes(ed25519_pub_bytes)
        self._peer_x25519[username]  = x25519_pub_from_bytes(x25519_pub_bytes)

    def has_peer_pubkeys(self, username: str) -> bool:
        """Return True if both public keys for username are cached."""
        return username in self._peer_ed25519 and username in self._peer_x25519

    # ------------------------------------------------------------------------
    # Encrypt
    # ------------------------------------------------------------------------

    def encrypt(
        self, conv_id: str, plaintext: str,
        my_username: str, peer_username: str,
    ) -> Envelope:
        """Encrypt plaintext for `peer_username` on `conv_id`.

        If no send-session exists for `conv_id`, one is established using the
        cached peer public keys.  The first `Envelope` will have bootstrap
        fields set; all subsequent ones will have them as `None`.
        """
        is_first = conv_id not in self._send_sessions
        bootstrap = None

        if is_first:
            peer_x25519 = self._peer_x25519[peer_username]
            state, bootstrap = establish_session(
                self._ed25519_priv, peer_x25519, peer_username, conv_id
            )

            self._send_sessions[conv_id] = state

        state = self._send_sessions[conv_id]
        new_state, envelope = encrypt_message(
            state, plaintext.encode(), my_username
        )

        self._send_sessions[conv_id] = new_state

        if is_first and bootstrap is not None:
            envelope.ek_pub = Envelope.b64_encode(bootstrap.ek_pub_bytes)
            envelope.sig    = Envelope.b64_encode(bootstrap.sig_bytes)

            envelope.spk_id = bootstrap.spk_id
            envelope.opk_id = bootstrap.opk_id

        return envelope

    # ------------------------------------------------------------------------
    # Decrypt
    # ------------------------------------------------------------------------

    def decrypt(
        self, envelope: Envelope, my_username: str,
    ) -> str:
        """Decrypt an incoming `Envelope`.

        If the `Envelope` has `ek_pub` set (session-init), this method first
        verifies the initiator's signature and derives the recv-session chain
        key.  If the peer's Ed25519 public key is not cached, this raises
        KeyError and the caller should fetch it before retrying.
        """
        sender  = envelope.sender
        conv_id = envelope.conv_id

        if envelope.ek_pub is not None:
            # Session-init: verify the signature and derive the recv chain key.
            if envelope.sig is None:
                raise ValueError(
                    f"Session-init envelope from {sender!r} is missing 'sig'"
                )

            peer_ed25519 = self._peer_ed25519[sender]
            ek_pub_bytes = Envelope.b64_decode(envelope.ek_pub)
            sig_bytes    = Envelope.b64_decode(envelope.sig)

            recv_state = accept_session(
                my_x25519_priv   = self._x25519_priv,
                peer_ed25519_pub = peer_ed25519,
                peer_username    = sender,
                conv_id          = conv_id,
                ek_pub_bytes     = ek_pub_bytes,
                sig_bytes        = sig_bytes,
                session_id       = envelope.session_id,
                my_username      = my_username,
                spk_id           = envelope.spk_id,
                opk_id           = envelope.opk_id,
            )
            self._recv_sessions[conv_id] = recv_state

        if conv_id not in self._recv_sessions:
            raise ValueError(
                f"No recv-session for conv_id {conv_id!r} — "
                "a session-init message (ek_pub set) must arrive first"
            )

        state = self._recv_sessions[conv_id]
        new_state, plaintext_bytes   = decrypt_message(state, envelope, sender)
        self._recv_sessions[conv_id] = new_state

        return plaintext_bytes.decode()
