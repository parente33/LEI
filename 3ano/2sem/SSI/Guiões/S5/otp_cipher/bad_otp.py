import sys, random

def bad_prng(n):
    """ An insecure pseudo-random number generator """
    random.seed(random.randbytes(2))
    return random.randbytes(n)


def setup(nr_bytes, file):
    
    vector = bad_prng(nr_bytes)
    with open(file, "wb") as f:
        f.write(vector)


def enc(file_message, file_key):

    with open(file_message, "r", encoding="utf-8") as fm:
        message = fm.read().encode("utf-8")

    with open(file_key, "rb") as fk:
        key = fk.read()

    # Just to make sure
    assert len(message) == len(key), "Message and key must be the same length!"

    encripted_message = bytes([m ^ k for m, k in zip(message, key)])

    output_file = file_message + ".enc"

    with open(output_file, "wb") as fo:
        fo.write(encripted_message)


def dec(file_message, file_key):

    with open(file_message, "rb") as fm:
        encoded_message = fm.read()

    with open(file_key, "rb") as fk:
        key = fk.read()

    decripted_message = bytes([m ^ k for m, k in zip(encoded_message, key)])

    output_file = file_message + ".dec"

    with open(output_file, "wb") as fo:
        fo.write(decripted_message)


def main(argv:list[str]):

    if argv[1] == "setup":
        if len(argv) != 4:
            print("Usage: python3 otp.py setup <number_of_bytes_to_generate> <file_to_write_the_bytes>")
            return

        nr_random_bytes = int(argv[2])
        file_to_write_bytes = argv[3]

        setup(nr_random_bytes, file_to_write_bytes)
        return

    elif argv[1] == "enc":

        if len(argv) != 4:
            print("Usage: python3 otp.py enc <file_w_message_to_encript> <file_w_key> > <file_w_encripted_message.enc>")
            return

        file_w_message_to_encript = argv[2]
        file_w_key = argv[3]

        enc(file_w_message_to_encript, file_w_key)

    elif argv[1] == "dec":
        if len(argv) != 4:
            print("Usage: python3 otp.py dec <file_w_message_to_decript> <file_w_key> > <file_w_decripted_message.dec>")
            return

        file_w_message_to_decript = argv[2]
        file_w_key = argv[3]

        dec(file_w_message_to_decript, file_w_key)

    else:
        print("Usage: python3 otp.py setup|enc|dec <relevant_args>")
        return

if __name__ == '__main__':
    main(sys.argv)
