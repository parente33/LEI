"""client/core/state.py
`Runtime` application state owned by `ChatApp`.
"""

from __future__ import annotations

from collections.abc import Callable
import hashlib

from .models import Contact, Conversation, Message


class AppState:
    def __init__(self) -> None:
        self.username: str | None = None
        self.password: str | None = None  # vault passphrase

        # username -> Contact
        self.contacts      : dict[str, Contact]      = {}

        # conv_id  -> Conversation
        self.conversations : dict[str, Conversation] = {}

        self._contacts_listeners : list[Callable[[list[str]], None]] = []
        self._message_listeners  : list[Callable[[str, Message], None]] = []

    def on_contacts_updated(self, cb: Callable[[list[str]], None]) -> None:
        """Register a callback for when the contact list changes."""
        self._contacts_listeners.append(cb)

    def on_message_added(self, cb: Callable[[str, Message], None]) -> None:
        """ Register a callback for when a new message
        is appended to a conversation.
        """
        self._message_listeners.append(cb)

    def remove_listeners(self) -> None:
        """Clear all active listeners."""
        self._contacts_listeners .clear()
        self._message_listeners  .clear()

    # ------------------------------------------------------------------------
    # Conversation helpers
    # ------------------------------------------------------------------------

    @staticmethod
    def make_conv_id(a: str, b: str) -> str:
        """Deterministic, order-independent conversation ID for a `1:1` chat.

        Returns the first 16 hex chars of SHA-256 over the sorted,
        pipe-joined pair of usernames.
        """
        pair = "|".join(sorted([a, b]))
        return hashlib.sha256(pair.encode()).hexdigest()[:16]

    def get_or_create_conversation(self, peer: str) -> Conversation:
        """Return the existing `1:1` conversation with peer, or create it."""
        if self.username is None:
            raise RuntimeError("Cannot get a conversation before logging in.")

        conv_id = self.make_conv_id(self.username, peer)
        if conv_id not in self.conversations:
            self.conversations[conv_id] = Conversation(
                conv_id=conv_id, participants=[self.username, peer],
            )

        return self.conversations[conv_id]

    # ------------------------------------------------------------------------
    # Contact helpers
    # ------------------------------------------------------------------------

    def add_contact(self, contact: Contact) -> None:
        """Add or replace a single contact entry."""
        self.contacts[contact.username] = contact
        for cb in self._contacts_listeners:
            try:
                cb(list(self.contacts.keys()))
            except Exception:
                pass

    def set_contacts(self, usernames: list[str]) -> None:
        """Replace the entire contact list from a server CONTACTS response.

        Replaces `self.contacts` atomically so that contacts removed on
        another session are not left as stale entries.
        """
        self.contacts = {u: Contact(username=u) for u in usernames}
        for cb in self._contacts_listeners:
            try:
                cb(usernames)
            except Exception:
                pass

    def add_message(self, conv_id: str, message: Message) -> None:
        """Append a message to an existing conversation.

        Silently drops the message if the conversation is not found
        """
        conv = self.conversations.get(conv_id)
        if conv is not None:
            conv.messages.append(message)
            for cb in self._message_listeners:
                try:
                    cb(conv_id, message)
                except Exception:
                    pass

    def clear(self) -> None:
        """Reset to unauthenticated state."""
        self.username = None
        self.password = None

        self.contacts.clear()
        self.conversations.clear()

        self.remove_listeners()
