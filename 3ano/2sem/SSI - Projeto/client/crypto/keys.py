"""client/crypto/keys.py
Key generation and serialisation for Ed25519 (signing)
and X25519 (key exchange).
"""

from __future__ import annotations

from cryptography.hazmat.primitives.asymmetric.ed25519 import (
    Ed25519PrivateKey, Ed25519PublicKey
)
from cryptography.hazmat.primitives.asymmetric.x25519 import (
    X25519PrivateKey, X25519PublicKey
)
from cryptography.hazmat.primitives.serialization import (
    Encoding, NoEncryption, PrivateFormat, PublicFormat
)


# ----------------------------------------------------------------------------
# Key generation
# ----------------------------------------------------------------------------

def generate_ed25519() -> tuple[Ed25519PrivateKey, Ed25519PublicKey]:
    """Generate a fresh Ed25519 identity keypair."""
    priv = Ed25519PrivateKey.generate()
    return priv, priv.public_key()


def generate_x25519() -> tuple[X25519PrivateKey, X25519PublicKey]:
    """Generate a fresh X25519 DH keypair."""
    priv = X25519PrivateKey.generate()
    return priv, priv.public_key()


# ----------------------------------------------------------------------------
# Serialisation  (key object to raw bytes)
# ----------------------------------------------------------------------------

def ed25519_priv_to_bytes(key: Ed25519PrivateKey) -> bytes:
    """Serialise an Ed25519 private key to 32 raw bytes."""
    return key.private_bytes(Encoding.Raw, PrivateFormat.Raw, NoEncryption())


def x25519_priv_to_bytes(key: X25519PrivateKey) -> bytes:
    """Serialise an X25519 private key to 32 raw bytes."""
    return key.private_bytes(Encoding.Raw, PrivateFormat.Raw, NoEncryption())


def ed25519_pub_to_bytes(key: Ed25519PublicKey) -> bytes:
    """Serialise an Ed25519 public key to 32 raw bytes."""
    return key.public_bytes(Encoding.Raw, PublicFormat.Raw)


def x25519_pub_to_bytes(key: X25519PublicKey) -> bytes:
    """Serialise an X25519 public key to 32 raw bytes."""
    return key.public_bytes(Encoding.Raw, PublicFormat.Raw)


# ----------------------------------------------------------------------------
# Deserialisation  (raw bytes to key object)
# ----------------------------------------------------------------------------

def ed25519_priv_from_bytes(data: bytes) -> Ed25519PrivateKey:
    """Load an Ed25519 private key from 32 raw bytes."""
    return Ed25519PrivateKey.from_private_bytes(data)


def x25519_priv_from_bytes(data: bytes) -> X25519PrivateKey:
    """Load an X25519 private key from 32 raw bytes."""
    return X25519PrivateKey.from_private_bytes(data)


def ed25519_pub_from_bytes(data: bytes) -> Ed25519PublicKey:
    """Load an Ed25519 public key from 32 raw bytes."""
    return Ed25519PublicKey.from_public_bytes(data)


def x25519_pub_from_bytes(data: bytes) -> X25519PublicKey:
    """Load an X25519 public key from 32 raw bytes."""
    return X25519PublicKey.from_public_bytes(data)
