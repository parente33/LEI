"""client/tui/screens/login.py
Login / Register screen.
"""

from __future__ import annotations

from cryptography.exceptions import InvalidTag

from textual.containers import Center, Vertical
from textual.widgets    import Button, Input, Label
from textual.screen     import Screen
from textual.app        import ComposeResult


class LoginScreen(Screen):
    """Entry screen shown before authentication."""

    _register_mode: bool = False

    def compose(self) -> ComposeResult:
        with Center():
            with Vertical(id="login-container"):
                yield Label("SSI CHAT", id="login-title")

                yield Label("", id="login-mode-label")
                yield Input(placeholder="Username", id="username-input")

                yield Input(
                    placeholder="Password",
                    password=True,
                    id="password-input"
                )

                yield Input(
                    placeholder="Confirm password",
                    password=True,
                    id="confirm-input",
                )

                yield Label("", id="error-label")

                yield Button("Login", variant="primary", id="submit-button")
                yield Button(
                    "Switch to Register",
                    variant="default",
                    id="toggle-button"
                )

    async def on_mount(self) -> None:
        self._apply_mode()

        if not self.app.network.connected:
            try:
                await self.app.network.connect()
            except OSError as exc:
                self.show_error(f"Cannot reach server: {exc}")

    # ------------------------------------------------------------------------
    # Mode toggle
    # ------------------------------------------------------------------------

    def _apply_mode(self) -> None:
        confirm = self.query_one("#confirm-input", Input)
        submit  = self.query_one("#submit-button", Button)

        mode_label = self.query_one("#login-mode-label", Label)
        toggle     = self.query_one("#toggle-button", Button)

        if self._register_mode:
            confirm.display = True
            submit.label    = "Register"

            mode_label.update("Create a new account")
            toggle.label = "Switch to Login"
        else:
            confirm.display = False
            submit.label    = "Login"

            mode_label.update("Sign in to your account")
            toggle.label = "Switch to Register"

        self.query_one("#error-label", Label).update("")

    # ------------------------------------------------------------------------
    # UI helpers
    # ------------------------------------------------------------------------

    def show_error(self, message: str) -> None:
        self.query_one("#error-label", Label).update(message)

    def _set_loading(self, loading: bool) -> None:
        """Disable / re-enable button."""
        for widget_id in ("#submit-button", "#toggle-button"):
            self.query_one(widget_id, Button).disabled = loading

    # ------------------------------------------------------------------------
    # Event handlers
    # ------------------------------------------------------------------------

    async def on_button_pressed(self, event: Button.Pressed) -> None:
        if event.button.id == "toggle-button":
            self._register_mode = not self._register_mode
            self._apply_mode()
        elif event.button.id == "submit-button":
            await self._on_submit()

    async def _on_submit(self) -> None:
        username = self.query_one("#username-input", Input).value.strip()
        password = self.query_one("#password-input", Input).value

        if not username:
            self.show_error("Username cannot be empty.")
            return

        if not password:
            self.show_error("Password cannot be empty.")
            return

        if self._register_mode:
            confirm = self.query_one("#confirm-input", Input).value
            if password != confirm:
                self.show_error("Passwords do not match.")
                return

        if not self.app.network.connected:
            self.show_error("Not connected to server.")
            return

        self._set_loading(True)

        try:
            if self._register_mode:
                await self.app.auth_service.register(username, password)

                self._set_loading(False)
                self._register_mode = False
                self._apply_mode()

                self.query_one("#username-input", Input).value = username
                self.query_one("#password-input", Input).value = ""
                self.query_one("#confirm-input", Input).value  = ""
            else:
                ctx = await self.app.auth_service.login(username, password)
                self.app.crypto = ctx

                self._set_loading(False)
                self.app.push_screen("chat")

        except InvalidTag:
            self.app.state.clear()
            self._set_loading(False)
            self.show_error("Wrong password: cannot unlock your local vault.")

        except Exception as exc:
            self._set_loading(False)
            self.show_error(str(exc))
