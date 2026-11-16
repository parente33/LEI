"""server/handlers/auth.py
Registration and login.

Password hashing
----------------
Passwords are hashed using Scrypt with a per-user random salt.
The derived key is stored as `pw_hash`.  Plaintext passwords
are never stored or persisted.
"""

from __future__ import annotations

import os, logging

from cryptography.hazmat.primitives.kdf.scrypt import Scrypt
from cryptography.exceptions                   import InvalidKey

from server.config import (
    HASH_LEN,
    MIN_USERNAME_LEN, MAX_USERNAME_LEN,
    SALT_LEN, MIN_PASSWORD_LEN,
    SCRYPT_N, SCRYPT_P, SCRYPT_R,
)

from server.handlers.keys import PrekeyRegistrationError, register_prekeys
from server.core.db       import DB

from shared.protocol import MessageType, Packet

log = logging.getLogger(__name__)


def _derive_scrypt(password: str, salt: bytes) -> bytes:
    """Return a derived hash from a password and salt."""

    kdf = Scrypt(
        salt=salt, length=HASH_LEN,
        n=SCRYPT_N, r=SCRYPT_R, p=SCRYPT_P
    )

    return kdf.derive(password.encode())


def _verify_scrypt(password: str, salt: bytes, stored_hash: bytes) -> bool:
    """Return True if password matches `stored_hash`."""

    kdf = Scrypt(
        salt=salt, length=HASH_LEN,
        n=SCRYPT_N, r=SCRYPT_R, p=SCRYPT_P
    )

    try:
        kdf.verify(password.encode(), stored_hash)
        return True
    except InvalidKey:
        return False


def _validate_credentials(payload: dict) -> tuple[str, str] | None:
    """Extract and validate username/password."""

    username = payload.get("username", "")
    password = payload.get("password", "")

    if (
        not isinstance(username, str) or
        not isinstance(password, str)
    ):
        return None

    if (
        not (MIN_USERNAME_LEN <= len(username) <= MAX_USERNAME_LEN)
    ):
        return None

    if len(password) < MIN_PASSWORD_LEN:
        return None

    return username, password


def handle_register(db: DB, payload: dict) -> Packet:
    """Register a user and store his initial X3DH bundle."""
    creds = _validate_credentials(payload)

    if creds is None:
        return Packet(
            MessageType.ERROR,
            {"reason": "Invalid username or password"},
        )

    username, password = creds

    if db.user_exists(username):
        return Packet(
            MessageType.ERROR,
            {"reason": "Username already taken"}
        )

    salt    = os.urandom(SALT_LEN)
    pw_hash = _derive_scrypt(password, salt)

    try:
        with db.transaction():
            db.create_user(username, salt, pw_hash)
            register_prekeys(db, username, payload)

    except PrekeyRegistrationError as exc:
        return Packet(
            MessageType.ERROR,
            {"reason": str(exc)}
        )

    except Exception as exc:
        log.error(
            "Unexpected registration failure for %s: %s",
            username, exc, exc_info=True
        )
        return Packet(
            MessageType.ERROR,
            {"reason": "Internal server error"}
        )

    log.info(f"User {username} registered successfully.")

    remaining_opks = db.get_remaining_opk_count(username)
    return Packet(
        MessageType.REGISTER_OK,
        {"remaining_opks": remaining_opks}
    )


def handle_login(db: DB, payload: dict) -> Packet:
    """Authenticate a user."""
    creds = _validate_credentials(payload)

    if creds is None:
        return Packet(
            MessageType.ERROR,
            {"reason": "Invalid credentials"}
        )

    username, password = creds
    row = db.get_user(username)

    # Always run Scrypt, even for an unknown user, to prevent
    # username enumeration via response-time differences.
    row = row or {
        "salt"    : b"\x00" * SALT_LEN,
        "pw_hash" : b"\x00" * HASH_LEN,
    }

    salt        = row["salt"]
    stored_hash = row["pw_hash"]

    pw_ok = _verify_scrypt(password, salt, stored_hash)

    if row is None or not pw_ok:
        return Packet(
            MessageType.ERROR,
            {"reason": "Invalid username or password"}
        )

    remaining_opks = db.get_remaining_opk_count(username)
    return Packet(
        MessageType.LOGIN_OK,
        {"remaining_opks": remaining_opks}
    )
