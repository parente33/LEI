"""server/handlers/messages.py
Message routing and offline queue.

The server treats `Envelope` contents as opaque, reading only the routing
fields from the outer SEND_MSG payload (`to`, `envelope.conv_id`,
`envelope.sender`).  The ciphertext is never inspected or decoded.

Online delivery
---------------
If the recipient is online, the envelope is written directly to their writer.
If the recipient disconnects between the online check and the write), the
writer is unregistered and the message falls back to the offline queue.

Offline delivery
----------------
If the recipient is offline the envelope JSON is stored in `message_queue`.
It is delivered the next time the recipient sends FETCH_MSGS.
"""

from __future__ import annotations

import os, time, json, logging

from server.core.db import DB, QueuedMessage
from server.core.registry import ConnectionRegistry
from server.config import MIN_CONV_ID_LEN

from shared.framing import write_packet
from shared.protocol import MessageType, Packet

log = logging.getLogger(__name__)


async def handle_send(
    db: DB, registry: ConnectionRegistry, from_user: str, payload: dict
) -> Packet:
    """Route an encrypted envelope to its recipient."""
    to_user       = payload.get("to", "").strip()
    envelope_dict = payload.get("envelope")

    if not to_user or not isinstance(envelope_dict, dict):
        return Packet(
            MessageType.ERROR,
            {"reason": "Invalid payload: missing 'to' or 'envelope'"}
        )

    if to_user == from_user:
        return Packet(
            MessageType.ERROR,
            {"reason": "Cannot send a message to yourself"}
        )

    if not db.user_exists(to_user):
        return Packet(
            MessageType.ERROR,
            {"reason": f"User '{to_user}' does not exist"}
        )

    conv_id = envelope_dict.get("conv_id", "")
    if len(conv_id) < MIN_CONV_ID_LEN:
        return Packet(MessageType.ERROR, {"reason": "Invalid conv_id"})

    # Enforce that the envelope's sender field matches the authenticated user.
    envelope_sender = envelope_dict.get("sender", "")
    if envelope_sender != from_user:
        return Packet(
            MessageType.ERROR,
            {"reason": "Sender field mismatch"},
        )

    msg_id    = os.urandom(16).hex()
    timestamp = int(time.time())

    try:
        with db.transaction():
            db.ensure_conversation(conv_id, "direct", [from_user, to_user])

    except Exception as exc:
        log.warning("db.ensure_conversation failed: %s", exc)
        return Packet(
            MessageType.ERROR,
            {"reason": "Failed to initialize conversation"}
        )

    delivered = False
    if registry.is_online(to_user):
        try:
            await write_packet(
                registry.get(to_user),
                Packet(
                    MessageType.DELIVER_MSG,
                    {"envelope": envelope_dict}
                ),
            )
            delivered = True

        except OSError:
            # Unregister the dead writer and fall through to enqueue.
            registry.unregister(to_user)

    if not delivered:

        if db.get_queued_message_count(to_user) >= 1000:
            return Packet(
                MessageType.ERROR,
                {"reason": "Recipient's offline queue is full"}
            )

        try:
            with db.transaction():
                db.enqueue_message(
                    QueuedMessage(
                        msg_id=msg_id,
                        conv_id=conv_id,
                        to_user=to_user,
                        from_user=from_user,
                        timestamp=timestamp,
                        envelope_json=json.dumps(envelope_dict, separators=(',', ':')),
                    ),
                )

        except Exception as exc:
            log.warning("db.enqueue_message failed: %s", exc)
            return Packet(
                MessageType.ERROR,
                {"reason": "Failed to enqueue message"}
            )

    return Packet(MessageType.ACK, {"detail": "Message sent"})


async def handle_fetch(
    db: DB, registry: ConnectionRegistry, username: str
) -> Packet:
    """Fetch the offline queue for username."""

    rows      = db.fetch_queue(username)
    messages  : list[dict] = []

    for row in rows:
        try:
            messages.append({
                "msg_id"   : row["msg_id"],
                "envelope" : json.loads(row["envelope"]),
            })

        except (json.JSONDecodeError, KeyError):
            log.warning("corrupt queue entry for %s, skipping", username)

    for msg in messages:
        await write_packet(
            registry.get(username),
            Packet(
                MessageType.DELIVER_MSG,
                {"msg_id": msg["msg_id"], "envelope": msg["envelope"]}
            )
        )

    return Packet(
        MessageType.ACK,
        {"detail": f"Fetched {len(messages)} messages"},
    )


def handle_ack_msgs(db: DB, username: str, payload: dict) -> Packet:
    """Acknowledge receipt of queued messages
    by deleting them from database.
    """

    msg_ids = payload.get("msg_ids")
    if not isinstance(msg_ids, list):
        return Packet(
            MessageType.ERROR,
            {"reason": "Invalid payload: missing 'msg_ids' or not a list"}
        )

    try:
        with db.transaction():
            db.delete_queued_messages(username, msg_ids)

        return Packet(
            MessageType.ACK,
            {"detail": f"Successfully deleted {len(msg_ids)} messages"}
        )
    except Exception as exc:
        log.error(
            "Failed to delete acknowledged messages for %s: %s",
            username, exc, exc_info=True
        )
        return Packet(
            MessageType.ERROR,
            {"reason": "Failed deleting acknowledged messages"}
        )
