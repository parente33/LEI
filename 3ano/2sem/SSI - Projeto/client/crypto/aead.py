"""client/crypto/aead.py
ChaCha20-Poly1305 authenticated encryption.
"""

from __future__ import annotations

import os
from cryptography.hazmat.primitives.ciphers.aead import ChaCha20Poly1305


def encrypt(key: bytes, plaintext: bytes, aad: bytes) -> tuple[bytes, bytes]:
    """Encrypt plaintext under key with authenticated associated data."""
    nonce      = os.urandom(12)
    ciphertext = ChaCha20Poly1305(key).encrypt(nonce, plaintext, aad)
    return nonce, ciphertext


def decrypt(key: bytes, nonce: bytes, ciphertext: bytes, aad: bytes) -> bytes:
    """Decrypt and authenticate ciphertext."""
    return ChaCha20Poly1305(key).decrypt(nonce, ciphertext, aad)
