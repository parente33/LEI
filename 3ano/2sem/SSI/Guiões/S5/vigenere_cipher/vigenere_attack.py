import sys

from vigenere import decode, match_length

alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

common_letters = "AEOSRINDMUT"

def letter_freq_score(text):
    count = sum(text.count(c) for c in common_letters)
    return count / max(len(text), 1)

def generate_possible_keys(alphabet, k):
    if k == 0:
        yield ""
    else:
        for c in alphabet:
            for suffix in generate_possible_keys(alphabet, k - 1):
                result = c + suffix
                yield result

def attack(size: int, cript: str, seq: [str]):
    best_key = None
    best_decrypt = None
    best_score = -1

    for key in generate_possible_keys(alphabet, size):
        # Expand the key to match the criptogram's size
        expanded_key = match_length(key, cript)
        # Try to decipher with the possible key
        decripted_mess = "".join(decode(expanded_key, cript))

        matches = sum(1 for word in seq if word in decripted_mess)

        freq_score = letter_freq_score(decripted_mess)
        total_score = matches + 0.1 * freq_score

        if total_score > best_score:
            best_key = key
            best_decrypt = decripted_mess
            best_score = total_score

    if best_key:
        return [best_key, best_decrypt]
        
    return "" # Couldn't find the key after exausting all possibilites    

def main(argv:list[str]):
    key_size = int(argv[1])
    criptogram = argv[2]
    words_seq = []

    for i in range(3, len(argv)):
        words_seq.append(argv[i])

    result = attack(key_size, criptogram, words_seq)
    if len(result) == 0:
        print(result)
    else:
        print(result[0])
        print(result[1])

if __name__ == '__main__':
    main(sys.argv)
