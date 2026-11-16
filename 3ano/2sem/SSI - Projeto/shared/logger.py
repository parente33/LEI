"""shared/logger.py
Logging setup for both the server and the client.
"""

from __future__ import annotations

import sys, logging
from pathlib import Path

_FORMAT = "%(asctime)s [%(name)s] %(levelname)s %(message)s"
_DATE   = "%H:%M:%S"


def setup_logging(
    app_name: str,
    *,
    level: int = logging.DEBUG,
    log_file: Path | None = None,
) -> None:
    """Configure application logging."""

    handler: logging.Handler

    if log_file is not None:
        handler = logging.FileHandler(log_file, encoding="utf-8")
    else:
        handler = logging.StreamHandler(sys.stderr)

    handler.setFormatter(logging.Formatter(_FORMAT, datefmt=_DATE))

    root = logging.getLogger()
    root.handlers.clear()

    root.setLevel(level)
    root.addHandler(handler)
