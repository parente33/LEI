import sys

NONCE_SIZE = 16

def main(argv: list[str]):

    if len(argv) != 5:
        print("Usage: python3 %s <fctxt> <pos> <ptxtAtPos> <newPtxtAtPos>", argv[0])
        return

    fctxt = argv[1]
    pos = int(argv[2])
    ptxt = argv[3].encode()
    new_ptxt = argv[4].encode()

    if len(ptxt) != len(new_ptxt):
        raise ValueError("ptxtAtPos and newPtxtAtPos must have the same length")

    with open(fctxt, "rb") as f:
        data = bytearray(f.read())

    nonce = data[:NONCE_SIZE] # Igual ao usado no outro script
    ciphertext = data[NONCE_SIZE:]
    
    if pos + len(ptxt) > len(ciphertext):
        raise ValueError("Position out of bounds")
    
    for i in range(len(ptxt)):
        idx = pos + i
        ciphertext[idx] ^= ptxt[i] ^ new_ptxt[i]

    modified = nonce + ciphertext

    with open(fctxt + ".attck", "wb") as f:
        f.write(modified)


if __name__ == "__main__":
    main(sys.argv)
