"""shared/config.py
Shared configuration.
"""

from __future__ import annotations

import yaml
from pathlib import Path

_cfg = yaml.safe_load((Path(__file__).parent / "config.yaml").read_text())

HOST: str = _cfg["network"]["host"]
PORT: int = _cfg["network"]["port"]

MAX_PACKET_BYTES: int = _cfg["protocol"]["max_packet_bytes"]
