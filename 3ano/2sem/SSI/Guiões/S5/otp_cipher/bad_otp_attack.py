import sys
import random

def candidate_key(seed, nr_bytes):
    """Reproduz a chave gerada pelo bad_prng com uma dada seed."""
    rng = random.Random()
    rng.seed(seed)
    return rng.randbytes(nr_bytes)
 
 
def try_decrypt(ciphertext, key):
    """Decifra o criptograma com a chave candidata através de XOR."""
    return bytes([c ^ k for c, k in zip(ciphertext, key)])
 
 
def attack(nr_bytes, file_ciphertext, wordlist):
 
    with open(file_ciphertext, "rb") as f:
        ciphertext = f.read()
 
    # A seed é random.randbytes(2) -> 2^16 = 65536 possibilidades
    for seed_int in range(65536):
        seed_bytes = seed_int.to_bytes(2, "big")
 
        key = candidate_key(seed_bytes, nr_bytes)
        plaintext_bytes = try_decrypt(ciphertext, key)
 
        try:
            plaintext = plaintext_bytes.decode("utf-8")
        except UnicodeDecodeError:
            continue
 
        if any(word in plaintext for word in wordlist):
            return plaintext
 
    return None


def main(argv: list[str]):
    
    if len(argv) < 4:
        print("Usage: python3 bad_otp_attack.py <key_size> <file_w_ciphertext> <word1> [word2 ...]")
        return

    size_of_key = int(argv[1])
    file = argv[2]
    words_seq = []
    for i in range(3, len(argv)):
        words_seq.append(argv[i])

    result = attack(size_of_key, file, words_seq)

    if result is not None:
        print(result, end="")
    else:
        print("Ataque falhado: nenhuma seed produziu um texto com as palavras fornecidas.")

if __name__ == "__main__":
    main(sys.argv)
