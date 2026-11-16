"""server/handlers/contacts.py
Contact list management.
"""

from __future__ import annotations

import logging

from server.core.db import DB
from shared.protocol import MessageType, Packet

log = logging.getLogger(__name__)


def handle_add_contact(db: DB, username: str, payload: dict) -> Packet:
    """Persist a new contact for the authenticated user.

    Returns CONTACTS with the updated list on success so the client can
    refresh its contact view from the same callback used by GET_CONTACTS,
    ERROR if the contact username does not exist in the system.
    """
    contact = payload.get("contact", "").strip()

    if not contact:
        return Packet(
            MessageType.ERROR,
            {"reason": "Missing contact username"}
        )

    if contact == username:
        return Packet(
            MessageType.ERROR,
            {"reason": "Cannot add yourself as a contact"}
        )

    if not db.user_exists(contact):
        return Packet(
            MessageType.ERROR,
            {"reason": f"User '{contact}' does not exist"}
        )

    try:
        with db.transaction():
            db.add_contact(username, contact)

    except Exception as exc:
        log.error(
            "Failed to add contact for %s: %s",
            username, exc, exc_info=True
        )
        return Packet(
            MessageType.ERROR,
            {"reason": f"Failed to add contact for '{username}"}
        )

    contacts = db.get_contacts(username)
    return Packet(MessageType.CONTACTS, {"contacts": contacts})


def handle_get_contacts(db: DB, username: str) -> Packet:
    """Return the full contact list for the authenticated user."""
    contacts = db.get_contacts(username)
    return Packet(MessageType.CONTACTS, {"contacts": contacts})
