"""server/core/registry.py
In-memory registry of authenticated connections.

The `ConnectionRegistry` is the server's complete picture of online state.  It
maps usernames to their active `StreamWriter` for direct message delivery.
"""

from __future__ import annotations
import asyncio


class ConnectionRegistry:
    """Maps authenticated usernames to their active connection."""

    def __init__(self) -> None:
        self._connections: dict[str, asyncio.StreamWriter] = {}

    def register(self, username: str, writer: asyncio.StreamWriter) -> None:
        """Record that username is now online via writer.

        If username is already registered (double-login), the previous
        writer is closed and replaced so only one connection is active.
        """
        existing = self._connections.get(username)
        if existing is not None and not existing.is_closing():
            existing.close()

        self._connections[username] = writer

    def unregister(self, username: str) -> None:
        """Remove username from the online set."""
        self._connections.pop(username, None)

    def get(self, username: str) -> asyncio.StreamWriter | None:
        """Return the writer for username."""
        return self._connections.get(username)

    def is_online(self, username: str) -> bool:
        """Return True if username has an active, non-closing connection."""
        writer = self._connections.get(username)
        return writer is not None and not writer.is_closing()
