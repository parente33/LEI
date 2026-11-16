"""server/core/tls.py
TLS utilities for certificate generation and SSL context setup.

Provides helper functions to generate PKI material (certificates and keys)
and to construct a configured `SSLContext` for secure server communication.
"""

from __future__ import annotations

import ssl, datetime, ipaddress, logging
from pathlib import Path

from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.hazmat.primitives            import serialization
from cryptography.hazmat.primitives            import hashes
from cryptography.x509.oid                     import NameOID
from cryptography                              import x509

from server.config import SSL_CA_CERTFILE, SSL_SERVER_CERTFILE, SSL_KEYFILE

log = logging.getLogger(__name__)

project_root = Path(__file__).parent.parent.parent
certs_dir = project_root / "certs"


def generate_pki() -> None:
    """Generate a private Root CA and a Server Certificate."""

    certs_dir.mkdir(parents=True, exist_ok=True)

    ca_cert_path     = certs_dir / SSL_CA_CERTFILE
    server_cert_path = certs_dir / SSL_SERVER_CERTFILE
    server_key_path  = certs_dir / SSL_KEYFILE

    if (
            ca_cert_path     .exists()
        and server_cert_path .exists()
        and server_key_path  .exists()
    ):
        return

    log.info("Generating pinned private CA and server certificate...")

    # -- CA certificate builder ----------------------------------------------
    ca_key = rsa.generate_private_key(
        public_exponent=65537,
        key_size=2048
    )

    subject = issuer = x509.Name([
        x509.NameAttribute(NameOID.COMMON_NAME, "SSI.CHAT"),
    ])

    ca_cert = x509.CertificateBuilder().subject_name(
        subject
    ).issuer_name(
        issuer
    ).public_key(
        ca_key.public_key()
    ).serial_number(
        x509.random_serial_number()
    ).not_valid_before(
        datetime.datetime.now(datetime.timezone.utc)
    ).not_valid_after(
        datetime.datetime.now(datetime.timezone.utc) + datetime.timedelta(days=10)
    ).add_extension(
        x509.BasicConstraints(ca=True, path_length=None),
        critical=True,
    ).sign(ca_key, hashes.SHA256())

    # -- Server certificate builder ------------------------------------------
    server_key = rsa.generate_private_key(
        public_exponent=65537,
        key_size=2048
    )

    server_subject = x509.Name([
        x509.NameAttribute(NameOID.COMMON_NAME, "127.0.0.1"),
    ])

    server_cert = (x509.CertificateBuilder().subject_name(
        server_subject
    ).issuer_name(
        issuer  # the CA certicate signs the server certificate
    ).public_key(
        server_key.public_key()
    ).serial_number(
        x509.random_serial_number()
    ).not_valid_before(
        datetime.datetime.now(datetime.timezone.utc)
    ).not_valid_after(
        datetime.datetime.now(datetime.timezone.utc) + datetime.timedelta(days=365)
    ).add_extension(
        x509.SubjectAlternativeName([
            x509.DNSName("localhost"),
            x509.IPAddress(ipaddress.ip_address("127.0.0.1")),
        ]),
        critical=False,
    ).sign(ca_key, hashes.SHA256())
    )

    ca_cert_path.write_bytes(
        ca_cert.public_bytes(serialization.Encoding.PEM)
    )

    server_cert_path.write_bytes(
        server_cert.public_bytes(serialization.Encoding.PEM)
    )

    server_key_path.write_bytes(
        server_key.private_bytes(
            encoding=serialization.Encoding.PEM,
            format=serialization.PrivateFormat.TraditionalOpenSSL,
            encryption_algorithm=serialization.NoEncryption(),
        )
    )

    log.info("PKI generated. Certificates saved to %s", certs_dir)


def create_server_ssl_context(
    min_tls: ssl.TLSVersion = ssl.TLSVersion.TLSv1_3,
) -> ssl.SSLContext:

    context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
    context.minimum_version = min_tls

    cert_path = certs_dir / SSL_SERVER_CERTFILE
    key_path  = certs_dir / SSL_KEYFILE

    context.load_cert_chain(
        certfile=str(cert_path),
        keyfile=str(key_path)
    )

    return context
