"""client/core/storage.py
Local message history database (client-side SQLite).

Decrypted plaintext is never sent to or stored by the server.  This module
persists messages on the user's own device after successful decryption by
the crypto layer.
"""

from __future__ import annotations

import sqlite3, time
from pathlib import Path
from contextlib import contextmanager

from client.core.models import Message

project_root = Path(__file__).parent.parent
db_dir = project_root / "data"


class LocalHistory:
    """Append-only local message store with deduplication."""

    def __init__(self, username: str) -> None:
        self._username = username

        self._db_path = db_dir / f"{self._username}.history.db"
        db_dir.parent.mkdir(parents=True, exist_ok=True)

        self._conn = sqlite3.connect(
            self._db_path,
            check_same_thread=False
        )

        self._conn.row_factory = sqlite3.Row
        self._conn.execute("PRAGMA journal_mode=WAL")

    # ------------------------------------------------------------------------
    # Transaction context
    # ------------------------------------------------------------------------

    @contextmanager
    def transaction(self):
        with self._conn:
            yield

    # ------------------------------------------------------------------------
    # Lifetime management
    # ------------------------------------------------------------------------

    def close(self) -> None:
        self._conn.close()

    def __enter__(self) -> "LocalHistory":
        return self

    def __exit__(self, *_: object) -> None:
        self.close()

    # ------------------------------------------------------------------------
    # Schema
    # ------------------------------------------------------------------------

    def init_schema(self) -> None:
        """Create all tables and indexes if they do not already exist."""

        self._conn.executescript(
        """
            CREATE TABLE IF NOT EXISTS messages (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                conv_id       TEXT    NOT NULL,
                sender        TEXT    NOT NULL,
                content       TEXT    NOT NULL,
                timestamp_ms  INTEGER NOT NULL,
                dedup_key     TEXT    NOT NULL DEFAULT ''
            );

            CREATE UNIQUE INDEX IF NOT EXISTS idx_messages_dedup
                ON messages(conv_id, dedup_key)
                WHERE dedup_key != '';

            CREATE INDEX IF NOT EXISTS idx_messages_conv
                ON messages(conv_id, timestamp_ms);
        """)

    # ------------------------------------------------------------------------
    # Write
    # ------------------------------------------------------------------------

    def save_message(
        self,
        conv_id: str, sender:    str,
        content: str, dedup_key: str = "",
    ) -> None:
        """Persist one decrypted message."""

        ts = int(time.time() * 1000)
        self._conn.execute(
        """
        INSERT OR IGNORE INTO messages
            (conv_id, sender, content, timestamp_ms, dedup_key)
        VALUES (?, ?, ?, ?, ?)
        """,
            (conv_id, sender, content, ts, dedup_key),
        )
        self._conn.commit()

    # ------------------------------------------------------------------------
    # Read
    # ------------------------------------------------------------------------

    def load_messages(
        self, conv_id: str, limit: int = 200
    ) -> list[Message]:
        """Load the most recent `limit` messages, returned oldest-first."""

        rows = self._conn.execute(
        """
        SELECT sender, content, timestamp_ms
        FROM (
            SELECT  id, sender, content, timestamp_ms
            FROM    messages
            WHERE   conv_id = ?
            ORDER   BY timestamp_ms DESC, id DESC
            LIMIT   ?
        )
        ORDER BY timestamp_ms ASC, id ASC
        """,
            (conv_id, limit),
        ).fetchall()

        messages = []

        for row in rows:
            sender = row["sender"]
            outbound = sender == self._username
            messages.append(
                Message(
                    sender=sender,
                    content=row["content"],
                    timestamp_ms=row["timestamp_ms"],
                    is_outbound=outbound,
                )
            )

        return messages
