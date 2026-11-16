"""shared/framing.py
Length-prefixed `Packet` framing for asyncio streams.
"""

from __future__ import annotations

import struct, json, asyncio, logging
from asyncio import StreamReader, StreamWriter

from shared.config   import MAX_PACKET_BYTES
from shared.protocol import MessageType, Packet

log = logging.getLogger(__name__)


# ----------------------------------------------------------------------------
# Framing constants
# ----------------------------------------------------------------------------

_LENGTH_FMT  = "!I"                          # network-order unsigned 32-bit
_LENGTH_SIZE = struct.calcsize(_LENGTH_FMT)  # 4 bytes


# ----------------------------------------------------------------------------
# ProtocolError
# ----------------------------------------------------------------------------

class ProtocolError(Exception):
    """Raised when the wire format is violated or a packet cannot be decoded.

    Any `ProtocolError` leaves the stream in an undefined state because the
    read position is unknown.  The connection must be closed immediately,
    partial reads cannot be resumed.
    """
    pass


# ----------------------------------------------------------------------------
# Codec
# ----------------------------------------------------------------------------

def encode(packet: Packet) -> bytes:
    """Serialise a Packet to length-prefixed bytes."""
    body = json.dumps(
        {
            "type"    : packet.type.value,
            "payload" : packet.payload
        },
        separators=(',', ':'),
        ensure_ascii=True,
    ).encode()

    body_len = len(body)
    if body_len > MAX_PACKET_BYTES:
        raise ProtocolError(
            f"Packet too large: {body_len:,} bytes (max {MAX_PACKET_BYTES:,})"
        )

    return struct.pack(_LENGTH_FMT, body_len) + body


def decode(data: bytes) -> Packet:
    """Deserialise a `Packet` to bytes removing the length prefix."""
    try:
        obj: dict = json.loads(data.decode())
    except (json.JSONDecodeError, UnicodeDecodeError) as exc:
        raise ProtocolError(f"Malformed packet body: {exc}") from exc

    if "type" not in obj:
        raise ProtocolError("Packet missing required 'type' field")

    try:
        msg_type = MessageType(obj["type"])
    except ValueError as exc:
        raise ProtocolError(f"Unknown message type: {obj['type']!r}") from exc

    payload = obj.get("payload", {})
    if not isinstance(payload, dict):
        raise ProtocolError("Packet 'payload' must be a JSON object")

    return Packet(type=msg_type, payload=payload)


# ----------------------------------------------------------------------------
# Async I/O layer
# ----------------------------------------------------------------------------

async def read_packet(reader: StreamReader) -> Packet:
    """Read exactly one `Packet` from `reader`."""
    bytes_len = await reader.readexactly(_LENGTH_SIZE)

    (body_len,) = struct.unpack(_LENGTH_FMT, bytes_len)

    if body_len == 0:
        raise ProtocolError("Zero-length packet body is not allowed")

    if body_len > MAX_PACKET_BYTES:
        raise ProtocolError(
            f"Packet too large: {body_len:,} bytes (max {MAX_PACKET_BYTES:,})"
        )

    body = await reader.readexactly(body_len)

    return decode(body)


async def write_packet(writer: StreamWriter, packet: Packet) -> None:
    """Encode a `Packet` and write it to `writer`."""

    data = encode(packet)
    writer.write(data)
    await writer.drain()
