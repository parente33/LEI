"""client/tui/screens/chat.py
Main chat screen.
"""

from __future__ import annotations

import asyncio
from pathlib import Path

from textual.containers import Horizontal, Vertical
from textual.widgets    import Input, Label, ListView, ListItem, RichLog
from textual.screen     import Screen
from textual.app        import ComposeResult

from client.core.local_history import LocalHistory
from client.core.coordinator   import ClientCoordinator
from client.core.models        import Message

from client.crypto.vault import Vault

_DATA_DIR = Path(__file__).parent.parent.parent / "data"


class ChatScreen(Screen):
    """The main chat interface shown after successful login."""

    _active_contact: str | None = None

    def compose(self) -> ComposeResult:
        with Horizontal(id="chat-layout"):
            with Vertical(id="sidebar"):
                yield Label("CONTACTS", id="contacts-title")
                yield ListView(id="contacts-list")
                yield Input(placeholder="Add contact...", id="add-contact-input")

            with Vertical(id="main-panel"):
                yield Label("Select a contact to start chatting", id="chat-header")
                yield RichLog(id="chat-log", wrap=True, highlight=False, markup=True)
                yield Input(
                    placeholder="Type a message…",
                    id="message-input",
                    disabled=True,
                )

    async def on_mount(self) -> None:
        self._history = LocalHistory(self.app.state.username)
        self._vault   = Vault(_DATA_DIR)

        # Initialize the coordinator, passing dependency managers and a provider
        # for retrieving the active user's CryptoContext.
        self._coordinator = ClientCoordinator(
            network=self.app.network,
            state=self.app.state,
            local_history=self._history,
            vault=self._vault,
            crypto_context_provider=lambda: self.app.crypto,
        )

        self.app.state.on_contacts_updated(self.populate_contacts)
        self.app.state.on_message_added(self._on_message_added)

        self._coordinator.start()

        await self._coordinator.fetch_offline_messages()
        await self._coordinator.fetch_contacts()

    def on_unmount(self) -> None:
        self._coordinator.stop()
        self.app.state.remove_listeners()
        self._history.close()

    # ------------------------------------------------------------------------
    # Contact selection
    # ------------------------------------------------------------------------

    def on_list_view_selected(self, event: ListView.Selected) -> None:
        username = str(event.item.query_one(Label).render())
        self._select_contact(username)

    def _select_contact(self, username: str) -> None:
        self._active_contact = username
        self.query_one("#chat-header", Label).update(f"@ {username}")
        self.query_one("#message-input", Input).disabled = False
        self._load_history(username)

        # Pre-emptively fetch the peer's public keys so sending is fast
        if not self.app.crypto.has_peer_pubkeys(username):
            asyncio.create_task(self._coordinator._fetch_pubkeys(username))

    # ------------------------------------------------------------------------
    # History
    # ------------------------------------------------------------------------

    def _load_history(self, peer: str) -> None:
        """Clear the log and reload persisted messages for this conversation.

        Also rebuilds the in-memory conversation buffer so the crypto layer's
        session state matches what was last written to the vault.
        """
        log = self.query_one("#chat-log", RichLog)
        log.clear()

        self.app.state.remove_listeners()
        self.app.state.on_contacts_updated(self.populate_contacts)

        try:
            conv = self.app.state.get_or_create_conversation(peer)
            messages = self._history.load_messages(conv.conv_id)

            for msg in messages:
                self._write_message(msg.sender, msg.content, msg.is_outbound)
                self.app.state.add_message(conv.conv_id, msg)
        finally:
            # Re-register message listener
            self.app.state.on_message_added(self._on_message_added)

    # ------------------------------------------------------------------------
    # Message display & Reactive listeners
    # ------------------------------------------------------------------------

    def _write_message(self, sender: str, content: str, outbound: bool) -> None:
        log = self.query_one("#chat-log", RichLog)
        if outbound:
            log.write(f"[bold]you[/bold]: {content}")
        else:
            log.write(f"[bold]{sender}[/bold]: {content}")

    def _on_message_added(self, conv_id: str, msg: Message) -> None:
        """Passive listener fired automatically when AppState receives a new message."""
        if self._active_contact is not None:
            active_conv = self.app.state.get_or_create_conversation(
                self._active_contact
            )
            if conv_id == active_conv.conv_id:
                self._write_message(msg.sender, msg.content, msg.is_outbound)

    # ------------------------------------------------------------------------
    # Sending
    # ------------------------------------------------------------------------

    async def on_input_submitted(self, event: Input.Submitted) -> None:
        if event.input.id == "message-input":
            await self._on_send_message(event.value)
            event.input.value = ""
        elif event.input.id == "add-contact-input":
            await self._on_add_contact(event.value)
            event.input.value = ""

    async def _on_send_message(self, text: str) -> None:
        text = text.strip()
        if not text or self._active_contact is None:
            return

        await self._coordinator.send_message(self._active_contact, text)

    async def _on_add_contact(self, contact: str) -> None:
        await self._coordinator.add_contact(contact)

    # ------------------------------------------------------------------------
    # Contact list
    # ------------------------------------------------------------------------

    def populate_contacts(self, usernames: list[str]) -> None:
        contacts_list = self.query_one("#contacts-list", ListView)
        contacts_list.clear()
        for username in usernames:
            contacts_list.append(ListItem(Label(username)))
