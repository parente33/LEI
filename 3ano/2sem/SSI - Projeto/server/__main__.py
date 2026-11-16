"""server/__main__.py
Server application entry point.

Authentication is session-bound.  After LOGIN, all messages on that
connection are treated as coming from the authenticated user.
"""

from __future__ import annotations

import sys, asyncio, logging
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from server.core.dispatcher import Dispatcher
from server.core.registry   import ConnectionRegistry
from server.core.tls        import generate_pki, create_server_ssl_context
from server.core.db         import DB

from shared.logger   import setup_logging
from shared.config   import HOST, PORT
from shared.framing  import ProtocolError, read_packet, write_packet
from shared.protocol import MessageType

log = logging.getLogger(__name__)


async def _handle_client(
    reader: asyncio.StreamReader, writer: asyncio.StreamWriter,
    registry: ConnectionRegistry, dispatcher: Dispatcher,
) -> None:
    """Manage one client connection for its full lifetime.

    The loop reads packets, dispatches them, and writes responses.
    """
    addr     = writer.get_extra_info("peername")
    username : str | None = None

    log.info("connected %s", addr)

    try:
        while True:
            try:
                packet = await read_packet(reader)
                log.info(f"Received packet: {packet}")
            except asyncio.IncompleteReadError:
                break                                     # clean disconnect

            except ProtocolError as exc:
                log.warning("protocol error from %s: %s", addr, exc)
                break                                     # stream is unusable

            response = await dispatcher.dispatch(packet, username)

            # Bind the username to this connection on a successful LOGIN.
            if (
                    packet.type   == MessageType.LOGIN
                and response.type == MessageType.LOGIN_OK
            ):
                username = packet.payload.get("username", "")
                registry.register(username, writer)
                log.info("authenticated %s from %s", username, addr)

            log.info(f"Sending response: {response}")
            await write_packet(writer, response)

    except Exception as exc:
        log.error("unhandled error from %s: %s", addr, exc, exc_info=True)

    finally:
        if username:
            registry.unregister(username)

        writer.close()
        await writer.wait_closed()

        log.info("disconnected %s (%s)", addr, username or "unauthenticated")


async def main() -> None:
    setup_logging("server", level=logging.DEBUG)
    generate_pki()

    with DB() as db:
        db.init_schema()
        registry   = ConnectionRegistry()
        dispatcher = Dispatcher(db, registry)

        async def _on_connect(
            reader: asyncio.StreamReader,
            writer: asyncio.StreamWriter
        ) -> None:
            await _handle_client(reader, writer, registry, dispatcher)

        ssl_context = create_server_ssl_context()

        server = await asyncio.start_server(_on_connect, HOST, PORT, ssl=ssl_context)
        addrs  = ", ".join(str(s.getsockname()) for s in server.sockets)
        log.info("listening securely on %s", addrs)

        async with server:
            await server.serve_forever()


if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        log.info("shutting down")
