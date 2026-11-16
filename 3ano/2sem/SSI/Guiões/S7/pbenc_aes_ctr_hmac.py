import sys, os, getpass
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.hazmat.primitives import hashes, hmac


def derive_keys(passphrase: bytes, salt: bytes):
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32 + 32, # key size and mac key size respectively
        salt=salt,
        iterations=100000,
    )
    key_material = kdf.derive(passphrase)
    return key_material[:32], key_material[32:]



def enc(file: str):
    passphrase = getpass.getpass("Passphrase: ").encode()

    with open(file, "rb") as f:
        plaintext = f.read()

    salt = os.urandom(16)
    key_enc, key_mac = derive_keys(passphrase, salt)
    nonce = os.urandom(16)

    cipher = Cipher(algorithms.AES(key_enc), modes.CTR(nonce))
    encryptor = cipher.encryptor()
    ciphertext = encryptor.update(plaintext)

    # encrypt-then-MAC → MAC sobre (salt || nonce || ciphertext)
    h = hmac.HMAC(key_mac, hashes.SHA256())
    h.update(salt + nonce + ciphertext)
    tag = h.finalize()

    with open(file + ".enc", "wb") as f:
        f.write(salt + nonce + ciphertext + tag)


def dec(file: str):
    passphrase = getpass.getpass("Passphrase: ").encode()

    with open(file, "rb") as f:
        data = f.read()

    salt = data[:16]
    nonce = data[16:16 + 16]
    tag = data[-32:]  # SHA256 → 32 bytes
    ciphertext = data[16 + 16:-32]

    key_enc, key_mac = derive_keys(passphrase, salt)

    # verificar MAC antes de decifrar
    h = hmac.HMAC(key_mac, hashes.SHA256())
    h.update(salt + nonce + ciphertext)
    try:
        h.verify(tag)
    except:
        print("FAIL")
        return

    cipher = Cipher(algorithms.AES(key_enc), modes.CTR(nonce))
    decryptor = cipher.decryptor()
    plaintext = decryptor.update(ciphertext)

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
