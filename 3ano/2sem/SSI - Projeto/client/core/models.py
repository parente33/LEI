"""client/core/models.py
Pure data models.

These types travel up through the stack: the crypto layer decrypts into
plaintext, constructs a `Message`, and the TUI reads from `Conversation.messages`.
The TUI never sees raw bytes or `Envelope` objects.
"""

from __future__ import annotations

from dataclasses import dataclass, field


@dataclass
class Contact:
    """A known user in the local contact list."""
    username: str


@dataclass
class Message:
    """A single decrypted message as the TUI sees it.

    `content`      is always plaintext; the crypto layer decrypts before
                   constructing this object.  The TUI must never receive
                   ciphertext.

    `timestamp_ms` is a Unix millisecond timestamp, consistent with the
                   `LocalHistory` schema.

    `is_outbound`  is True for messages sent by the local user.
    """
    sender:       str
    content:      str
    timestamp_ms: int  = field(default_factory=lambda: 0)
    is_outbound:  bool = False


@dataclass
class Conversation:
    """A chat thread between two participants.

    `conv_id`      is the stable conversation identifier.

    `participants` are the other users in the chat.
 
    `messages`     is the in-memory display buffer.
    """
    conv_id:      str
    participants: list[str]
    messages:     list[Message] = field(default_factory=list)
