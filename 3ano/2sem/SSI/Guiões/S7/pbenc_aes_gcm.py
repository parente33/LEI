import sys, os, getpass
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.hazmat.primitives import hashes


def derive_key(passphrase: bytes, salt: bytes) -> bytes:
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        iterations=100000,
    )
    return kdf.derive(passphrase)


def enc(file: str):
    passphrase = getpass.getpass("Passphrase: ").encode()

    with open(file, "rb") as f:
        plaintext = f.read()

    salt = os.urandom(16)
    key = derive_key(passphrase, salt)
    nonce = os.urandom(12)

    cipher = Cipher(algorithms.AES(key), modes.GCM(nonce))
    encryptor = cipher.encryptor()

    ciphertext = encryptor.update(plaintext) + encryptor.finalize()
    tag = encryptor.tag

    with open(file + ".enc", "wb") as f:
        f.write(salt + nonce + ciphertext + tag)


def dec(file: str):
    passphrase = getpass.getpass("Passphrase: ").encode()

    with open(file, "rb") as f:
        data = f.read()

    salt = data[:16]
    nonce = data[16:16 + 12]
    tag = data[-16:]
    ciphertext = data[16 + 12:-16]

    key = derive_key(passphrase, salt)

    cipher = Cipher(algorithms.AES(key), modes.GCM(nonce, tag))
    decryptor = cipher.decryptor()

    try:
        plaintext = decryptor.update(ciphertext) + decryptor.finalize()
    except:
        print("FAIL")
        return

    with open(file + ".dec", "wb") as f:
        f.write(plaintext)


def main(argv):
    if len(argv) < 3:
        print("Usage: python3 %s <enc|dec> <file>" % argv[0])
        return

    op = argv[1]
    file = argv[2]

    if op == "enc":
        enc(file)
    elif op == "dec":
        dec(file)
    else:
        print("Operation not supported")


if __name__ == "__main__":
    main(sys.argv)
