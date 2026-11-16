"""client/core/auth.py
Service for user registration and authentication.
"""

from __future__ import annotations

import asyncio, logging

from shared.protocol import MessageType, Packet, Envelope

from client.core.local_history import LocalHistory
from client.core.net_client    import NetClient
from client.core.state         import AppState

from client.crypto.context import CryptoContext
from client.crypto.vault   import Vault

log = logging.getLogger(__name__)


class AuthService:
    """Manages user registration/authentication
    and cryptographic identity key upload.
    """

    def __init__(
        self,
        network: NetClient, vault: Vault, state: AppState,
    ) -> None:
        self._network = network
        self._vault   = vault
        self._state   = state

    async def _send_and_await(self, packet: Packet, success_type: MessageType) -> Packet:
        """Send a packet and wait for a response."""
        loop = asyncio.get_running_loop()
        fut  = loop.create_future()

        async def on_success(p: Packet) -> None:
            if not fut.done():
                fut.set_result(p)

        async def on_error(p: Packet) -> None:
            if not fut.done():
                reason = p.payload.get("reason", "Unknown error")
                fut.set_exception(ValueError(reason))

        self._network.on(success_type, on_success)
        self._network.on(MessageType.ERROR, on_error)

        try:
            ok = await self._network.send_packet(packet)

            if not ok:
                raise IOError("Failed to send packet to server.")

            return await fut

        finally:
            self._network.off(success_type)
            self._network.off(MessageType.ERROR)

    async def register(self, username: str, password: str) -> None:
        """Register a new account on the server."""
        log.info(f"Registering user {username}...")
        ctx = self._vault.load_or_create(username, password)

        with LocalHistory(username) as lh:
            lh.init_schema()

        spk_bytes = ctx.x25519_pub_bytes()
        sig_bytes = ctx._ed25519_priv.sign(spk_bytes)

        packet = Packet(
            MessageType.REGISTER,
            {
                "username" : username,
                "password" : password,
                "ik_pub"   : Envelope.b64_encode(ctx.ed25519_pub_bytes()),
                "spk"      : Envelope.b64_encode(spk_bytes),
                "spk_id"   : 1,
                "spk_sig"  : Envelope.b64_encode(sig_bytes),
                "opks"     : [],
            },
        )
        log.info(f"Sending registration packet: {packet}")

        await self._send_and_await(packet, MessageType.REGISTER_OK)
        log.info(f"Registration packet sent successfully.")

    async def login(self, username: str, password: str) -> CryptoContext:
        """Restore a user session and initialize identity keys."""
        packet = Packet(
            MessageType.LOGIN,
            {"username": username, "password": password},
        )

        response = await self._send_and_await(packet, MessageType.LOGIN_OK)
        remaining_opks = response.payload.get("remaining_opks", 0)

        ctx = self._vault.load_or_create(username, password)

        with LocalHistory(username) as lh:
            lh.init_schema()

        if remaining_opks < 10:
            spk_bytes = ctx.x25519_pub_bytes()
            sig_bytes = ctx._ed25519_priv.sign(spk_bytes)

            upload_packet = Packet(
                MessageType.REGISTER_PREKEYS,
                {
                    "spk"     : Envelope.b64_encode(spk_bytes),
                    "spk_id"  : 1,
                    "spk_sig" : Envelope.b64_encode(sig_bytes),
                    "opks"    : [],
                },
            )
            await self._send_and_await(upload_packet, MessageType.ACK)

        self._state.username = username
        self._state.password = password

        return ctx
