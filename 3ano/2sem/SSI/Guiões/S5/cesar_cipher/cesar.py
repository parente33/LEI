import sys

# Converts everything into uppercase and removes other characters (spaces, ...)
def prepoc(str):
    l = []
    for c in str:
        if c.isalpha():
            l.append(c.upper())
    return "".join(l)

# Takes care of enconding the message
def encode(secret_key: str, message: str):

    new_secret_key = secret_key.upper()
    prepoc_mess = prepoc(message)
    aux = []

    for i in range(len(prepoc_mess)):
        # Get the ASCII value
        value = ord(prepoc_mess[i])

        # Subtract 'A' from that value (case sensivity doesn't matter here cause it has been prepocessed already)
        position_in_alphabet = value - ord('A')

        # Add the equivalent of the key to the value (A = 0, B = 1, ...), ensuring that the result falls withing the alphabet (mod 26)
        position_in_alphabet += (ord(new_secret_key) - ord('A'))
        position_in_alphabet = position_in_alphabet % 26

        # Find the letter with 'chr' and add it to the auxiliary list
        final_letter = chr(position_in_alphabet + ord('A'))
        aux.append(final_letter)
    return aux

# Takes care of decoding the message
def decode(secret_key: str, message: str):
    
    new_secret_key = secret_key.upper()
    prepoc_mess = prepoc(message)
    aux = []
    
    for i in range(len(prepoc_mess)):
        # Some messy code to solve "negative" ascii values
        if ord(prepoc_mess[i]) - ord(new_secret_key) < 0:
            decrypted_letter_ascii_value = ord(prepoc_mess[i]) + (26 - (ord(new_secret_key) - ord('A')))
        else:
            decrypted_letter_ascii_value = ord(prepoc_mess[i]) - ord(new_secret_key) + ord('A')
        final_letter = chr(decrypted_letter_ascii_value)
        aux.append(final_letter)
    return aux

def main(argv:list[str]):
    operation_type = argv[1]
    secret_key = argv[2]
    message = argv[3]

    if len(secret_key) != 1:
        print("Secret key has to be a single uppercase letter!")
        return

    if len(argv) != 4:
        print("Usage: python3 cesar.py <operation_type> <secret_key> <message>")
        return

    if operation_type == 'enc':
        result = encode(secret_key, message)
        print("".join(result))

    elif operation_type == 'dec':
        result = decode(secret_key, message)
        print("".join(result))
    else:
        print("Operation not supported!")
        return

if __name__ == '__main__':
    main(sys.argv)
