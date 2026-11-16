"""server/handlers/keys.py
Public key and prekey bundle upload and retrieval for X3DH.
"""

from __future__ import annotations

import logging

from cryptography.hazmat.primitives.asymmetric import ed25519
from cryptography.exceptions                   import InvalidSignature

from server.core.db  import DB
from shared.protocol import MessageType, Packet, Envelope

log = logging.getLogger(__name__)


class PrekeyRegistrationError(Exception):
    """
    Base exception for all prekey registration failures, including
    validation errors, signature verification failures, and storage-related
    issues that occur during the prekey provisioning process.
    """
    pass


def register_prekeys(db: DB, username: str, payload: dict) -> None:
    """Validate the payload, verify the signed prekey signature,
    and store the bundle.
    """

    ik_pub  = payload.get("ik_pub")
    spk     = payload.get("spk")
    spk_id  = payload.get("spk_id")
    spk_sig = payload.get("spk_sig")
    otpks   = payload.get("otpks")

    is_refresh = False
    if not ik_pub:
        row = db._conn.execute(
            "SELECT ik_pub FROM prekey_bundles WHERE username = ?", (username,)
        ).fetchone()

        if not row:
            raise PrekeyRegistrationError("Missing ik_pub and user not found")

        ik_pub     = row["ik_pub"]
        is_refresh = True

    if not isinstance(ik_pub, str) or not ik_pub:
        raise PrekeyRegistrationError("Missing or invalid ik_pub")

    if not isinstance(spk_id, int):
        raise PrekeyRegistrationError("Missing or invalid spk_id")

    if not isinstance(spk, str) or not spk:
        raise PrekeyRegistrationError("Missing or invalid spk")

    if not isinstance(spk_sig, str) or not spk_sig:
        raise PrekeyRegistrationError("Missing or invalid spk_sig")

    #if not isinstance(otpks, list):
    #    raise PrekeyRegistrationError("Missing or invalid otpks list")

    #for opk in otpks:
    #    if (
    #        not isinstance(opk, dict)
    #        or "otpk_id" not in opk
    #        or "otpk"    not in opk
    #    ):
    #        raise PrekeyRegistrationError(
    #            "Invalid one-time prekey entry format"
    #        )

    try:
        ik_bytes  = Envelope.b64_decode(ik_pub)
        spk_bytes = Envelope.b64_decode(spk)
        sig_bytes = Envelope.b64_decode(spk_sig)

        pub_key = ed25519.Ed25519PublicKey.from_public_bytes(ik_bytes)
        pub_key.verify(sig_bytes, spk_bytes)

    except (InvalidSignature, ValueError):
        raise PrekeyRegistrationError("Invalid spk_sig")

    except Exception as exc:
        log.warning(
            "Signature validation failed for %s: %s",
            username, exc
        )
        raise PrekeyRegistrationError(
            "Invalid cryptographic key or signature format"
        )

    try:
        if is_refresh:
            db.update_prekeys(
                username=username,
                spk=spk,
                spk_id=spk_id,
                spk_sig=spk_sig,
                otpks=otpks,
            )
        else:
            db.store_prekeys(
                username=username,
                ik_pub=ik_pub,
                spk=spk,
                spk_id=spk_id,
                spk_sig=spk_sig,
                otpks=otpks,
            )

    except ValueError:
        raise

    except Exception as exc:
        log.error(
            "Failed to store prekeys for %s: %s",
            username, exc, exc_info=True
        )
        raise PrekeyRegistrationError("Database failure registering prekeys")


def handle_register_prekeys(db: DB, username: str, payload: dict) -> Packet:
    """Store the user's core prekey bundle and one-time prekeys pool."""

    try:
        with db.transaction():
            register_prekeys(db, username, payload)

        log.info("Registered prekeys bundle for %s", username)
        return Packet(
            MessageType.ACK,
            {"detail": "Prekey bundle registered successfully"}
        )

    except PrekeyRegistrationError as exc:
        return Packet(MessageType.ERROR, {"reason": str(exc)})

    except Exception as exc:
        log.error(
            "Unexpected error registering prekeys for %s: %s",
            username, exc, exc_info=True
        )
        return Packet(
            MessageType.ERROR,
            {"reason": "Failed to register prekeys"}
        )


def handle_get_prekey_bundle(db: DB, payload: dict) -> Packet:
    """Retrieve the public prekey bundle for a requested username,
    dispensing one OPK.
    """

    target = payload.get("username", "").strip()

    if not target or not db.user_exists(target):
        return Packet(
            MessageType.ERROR,
            {"reason": f"User '{target}' does not exist"}
        )

    try:
        with db.transaction():
            bundle = db.dispense_prekey_bundle(target)

        if bundle is None:
            return Packet(
                MessageType.ERROR,
                {"reason": f"No prekey bundle found for '{target}'"}
            )

        return Packet(MessageType.PREKEY_BUNDLE, bundle)

    except Exception as exc:
        log.error(
            "Failed to dispense prekey bundle for %s: %s",
            target, exc, exc_info=True
        )
        return Packet(
            MessageType.ERROR,
            {"reason": f"Failed to dispense prekey bundle for '{target}"}
        )
