"""shared/protocol.py
Wire-format contract shared by the client and the server.
"""

from __future__ import annotations

import dataclasses, base64
from dataclasses import dataclass, field
from enum import Enum
from typing import Any

__all__ = [
    "MessageType", "Envelope", "Packet",
]


# ----------------------------------------------------------------------------
# Message types
# ----------------------------------------------------------------------------

class MessageType(str, Enum):
    """All packet types understood by the protocol.

    Direction annotations: CS = client to server, SC = server to client.

    Authentication
    --------------
    REGISTER           CS  Register and store initial X3DH bundle
        {
            username,
            password,
            ik_pub,
            spk,
            spk_id
            spk_sig,
            opks
        }

    REGISTER_OK        SC  Registration ok
        {
            remaining_opks
        }

    LOGIN              CS  Login
        {
            username,
            password
        }

    LOGIN_OK           SC  Login ok
        {
            remaining_opks
        }

    Key management (X3DH-style)
    ---------------------------
    REGISTER_PREKEYS   CS  Refresh signed prekey and OPK pool
        {
            spk,
            spk_id,
            spk_sig,
            opks
        }

    GET_PREKEY_BUNDLE  CS  Request recipient bundle
        {
            username
        }

    PREKEY_BUNDLE      SC  Return recipient public prekey bundle
        {
            username,
            ik_pub,
            spk,
            spk_id,
            spk_sig,
            opk_id,
            opk,
            opk_remaining
        }


    Contacts
    --------
    ADD_CONTACT        CS  Add a contact by username
        {
            contact
        }

    GET_CONTACTS       CS  Request contact list
        {}

    CONTACTS           SC  Return contact list
        {
            contacts: list[str]
        }


    Messaging
    ---------
    SEND_MSG           CS  Send encrypted envelope
        {
            to       : str,
            envelope : Envelope
        }

    DELIVER_MSG        SC  Deliver encrypted envelope
        {
            envelope: Envelope
        }

    FETCH_MSGS         CS  Drain the offline queue
        {}

    ACK_MSGS           CS  Acknowledge delivered messages
        {
            message_ids: list[str]
        }

    Generic
    -------
    ACK                SC  Generic Success
        {
            detail: str
        }

    ERROR              SC  Generic failure
        {
            reason: str
        }
    """

    REGISTER          = "REGISTER"
    REGISTER_OK       = "REGISTER_OK"
    LOGIN             = "LOGIN"
    LOGIN_OK          = "LOGIN_OK"

    REGISTER_PREKEYS  = "REGISTER_PREKEYS"
    GET_PREKEY_BUNDLE = "GET_PREKEY_BUNDLE"
    PREKEY_BUNDLE     = "PREKEY_BUNDLE"

    ADD_CONTACT       = "ADD_CONTACT"
    GET_CONTACTS      = "GET_CONTACTS"
    CONTACTS          = "CONTACTS"

    SEND_MSG          = "SEND_MSG"
    DELIVER_MSG       = "DELIVER_MSG"
    FETCH_MSGS        = "FETCH_MSGS"
    ACK_MSGS          = "ACK_MSGS"

    ACK               = "ACK"
    ERROR             = "ERROR"


# ----------------------------------------------------------------------------
# Envelope
# ----------------------------------------------------------------------------

@dataclass
class Envelope:
    """Cryptographic envelope that wraps a single user message.

    The server stores and routes envelopes as opaque blobs.  It reads only
    the outer `SEND_MSG` payload fields (to, `envelope.conv_id`,
    `envelope.sender`) for routing and never decodes any binary field.

    Fields
    ------
    session_id  Stable identifier for the session.
    conv_id     Conversation label for the 1-to-1 chat, used for grouping/routing.
    sender      Username of the message author.
    counter     Monotonic per-direction message number.
    nonce       AEAD nonce.
    ciphertext  Encrypted payload plus authentication tag.

    ik_pub      Sender's identity public key, only on first message
    ek_pub      Sender's ephemeral X25519 public key, only on first message
    spk_id      Recipient signed prekey identifier used for this bootstrap.
    opk_id      Recipient one-time prekey identifier used, if an OPK was consumed.
    sig         Sender signature over the bootstrap transcript, only on first message.
    """

    session_id : str
    conv_id    : str
    sender     : str
    counter    : int
    nonce      : str
    ciphertext : str

    # First-message-only bootstrap fields
    ik_pub     : str | None = None
    ek_pub     : str | None = None
    spk_id     : int | None = None
    opk_id     : str | None = None
    sig        : str | None = None

    # ------------------------------------------------------------------------
    # base64url helpers
    # ------------------------------------------------------------------------

    @staticmethod
    def b64_encode(data: bytes) -> str:
        """Encode bytes to a `base64url` string."""
        return base64.urlsafe_b64encode(data).rstrip(b'=').decode()

    @staticmethod
    def b64_decode(s: str) -> bytes:
        """Decode a `base64url` string to bytes."""
        padding = (4 - len(s) % 4) % 4
        return base64.urlsafe_b64decode(s + "=" * padding)

    # ------------------------------------------------------------------------
    # Serialisation
    # ------------------------------------------------------------------------

    def to_dict(self) -> dict[str, Any]:
        """Serialise to a plain dict suitable for JSON embedding.

        Only includes fields that are not None so that the wire representation
        stays compact.  `from_dict` tolerates missing optional fields.
        """
        d: dict[str, Any] = {
            "session_id" : self.session_id,
            "conv_id"    : self.conv_id,
            "sender"     : self.sender,
            "counter"    : self.counter,
            "nonce"      : self.nonce,
            "ciphertext" : self.ciphertext,
        }

        if self.ik_pub is not None:
            d["ik_pub"] = self.ik_pub

        if self.ek_pub is not None:
            d["ek_pub"] = self.ek_pub

        if self.spk_id is not None:
            d["spk_id"] = self.spk_id

        if self.opk_id is not None:
            d["opk_id"] = self.opk_id

        if self.sig is not None:
            d["sig"] = self.sig

        return d

    @classmethod
    def from_dict(cls, d: dict[str, Any]) -> "Envelope":
        """Reconstruct an `Envelope` from a plain `dict`.

        Unknown keys are silently ignored for forward compatibility.
        Missing optional fields default to None.
        """
        known = { f.name for f in dataclasses.fields(cls) }
        return cls(**{ k: v for k, v in d.items() if k in known })


# ----------------------------------------------------------------------------
# Packet
# ----------------------------------------------------------------------------

@dataclass
class Packet:
    """Top-level protocol unit exchanged between client and server.

    Payload validation is the responsibility of each handler, not this class.
    Packet is a transparent container; it imposes no schema constraints.
    """

    type    : MessageType
    payload : dict[str, Any] = field(default_factory=dict)
