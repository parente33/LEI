"""server/config.py
Server-only configuration.
"""

from __future__ import annotations

import yaml
from pathlib import Path

_cfg = yaml.safe_load((Path(__file__).parent / "config.yaml").read_text())

# -- Database ----------------------------------------------------------------
DB_FILENAME: str = _cfg["database"]["filename"]

# -- Auth --------------------------------------------------------------------
SCRYPT_N: int = _cfg["auth"]["scrypt_n"]
SCRYPT_R: int = _cfg["auth"]["scrypt_r"]
SCRYPT_P: int = _cfg["auth"]["scrypt_p"]
SALT_LEN: int = _cfg["auth"]["salt_len"]
HASH_LEN: int = _cfg["auth"]["hash_len"]

MIN_USERNAME_LEN: int = _cfg["auth"]["min_username_len"]
MAX_USERNAME_LEN: int = _cfg["auth"]["max_username_len"]
MIN_PASSWORD_LEN: int = _cfg["auth"]["min_password_len"]

# -- Keys --------------------------------------------------------------------
ED25519_PUB_LEN: int = _cfg["keys"]["ed25519_pub_len"]
X25519_PUB_LEN: int  = _cfg["keys"]["x25519_pub_len"]

# -- Messaging ---------------------------------------------------------------
MIN_CONV_ID_LEN: int = _cfg["messaging"]["min_conv_id_len"]

# -- SSL ---------------------------------------------------------------------
SSL_CA_CERTFILE: str = _cfg["ssl"]["ca_certfile"]
SSL_SERVER_CERTFILE: str = _cfg["ssl"]["server_certfile"]
SSL_KEYFILE: str  = _cfg["ssl"]["keyfile"]
