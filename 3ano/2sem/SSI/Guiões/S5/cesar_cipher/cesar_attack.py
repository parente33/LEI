import sys

abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

def prepoc(str):
    l = []
    for c in str:
        if c.isalpha():
            l.append(c.upper())
    return "".join(l)

def decode_char(key: str, char: str):
    new_key = key.upper()
    new_char = char.upper()
    
    if ord(new_char) - ord(new_key) < 0:
        decrypted_letter_ascii_value = ord(new_char) + (26 - (ord(new_key) - ord('A')))
    else:
        decrypted_letter_ascii_value = ord(new_char) - ord(new_key) + ord('A')
    final_letter = chr(decrypted_letter_ascii_value)
    return final_letter

def attack(criptogram: str, words_seq: [str]):
    aux = []

    for c in abc:
        for i in range(len(criptogram)):
            char = decode_char(c, criptogram[i])
            aux.append(char)
            new_array = ''.join(aux)

        for j in range(len(words_seq)):
            if words_seq[j] in new_array:
                key, word = c, new_array
                return [key, word]
        aux = []
        new_array = ""
    return ""
        
def main(argv:list[str]):
    criptogram = argv[1]
    words_seq = []
    for i in range(2, len(argv)): # To extract the list of words to search for
        words_seq.append(argv[i])
    
    result = attack(criptogram, words_seq)
    if len(result) == 0:
        print(result)
    else:
        print(result[0])
        print(result[1])

if __name__ == '__main__':
    main(sys.argv)
