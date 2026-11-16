import sys
import hashpumpy

def attack(file, ext):
    with open(file, "rb") as f:
        original_msg = f.read()

    with open(file + ".mac", "rb") as f:
        original_mac = f.read().hex()

    ext_bytes = ext.encode()

    for key_len in range(1, 65):
        new_mac, new_msg = hashpumpy.hashpump(
            original_mac,
            original_msg.decode('latin1'),
            ext,
            key_len
        )

        new_msg_bytes = new_msg.encode('latin1')
        new_mac_bytes = bytes.fromhex(new_mac)

        with open(file + ".ext", "wb") as f:
            f.write(new_msg_bytes)

        with open(file + ".ext.mac", "wb") as f:
            f.write(new_mac_bytes)

        print("Attack attempted with key length:", key_len)
        print("Extended files written")
        break


def main(argv):
    if len(argv) != 4:
        print("Usage: %s <fich> <ext>\n", argv[0])
        return

    file = argv[1]
    ext = argv[2]
    attack(file, ext)


if __name__ == '__main__':
    main(sys.argv)
