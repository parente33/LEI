"""server/core/db.py
SQLite persistence layer for the server.
"""

from __future__ import annotations

import sqlite3

from contextlib  import contextmanager
from dataclasses import dataclass

from pathlib import Path
from typing  import Any

from server.config import DB_FILENAME

project_root = Path(__file__).parent.parent
db_dir = project_root / "data"


@dataclass
class QueuedMessage:
    """Server-internal DTO for a message held in the offline queue."""

    msg_id        : str
    conv_id       : str
    to_user       : str
    from_user     : str
    timestamp     : int
    envelope_json : str


class DB:
    def __init__(self) -> None:
        db_dir.mkdir(parents=True, exist_ok=True)
        db_path = db_dir / DB_FILENAME

        self._conn = sqlite3.connect(
            db_path,
            check_same_thread=False
        )

        self._conn.row_factory = sqlite3.Row

        self._conn.execute("PRAGMA journal_mode=WAL")
        self._conn.execute("PRAGMA foreign_keys=ON")

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

    def __enter__(self) -> "DB":
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
            CREATE TABLE IF NOT EXISTS users (
                username  TEXT PRIMARY KEY,

                salt      BLOB NOT NULL,
                pw_hash   BLOB NOT NULL
            );

            CREATE TABLE IF NOT EXISTS prekey_bundles (
                username  TEXT PRIMARY KEY
                              REFERENCES users(username)
                              ON DELETE CASCADE,

                ik_pub    TEXT NOT NULL,
                spk       TEXT NOT NULL,
                spk_id    TEXT NOT NULL,
                spk_sig   TEXT NOT NULL
            );

            CREATE TABLE IF NOT EXISTS one_time_prekeys (
                username  TEXT NOT NULL
                              REFERENCES users(username)
                              ON DELETE CASCADE,

                otpk_id   TEXT NOT NULL,
                otpk      TEXT NOT NULL,

                PRIMARY KEY (username, otpk_id)
            );

            CREATE TABLE IF NOT EXISTS contacts (
                owner    TEXT NOT NULL
                             REFERENCES users(username)
                             ON DELETE CASCADE,

                contact  TEXT NOT NULL
                             REFERENCES users(username)
                             ON DELETE CASCADE,

                PRIMARY KEY (owner, contact)
            );

            CREATE TABLE IF NOT EXISTS conversations (
                conv_id  TEXT PRIMARY KEY,

                type     TEXT NOT NULL
                             CHECK(type IN ('direct', 'group'))
            );

            CREATE TABLE IF NOT EXISTS conv_members (
                conv_id  TEXT NOT NULL
                             REFERENCES conversations(conv_id)
                             ON DELETE CASCADE,

                username  TEXT NOT NULL
                             REFERENCES users(username)
                             ON DELETE CASCADE,

                PRIMARY KEY (conv_id, username)
            );

            CREATE TABLE IF NOT EXISTS message_queue (
                msg_id     TEXT PRIMARY KEY,
                conv_id    TEXT NOT NULL,

                to_user    TEXT NOT NULL
                               REFERENCES users(username)
                               ON DELETE CASCADE,

                from_user  TEXT NOT NULL
                               REFERENCES users(username)
                               ON DELETE CASCADE,

                timestamp  INTEGER NOT NULL,
                envelope   TEXT NOT NULL
            );

            -- GET_CONTACTS: WHERE owner = ?
            CREATE INDEX
                IF NOT EXISTS idx_contacts_owner
                ON contacts(owner);

            -- drain_queue: WHERE to_user = ? ORDER BY timestamp
            CREATE INDEX
                IF NOT EXISTS idx_queue_to_user_ts
                ON message_queue(to_user, timestamp);

            -- get_user_conversations: WHERE username = ?
            CREATE INDEX
                IF NOT EXISTS idx_conv_members_username
                ON conv_members(username);
        """)
        self._conn.commit()

    # ------------------------------------------------------------------------
    # Users
    # ------------------------------------------------------------------------

    def create_user(self, username: str, salt: bytes, pw_hash: bytes) -> None:
        """Insert a new user row."""
        self._conn.execute(
            """
            INSERT INTO users (
                username,
                salt,
                pw_hash
            ) VALUES (?, ?, ?)
            """,
            (username, salt, pw_hash),
        )

    def user_exists(self, username: str) -> bool:
        """Return True if username exists or False if not."""
        return (
            self._conn.execute(
                "SELECT 1 FROM users WHERE username = ?",
                (username,),
            ).fetchone() is not None
        )

    def get_user(self, username: str) -> sqlite3.Row | None:
        """Return the full users row for username, or None."""
        return self._conn.execute(
            """
            SELECT username, salt, pw_hash
            FROM users WHERE username = ?
            """,
            (username,),
        ).fetchone()

    # ------------------------------------------------------------------------
    # Prekey bundles & X3DH key directory
    # ------------------------------------------------------------------------

    def store_prekeys(
        self,
        username: str,
        ik_pub: str,
        spk: str,
        spk_id: str,
        spk_sig: str,
        otpks: list[dict[str, str]],
    ) -> None:
        """Store the user's prekey bundle and
        refresh their one-time prekeys pool.
        """
        self._conn.execute(
            """
            INSERT INTO prekey_bundles (
                username,
                ik_pub,
                spk,
                spk_id,
                spk_sig
            ) VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(username) DO UPDATE SET
                ik_pub  = excluded.ik_pub,
                spk     = excluded.spk,
                spk_id  = excluded.spk_id,
                spk_sig = excluded.spk_sig
            """,
            (username, ik_pub, spk, spk_id, spk_sig),
        )

        # Clear all one-time prekeys
        #self._conn.execute(
        #    "DELETE FROM one_time_prekeys WHERE username = ?",
        #    (username,),
        #)

        # Insert new one-time prekeys
        #self._conn.executemany(
        #    """
        #    INSERT OR REPLACE INTO one_time_prekeys (
        #        username,
        #        otpk_id,
        #        otpk
        #    ) VALUES (?, ?, ?)
        #    """,
        #    [
        #        (username, otpk["otpk_id"], otpk["otpk"])
        #        for otpk in otpks
        #    ],
        #)

    def update_prekeys(
        self,
        username: str,
        spk: str,
        spk_id: str,
        spk_sig: str,
        otpks: list[dict[str, str]],
    ) -> None:
        """Store the user's prekey bundle and
        refresh their one-time prekeys pool.
        """
        self._conn.execute(
            """
            UPDATE prekey_bundles
            SET    spk      = ?,
                   spk_id   = ?,
                   spk_sig  = ?
            WHERE  username = ?
            """,
            (spk, spk_id, spk_sig, username),
        )

        # Clear all one-time prekeys
        #self._conn.execute(
        #    "DELETE FROM one_time_prekeys WHERE username = ?",
        #    (username,),
        #)

        # Insert new one-time prekeys
        #self._conn.executemany(
        #    """
        #    INSERT OR REPLACE INTO one_time_prekeys (
        #        username,
        #        otpk_id,
        #        otpk
        #    ) VALUES (?, ?, ?)
        #    """,
        #    [
        #        (username, otpk["otpk_id"], otpk["otpk"])
        #        for otpk in otpks
        #    ],
        #)

    def dispense_prekey_bundle(self, username: str) -> dict[str, Any] | None:
        """Fetch a user's prekey bundle and dispense exactly one OPK."""

        bundle = self._conn.execute(
            """
            SELECT ik_pub, spk, spk_id, spk_sig  FROM prekey_bundles
            WHERE  username = ?
            """,
            (username,),
        ).fetchone()

        if not bundle:
            return None

        otpk = self._conn.execute(
            """
            SELECT otpk_id, otpk  FROM     one_time_prekeys
            WHERE  username = ?   ORDER BY otpk_id ASC
            LIMIT  1
            """,
            (username,),
        ).fetchone()

        # If an OPK is found, delete it atomically from the pool
        if otpk:
            self._conn.execute(
                """
                DELETE FROM  one_time_prekeys
                WHERE        username = ? AND otpk_id = ?
                """,
                (username, otpk["otpk_id"]),
            )

        return {
            "username"  : username,
            "ik_pub"    : bundle["ik_pub"],
            "spk"       : bundle["spk"],
            "spk_id"    : bundle["spk_id"],
            "spk_sig"   : bundle["spk_sig"],

            "otpk"    : otpk["otpk"]     if otpk else None,
            "otpk_id" : otpk["otpk_id"]  if otpk else None,
        }

    def get_remaining_opk_count(self, username: str) -> int:
        """Return the count of remaining one-time prekeys for the user."""
        row = self._conn.execute(
            """
            SELECT COUNT(*) AS count FROM one_time_prekeys
            WHERE username = ?
            """,
            (username,),
        ).fetchone()

        return row["count"] if row else 0

    # ------------------------------------------------------------------------
    # Contacts
    # ------------------------------------------------------------------------

    def add_contact(self, owner: str, contact: str) -> None:
        """Insert contact if not already present."""
        self._conn.execute(
            """
            INSERT OR IGNORE INTO contacts (
                owner,
                contact
            )
            VALUES (?, ?)
            """,
            (owner, contact),
        )

    def get_contacts(self, owner: str) -> list[str]:
        """Return every contact username for owner, sorted alphabetically."""
        rows = self._conn.execute(
            """
            SELECT  contact    FROM      contacts
            WHERE   owner = ?  ORDER BY  contact
            """,
            (owner,),
        )

        return [row["contact"] for row in rows]

    # ------------------------------------------------------------------------
    # Conversations
    # ------------------------------------------------------------------------

    def ensure_conversation(
        self, conv_id: str, conv_type: str, members: list[str]
    ) -> None:
        """Create the conversation and its member rows if they do not exist."""

        self._conn.execute(
            """
            INSERT OR IGNORE INTO conversations (
                conv_id,
                type
            ) VALUES (?, ?)
            """,
            (conv_id, conv_type),
        )

        self._conn.executemany(
            """
            INSERT OR IGNORE INTO conv_members (
                conv_id,
                username
            ) VALUES (?, ?)
            """,
            [(conv_id, username) for username in members],
        )

    # ------------------------------------------------------------------------
    # Message queue
    # ------------------------------------------------------------------------

    def enqueue_message(
        self,
        q_msg: QueuedMessage,
    ) -> None:
        """Insert one message into the offline queue."""

        self._conn.execute(
            """
            INSERT INTO message_queue (
                msg_id,
                conv_id,
                to_user,
                from_user,
                timestamp,
                envelope
            ) VALUES (?, ?, ?, ?, ?, ?)
            """,
            (
                q_msg.msg_id,
                q_msg.conv_id,
                q_msg.to_user,
                q_msg.from_user,
                q_msg.timestamp,
                q_msg.envelope_json,
            ),
        )

    def get_queued_message_count(self, username: str) -> int:
        """Return the count of queued messages for the user."""

        row = self._conn.execute(
            """
            SELECT COUNT(*) as count FROM message_queue
            WHERE  to_user = ?
            """,
            (username,),
        ).fetchone()

        return row["count"] if row else 0

    def fetch_queue(self, username: str) -> list[sqlite3.Row]:
        """Fetch all queued messages for username, ordered by timestamp."""
        return self._conn.execute(
            """
            SELECT msg_id, conv_id, from_user, timestamp, envelope
            FROM   message_queue
            WHERE  to_user = ?
            ORDER  BY timestamp ASC
            """,
            (username,),
        ).fetchall()

    def delete_queued_messages(self, username: str, msg_ids: list[str]) -> None:
        """Delete confirmed messages from the offline queue."""
        if not msg_ids:
            return

        placeholders = ",".join(["?"] * len(msg_ids))

        self._conn.execute(
            f"""
            DELETE FROM  message_queue
            WHERE        to_user = ?
            AND          msg_id IN ({placeholders})
            """,
            [username, *msg_ids],
        )
