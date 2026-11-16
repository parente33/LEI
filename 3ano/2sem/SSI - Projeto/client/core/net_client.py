"""client/core/net_client.py
Manages the TCP/TLS net_client lifecycle.
"""

from __future__ import annotations

import ssl, asyncio, logging

from typing  import Any
from pathlib import Path

from collections.abc import Callable, Coroutine

from shared.framing  import ProtocolError, read_packet, write_packet
from shared.protocol import MessageType,   Packet

log = logging.getLogger(__name__)

MessageCallback = Callable[[Packet], Coroutine[Any, Any, None]]


class NetClient:
    """Manages the lifecycle of a TCP (+ TLS) connection to the server."""

    def __init__(self, host: str, port: int) -> None:
        self._host = host
        self._port = port

        self._reader: asyncio.StreamReader | None = None
        self._writer: asyncio.StreamWriter | None = None

        self._recv_task: asyncio.Task[None] | None          = None
        self._callbacks: dict[MessageType, MessageCallback] = {}

        self._on_disconnect_cb: Callable[
            [], Coroutine[Any, Any, None]
        ] | None = None

    # ------------------------------------------------------------------------
    # State query
    # ------------------------------------------------------------------------

    @property
    def connected(self) -> bool:
        """Indicate whether the connection to the server is active."""
        return self._writer is not None and not self._writer.is_closing()

    # ------------------------------------------------------------------------
    # Callback registration
    # ------------------------------------------------------------------------

    def on(self, msg_type: MessageType, callback: MessageCallback) -> None:
        """Register a callback for an incoming message type."""
        self._callbacks[msg_type] = callback

    def off(self, msg_type: MessageType) -> None:
        """Unregister a callback."""
        self._callbacks.pop(msg_type, None)

    def on_disconnect(
        self, callback: Callable[[], Coroutine[Any, Any, None]]
    ) -> None:
        """Register a callback invoked when the connection closes."""
        self._on_disconnect_cb = callback

    # ------------------------------------------------------------------------
    # Connection management
    # ------------------------------------------------------------------------

    async def connect(self, ssl_ctx: Any = None) -> None:
        """Open a TCP connection wrapped in TLS."""

        if self.connected:
            log.info("Client is already connected")
            return

        if ssl_ctx is None:
            project_root = Path(__file__).parent.parent.parent
            ca_path      = project_root / "certs" / "server.crt"

            if ca_path.exists():
                ssl_ctx = ssl.create_default_context(
                    ssl.Purpose.SERVER_AUTH,
                    cafile=ca_path
                )

                ssl_ctx.check_hostname = True
                ssl_ctx.verify_mode    = ssl.CERT_REQUIRED

                log.info("TLS context loaded from %s", ca_path)

        self._reader, self._writer = await asyncio.open_connection(
            self._host, self._port, ssl=ssl_ctx
        )

        self._recv_task = asyncio.create_task(
            self._recv_loop(), name="net_client-recv"
        )

        log.info("connected to %s:%s", self._host, self._port)

    async def send_packet(self, packet: Packet) -> bool:
        """Send a packet."""
        if self._writer is None or self._writer.is_closing():
            log.warning("Connection closed, no Packet sent")
            return False

        try:
            await write_packet(self._writer, packet)
            return True

        except OSError:
            log.warning(
                "Failed to send packet of type %s",
                packet.type, exc_info=True
            )
            return False

    async def disconnect(self) -> None:
        """Gracefully shut down the receive loop and close the connection."""

        if self._recv_task and not self._recv_task.done():
            self._recv_task.cancel()
            try:
                await self._recv_task
            except asyncio.CancelledError:
                pass

        if self._writer:
            self._writer.close()
            try:
                await self._writer.wait_closed()
            except OSError:
                pass

        self._reader = None
        self._writer = None

        self._recv_task = None

        log.info("disconnected from %s:%s", self._host, self._port)

    # ------------------------------------------------------------------------
    # Internal receive loop
    # ------------------------------------------------------------------------

    async def _recv_loop(self) -> None:
        """Read packets continuously and dispatch to registered callbacks."""
        try:
            while True:
                packet   = await read_packet(self._reader)
                callback = self._callbacks.get(packet.type)

                if callback is not None:
                    try:
                        await callback(packet)

                    except Exception:
                        log.warning(
                            "callback error for %s",
                            packet.type, exc_info=True
                        )

        except asyncio.IncompleteReadError:
            log.info("remote peer closed the connection")

        except asyncio.CancelledError:
            raise  # let disconnect() cancel normally

        except ProtocolError:
            log.warning(
                "protocol error while receiving packets",
                exc_info=True
            )

        finally:
            if self._on_disconnect_cb is not None:
                try:
                    await self._on_disconnect_cb()
                except Exception:
                    log.warning("disconnect callback failed", exc_info=True)
