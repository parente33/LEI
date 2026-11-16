"""client/__main__.py
Client application entry point.
"""

from __future__ import annotations

import sys, logging
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from textual.app import App

from client.core.state        import AppState
from client.core.net_client   import NetClient
from client.core.auth_service import AuthService

from client.crypto.vault   import Vault
from client.crypto.context import CryptoContext

from client.tui.screens import (LoginScreen, ChatScreen)

from shared.config import HOST, PORT
from shared.logger import setup_logging

log = logging.getLogger(__name__)


class ChatApp(App):
    """Root application.  Created once, lives for the process lifetime."""

    CSS_PATH = str(Path(__file__).parent / "tui" / "styles" / "main.tcss")

    SCREENS = {
        "login" : LoginScreen,
        "chat"  : ChatScreen,
    }

    def __init__(self, *args, **kwargs) -> None:
        super().__init__(*args, **kwargs)

        self.data_dir = Path(__file__).parent / "data"
        self.data_dir.mkdir(parents=True, exist_ok=True)
        setup_logging("client", log_file=Path(self.data_dir / "client.log"))

        self.state = AppState()

        self.network = NetClient(HOST, PORT)
        self._crypto : CryptoContext | None = None

        self.vault         = Vault(self.data_dir)
        self.auth_service  = AuthService(
            self.network, self.vault, self.state
        )



    # ------------------------------------------------------------------------
    # Lifecycle
    # ------------------------------------------------------------------------

    def on_mount(self) -> None:
        self.theme = "ansi-dark"
        self.push_screen("login")

    async def on_unmount(self) -> None:
        """Disconnect cleanly on app exit."""
        if self.network.connected:
            await self.network.disconnect()


if __name__ == "__main__":
    ChatApp().run()
