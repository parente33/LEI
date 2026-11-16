import sys, os, getpass
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.hazmat.primitives import hashes

KEY_SIZE = 32      # 256 bits
NONCE_SIZE = 16    # 128 bits
SALT_SIZE = 16     # 128 bits
ITERATIONS = 100_000

def derive_key(passphrase: bytes, salt: bytes) -> bytes:
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=KEY_SIZE,
        salt=salt,
        iterations=ITERATIONS,
    )
    return kdf.derive(passphrase)

def enc(file: str):
    passphrase = getpass.getpass("Passphrase: ").encode()

    with open(file, "rb") as f:
        plaintext = f.read()

    salt = os.urandom(SALT_SIZE)
    key = derive_key(passphrase, salt)
    nonce = os.urandom(NONCE_SIZE)

    cipher = Cipher(algorithms.ChaCha20(key, nonce), mode=None)
    encryptor = cipher.encryptor()
    ciphertext = encryptor.update(plaintext)

    with open(file + ".enc", "wb") as f:
        f.write(salt + nonce + ciphertext)

def dec(file: str):
    passphrase = getpass.getpass("Passphrase: ").encode()

    with open(file, "rb") as f:
        data = f.read()

    salt = data[:SALT_SIZE]
    nonce = data[SALT_SIZE:SALT_SIZE + NONCE_SIZE]
    ciphertext = data[SALT_SIZE + NONCE_SIZE:]

    key = derive_key(passphrase, salt)
    cipher = Cipher(algorithms.ChaCha20(key, nonce), mode=None)
    decryptor = cipher.decryptor()
    plaintext = decryptor.update(ciphertext)

    with open(file + ".dec", "wb") as f:
        f.write(plaintext)

def main(argv):
    if len(argv) < 3:
        print("Usage: python3 %s <enc|dec> <file>", argv[0])
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
