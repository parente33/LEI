"""client/crypto/vault.py
Scrypt-encrypted local key storage and Vault Service.

File format
-----------
    [32 bytes] Scrypt salt
    [12 bytes] ChaCha20-Poly1305 nonce
    [ N bytes] AEAD ciphertext of JSON payload (includes 16-byte Poly1305 tag)

The passphrase used for key derivation is the user's login password.
"""

from __future__ import annotations

import os, json
from pathlib import Path

from cryptography.hazmat.primitives.ciphers.aead import ChaCha20Poly1305
from cryptography.hazmat.primitives.kdf.scrypt   import Scrypt

from client.crypto.context import CryptoContext

_SCRYPT_N  = 2**13
_SCRYPT_R  = 8
_SCRYPT_P  = 1
_SALT_LEN  = 32
_NONCE_LEN = 12
_KEY_LEN   = 32
_VAULT_AAD = b"SSIChat-vault-v1"


def _derive_key(passphrase: str, salt: bytes) -> bytes:
    kdf = Scrypt(
        salt=salt,
        length=_KEY_LEN,
        n=_SCRYPT_N, r=_SCRYPT_R, p=_SCRYPT_P
    )

    return kdf.derive(passphrase.encode())


class Vault:
    """Manages local cryptographic vault files.

    Handles resolving vault paths, loading existing client contexts, generating
    new keys for new users, and atomic persistence of cryptographic sessions.
    """

    def __init__(self, data_dir: Path) -> None:
        """Initialize the vault service with a target storage directory."""
        self._data_dir = data_dir
        self._data_dir.mkdir(parents=True, exist_ok=True)

    def _get_vault_path(self, username: str) -> Path:
        """Return the target file path for a user's vault."""
        return self._data_dir / f"{username}.vault"

    def load_or_create(self, username: str, passphrase: str) -> CryptoContext:
        """Load the user's `CryptoContext` from their vault, or generate a new one."""
        vault_path = self._get_vault_path(username)

        if not vault_path.exists():
            ctx = CryptoContext.generate()
            self.save(username, passphrase, ctx)
            return ctx

        # Load existing vault
        raw   = vault_path.read_bytes()
        salt  = raw[:_SALT_LEN]
        nonce = raw[_SALT_LEN : _SALT_LEN + _NONCE_LEN]
        ct    = raw[_SALT_LEN + _NONCE_LEN :]

        key     = _derive_key(passphrase, salt)
        payload = ChaCha20Poly1305(key).decrypt(nonce, ct, _VAULT_AAD)
        data    = json.loads(payload.decode())

        return CryptoContext.from_state(data)

    def save(self, username: str, passphrase: str, context: CryptoContext) -> None:
        """Encrypt and persist the current cryptographic context to the vault file."""
        vault_path = self._get_vault_path(username)
        vault_path.parent.mkdir(parents=True, exist_ok=True)

        data    = context.dump_state()
        payload = json.dumps(data, separators=(",", ":")).encode()

        salt    = os.urandom(_SALT_LEN)
        nonce   = os.urandom(_NONCE_LEN)
        key     = _derive_key(passphrase, salt)
        ct      = ChaCha20Poly1305(key).encrypt(nonce, payload, _VAULT_AAD)

        tmp_path = vault_path.with_suffix(".vault.tmp")
        tmp_path.write_bytes(salt + nonce + ct)
        tmp_path.replace(vault_path)
