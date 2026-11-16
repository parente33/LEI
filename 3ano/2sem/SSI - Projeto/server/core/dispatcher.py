"""server/core/dispatcher.py
Routes every `MessageType` to its handler and enforces
authentication requirements before any handler is invoked.
"""

from __future__ import annotations

from server.core.registry import ConnectionRegistry
from shared.protocol      import MessageType, Packet
from server.core.db       import DB

from server.handlers.messages import handle_ack_msgs, handle_fetch, handle_send
from server.handlers.contacts import handle_add_contact, handle_get_contacts
from server.handlers.auth     import handle_login, handle_register
from server.handlers.keys     import handle_get_prekey_bundle, handle_register_prekeys


class Dispatcher:
    """Routes incoming packets to the appropriate handler."""

    def __init__(self, db: DB, registry: ConnectionRegistry) -> None:
        self._db       = db
        self._registry = registry

    async def dispatch(
        self,
        packet: Packet,
        username: str | None,
    ) -> Packet:
        """Route one packet and return the single response Packet."""

        # -- Unauthenticated -------------------------------------------------
        if packet.type == MessageType.REGISTER:
            return handle_register(self._db, packet.payload)

        if packet.type == MessageType.LOGIN:
            return handle_login(self._db, packet.payload)

        # -- Require authentication ------------------------------------------
        if username is None:
            return Packet(
                MessageType.ERROR,
                {"reason": "Authentication required"},
            )

        # -- Authenticated ---------------------------------------------------

        # -- Messaging -------------------------------------------------------
        if packet.type == MessageType.SEND_MSG:
            return await handle_send(
                self._db, self._registry,
                username, packet.payload,
            )

        if packet.type == MessageType.FETCH_MSGS:
            return await handle_fetch(self._db, self._registry, username)

        if packet.type == MessageType.ACK_MSGS:
            return handle_ack_msgs(self._db, username, packet.payload)

        # -- Key management --------------------------------------------------
        if packet.type == MessageType.REGISTER_PREKEYS:
            return handle_register_prekeys(self._db, username, packet.payload)

        if packet.type == MessageType.GET_PREKEY_BUNDLE:
            return handle_get_prekey_bundle(self._db, packet.payload)

        # -- Contacts --------------------------------------------------------
        if packet.type == MessageType.GET_CONTACTS:
            return handle_get_contacts(self._db, username)

        if packet.type == MessageType.ADD_CONTACT:
            return handle_add_contact(self._db, username, packet.payload)

        return Packet(
            MessageType.ERROR,
            {"reason": f"Unknown message type: {packet.type.value}"},
        )
