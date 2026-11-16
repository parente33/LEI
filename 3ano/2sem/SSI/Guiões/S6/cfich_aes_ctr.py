import sys, os
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes

KEY_SIZE = 32      # 256 bits
NONCE_SIZE = 16

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

    nonce = os.urandom(NONCE_SIZE)

    cipher = Cipher(algorithms.AES(key), modes.CTR(nonce))
    encryptor = cipher.encryptor()
    ciphertext = encryptor.update(plaintext) + encryptor.finalize()

    with open(file + ".enc", "wb") as f:
        f.write(nonce + ciphertext)


def dec(file, key_file):
    with open(key_file, "rb") as f:
        key = f.read()

    if len(key) != KEY_SIZE:
        raise ValueError("Invalid key size")

    with open(file, "rb") as f:
        data = f.read()

    nonce = data[:NONCE_SIZE]
    ciphertext = data[NONCE_SIZE:]

    cipher = Cipher(algorithms.AES(key), modes.CTR(iv))
    decryptor = cipher.decryptor()
    plaintext = decryptor.update(ciphertext) + decryptor.finalize()

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
