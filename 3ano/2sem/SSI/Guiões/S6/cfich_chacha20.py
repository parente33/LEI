import sys, os

from cryptography.hazmat.primitives.ciphers import Cipher, algorithms

KEY_SIZE = 32 # 256 bits
NONCE_SIZE = 16 # 128 bits
# Ambos os valores foram retirados da documentação oficial


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
        message = f.read()

    nonce = b'\x00' * 16
    cipher = Cipher(algorithms.ChaCha20(key, nonce), mode = None)
    encryptor = cipher.encryptor()
    ciphertext = encryptor.update(message)

    with open(file + ".enc", "wb") as f:
        f.write(nonce + ciphertext)


def dec(file, key_file):
    with open(key_file, "rb") as f:
        key = f.read()

    if len(key) != KEY_SIZE:
        raise ValueError("Invalid key size")

    with open(file, "rb") as f:
        message = f.read()

    nonce = b'\x00' * 16
    ciphertext = message[NONCE_SIZE:]

    cipher = Cipher(algorithms.ChaCha20(key, nonce), mode = None)
    decryptor = cipher.decryptor()
    plaintext = decryptor.update(ciphertext)

    with open(file + ".dec", "wb") as f:
        f.write(plaintext)


def main(argv:list[str]):
    op_type = argv[1]

    if op_type == "setup":
        key_file = argv[2]
        
        setup(key_file)

    elif op_type == "enc":
        file = argv[2]
        key_file = argv[3]
        
        enc(file, key_file)

    elif op_type == "dec":
        file = argv[2]
        key_file = argv[3]

        dec(file, key_file)

    else:
        print("Operation not supported\n")
        return

if __name__ == "__main__":
    main(sys.argv)
