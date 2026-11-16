"""client/core/coordinator.py
Client coordinator managing E2EE messaging flows.
"""

from __future__      import annotations

import logging
from collections.abc import Callable

from client.core.local_history import LocalHistory
from client.core.net_client    import NetClient
from client.core.models        import Contact, Message
from client.core.state         import AppState

from client.crypto.context import CryptoContext
from client.crypto.vault   import Vault

from shared.protocol import MessageType, Packet, Envelope

log = logging.getLogger(__name__)


class ClientCoordinator:
    """Orchestrates high-level business logic, cryptographic
    message processing, persistence, and networking.
    """

    def __init__(
        self,
        network: NetClient,
        state: AppState,
        local_history: LocalHistory,
        vault: Vault,
        crypto_context_provider: Callable[[], CryptoContext],
    ) -> None:
        self._state   = state
        self._network = network

        self._history = local_history
        self._vault   = vault

        self._crypto_provider = crypto_context_provider

        # (plaintext, peer) held while waiting for peer's pubkeys before encrypting.
        self._pending_send: tuple[str, str] | None = None

        # All envelopes from senders whose pubkeys are not yet cached, in order.
        self._pending_recv: list[Envelope] = []

    @property
    def _crypto(self) -> CryptoContext:
        """Returns the active cryptographic context."""
        return self._crypto_provider()

    def start(self) -> None:
        """Register network event listeners."""
        self._network.on(MessageType.CONTACTS,      self._on_contacts)
        self._network.on(MessageType.DELIVER_MSG,   self._on_deliver_msg)
        self._network.on(MessageType.PREKEY_BUNDLE, self._on_pubkey)

    def stop(self) -> None:
        """Unregister network event listeners and flush pending queues."""
        self._network.off(MessageType.CONTACTS)
        self._network.off(MessageType.DELIVER_MSG)
        self._network.off(MessageType.PREKEY_BUNDLE)

        self._pending_recv.clear()
        self._pending_send = None

    # ------------------------------------------------------------------------
    # Outbound Actions
    # ------------------------------------------------------------------------

    async def fetch_contacts(self) -> None:
        """Fetch the contact list from the server."""
        await self._network.send_packet(Packet(MessageType.GET_CONTACTS, {}))

    async def add_contact(self, contact: str) -> None:
        """Add a contact to the authenticated contact list."""
        contact = contact.strip()
        if contact:
            await self._network.send_packet(
                Packet(MessageType.ADD_CONTACT, {"contact": contact})
            )

    async def fetch_offline_messages(self) -> None:
        """Request delivery of queued offline messages from the server."""
        await self._network.send_packet(Packet(MessageType.FETCH_MSGS, {}))

    async def send_message(self, peer: str, text: str) -> None:
        """ Send an outbound message, executing X3DH bootstrap
        if peer public keys are missing.
        """
        text = text.strip()
        if not text:
            return

        if not self._crypto.has_peer_pubkeys(peer):
            self._pending_send = (text, peer)
            await self._fetch_pubkeys(peer)
            return

        await self._do_encrypt_and_send(text, peer)

    async def _fetch_pubkeys(self, username: str) -> None:
        """Transmit a GET_PREKEY_BUNDLE request to the server."""
        await self._network.send_packet(
            Packet(MessageType.GET_PREKEY_BUNDLE, {"username": username})
        )

    async def _do_encrypt_and_send(self, text: str, peer: str) -> None:
        """Encrypt and transmit a message using
        the active cryptographic context.
        """
        my_username = self._state.username
        conv = self._state.get_or_create_conversation(peer)

        try:
            envelope = self._crypto.encrypt(
                conv_id=conv.conv_id,
                plaintext=text,
                my_username=my_username,
                peer_username=peer,
            )

        except Exception as exc:
            self._state.add_message(
                conv.conv_id, Message(
                    sender="none",
                    content=f"Encryption error: {exc}"
                )
            )
            return

        ok = await self._network.send_packet(
            Packet(
                MessageType.SEND_MSG, {"to": peer, "envelope": envelope.to_dict()}
            )
        )
        if not ok:
            self._state.add_message(
                conv.conv_id,
                Message(
                    sender="none",
                    content="Failed to send message: connection lost."
                ),
            )
            return

        msg = Message(sender=my_username, content=text, is_outbound=True)
        self._state.add_message(conv.conv_id, msg)

        # Sent messages have no server-assigned counter; dedup_key is empty.
        self._history.save_message(conv.conv_id, my_username, text)
        self._save_vault()

    # ------------------------------------------------------------------------
    # Network Packet Handlers
    # ------------------------------------------------------------------------

    async def _on_contacts(self, packet: Packet) -> None:
        """Handle incoming CONTACTS packet."""
        contacts: list[str] = packet.payload.get("contacts", [])
        self._state.set_contacts(contacts)

    async def _on_pubkey(self, packet: Packet) -> None:
        """Cache received public keys, then flush all pending operations."""
        username = packet.payload.get("username", "")

        ed_b64 = packet.payload.get("ik_pub", "")
        x_b64  = packet.payload.get("spk", "")

        if not username or not ed_b64 or not x_b64:
            return

        try:
            self._crypto.cache_peer_pubkeys(
                username,
                Envelope.b64_decode(ed_b64),
                Envelope.b64_decode(x_b64),
            )

        except Exception as exc:
            log.warning(f"bad pubkey for {username!r}: {exc}")
            return

        # Flush a pending outbound send for this peer.
        if self._pending_send is not None:
            text, peer = self._pending_send
            if peer == username:
                self._pending_send = None
                await self._do_encrypt_and_send(text, peer)

        # Flush ALL pending inbound envelopes from this sender, in order.
        still_pending: list[Envelope] = []
        for env in self._pending_recv:
            if env.sender == username:
                await self._process_envelope(env)
            else:
                still_pending.append(env)

        self._pending_recv = still_pending

    async def _on_deliver_msg(self, packet: Packet) -> None:
        """Receive and route an incoming encrypted envelope."""
        envelope_dict = packet.payload.get("envelope", {})

        try:
            envelope = Envelope.from_dict(envelope_dict)
        except Exception as exc:
            log.warning(f"malformed envelope: {exc}")
            return

        sender = envelope.sender
        if not sender or sender == self._state.username:
            return

        if any(e.sender == sender for e in self._pending_recv):
            self._pending_recv.append(envelope)
            return

        # Session-init: we need the sender's keys before we can accept_session.
        if envelope.ek_pub is not None and not self._crypto.has_peer_pubkeys(
            sender
        ):
            self._pending_recv.append(envelope)
            await self._fetch_pubkeys(sender)
            return

        await self._process_envelope(envelope)

    async def _process_envelope(self, envelope: Envelope) -> None:
        """Decrypt, persist, and notify a received envelope."""
        sender = envelope.sender

        if not sender or sender == self._state.username:
            return

        if sender not in self._state.contacts:
            self._state.add_contact(Contact(username=sender))

        try:
            content = self._crypto.decrypt(
                envelope=envelope,
                my_username=self._state.username,
            )
        except Exception as exc:
            log.warning(f"decryption failed from {sender!r}: {exc}")
            return

        conv = self._state.get_or_create_conversation(sender)
        msg = Message(sender=sender, content=content, is_outbound=False)
        self._state.add_message(conv.conv_id, msg)

        dedup_key = f"{envelope.session_id}:{envelope.counter}"
        self._history.save_message(conv.conv_id, sender, content, dedup_key)
        self._save_vault()

    def _save_vault(self) -> None:
        """Save the updated cryptographic session state immediately."""
        try:
            self._vault.save(
                self._state.username,
                self._state.password,
                self._crypto,
            )
        except Exception as exc:
            log.warning(f"vault save failed: {exc}")
