import sys

def prepoc(str):
    l = []
    for c in str:
        if c.isalpha():
            l.append(c.upper())
    return "".join(l)

def match_length(s, target):
    if len(s) == 0:
        return ""  # avoid division by zero
    return (s * (len(target) // len(s) + 1))[:len(target)]

def encode(k: str, m: str):
    aux = []

    for i in range(len(m)):
        value = ord(m[i])

        pos_in_alphabet = value - ord('A')
        pos_in_alphabet += (ord(k[i]) - ord('A'))
        pos_in_alphabet = pos_in_alphabet % 26

        final_letter = chr(pos_in_alphabet + ord('A'))
        aux.append(final_letter)
    return aux

def decode(k: str, m: str):
    aux = []

    for i in range(len(m)):
        if ord(m[i]) - ord(k[i]) < 0:
            decryp_letter_ascii_val = ord(m[i]) + (26 - (ord(k[i]) - ord('A')))
        else:
            decryp_letter_ascii_val = ord(m[i]) - ord(k[i]) + ord('A')
        final_letter = chr(decryp_letter_ascii_val)
        aux.append(final_letter)
    return aux

def main(argv:list[str]):
    op_type = argv[1]
    key = argv[2]
    message = argv[3]

    if len(argv) != 4:
        print("Usage: python3 vigenere.py <op_type> <key> <message>")
        return

    new_key = prepoc(key)
    new_message = prepoc(message)
    new_key = match_length(new_key, new_message)

    if op_type == 'enc':
        result = encode(new_key, new_message)
        print("".join(result))
    elif op_type == 'dec':
        result = decode(new_key, new_message)
        print("".join(result))
    else:
        print("Operation not supported!")
        return

if __name__ == '__main__':
    main(sys.argv)
