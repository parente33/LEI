import sys
import os
from cryptography.hazmat.primitives import hashes


def setup(file_name):
    key = os.urandom(32)
    with open(file_name, "wb") as f:
        f.write(key)


def mac(file, keyf):
    with open(file, "rb") as f:
        content = f.read()

    with open(keyf, "rb") as f:
        key = f.read()

    digest = hashes.Hash(hashes.SHA256())
    digest.update(key + content)
    mac = digest.finalize()

    output = file + ".mac"
    with open(output, "wb") as f:
        f.write(mac)


def ver(file, keyf):
    with open(file, "rb") as f:
        content = f.read()

    with open(keyf, "rb") as f:
        key = f.read()

    with open(file + ".mac", "rb") as f:
        mac_stored = f.read()

    digest = hashes.Hash(hashes.SHA256())
    digest.update(key + content)
    mac_computed = digest.finalize()

    if mac_computed == mac_stored:
        return True
    else:
        return False


def main(argv):
    if len(argv) < 2:
        print("Usage:\n")
        print("\tsetup <fkey>\n")
        print("\tmac <fich> <fkey>\n")
        print("\tver <fich> <fkey>\n")
        return

    op = argv[1]

    if op == "setup":
        if len(argv) != 3:
            print("Usage: %s setup <fkey>\n", argv[0])
            return
        
        file_name = argv[2]
        setup(file_name)

    if op == "mac":
        if len(argv) != 4:
            print("Usage: %s mac <fich> <fkey>\n", argv[0])
            return

        file = argv[2]
        keyf = argv[3]
        mac(file, keyf)

    if op == "ver":
        if len(argv) != 4:
            print("Usage: %s ver <fich> <fkey>\n", argv[0])
            return

        file = argv[2]
        keyf = argv[3]
        output = ver(file, keyf)
        print(output)

if __name__ == '__main__':
    main(sys.argv)
