import sys, os
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives import padding

KEY_SIZE = 32      # 256 bits
BLOCK_SIZE = 16    # AES block size (bytes)

def setup(key_file):
    key = os.urandom(KEY_SIZE)
    with open(key_file, "wb") as f:
        f.write(key)


def enc(file, key_file):
    with open(key_file, "rb") as f:
        key = f.read()

    if len(key) != KEY_SIZE:
        raise ValueError("Invalid key size")

    with open(file, "rb") as f:
        plaintext = f.read()

    padder = padding.PKCS7(128).padder()
    padded = padder.update(plaintext) + padder.finalize()

    iv = os.urandom(BLOCK_SIZE)

    cipher = Cipher(algorithms.AES(key), mode.CBC(iv))
    encryptor = cipher.encryptor()
    ciphertext = encryptor.update(padded) + encryptor.finalize()

    with open(file + ".enc", "wb") as f:
        f.write(iv + ciphertext)


def dec(file, key_file):
    with open(key_file, "rb") as f:
        key = f.read()

    if len(key) != KEY_SIZE:
        raise ValueError("Invalid key size")

    with open(file, "rb") as f:
        data = f.read()

    iv = data[:BLOCK_SIZE]
    ciphertext = data[BLOCK_SIZE:]

    cipher = Cipher(algorithms.AES(key), modes.CBC(iv))
    decryptor = cipher.decryptor()
    padded = decryptor.update(ciphertext) + decryptor.finalize()

    unpadder = padding.PKCS7(128).unpadder()
    plaintext = unpadder.update(padded) + unpadder.finalize()

    with open(file + ".dec", "wb") as f:
        f.write(plaintext)


def main(argv):
    op = argv[1]

    if op == "setup":
        setup(argv[2])
    elif op == "enc":
        enc(argv[2], argv[3])
    elif op == "dec":
        dec(argv[2], argv[3])
    else:
        print("Operation not supported")


if __name__ == "__main__":
    main(sys.argv)
