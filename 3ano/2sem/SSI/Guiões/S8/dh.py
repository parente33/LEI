from multiprocessing import Process, Pipe
from cryptography.hazmat.primitives.asymmetric import dh
from cryptography.hazmat.primitives import serialization

# Parâmetros fixos
p = int(
    "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E08"
    "8A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B"
    "302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9"
    "A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE6"
    "49286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8"
    "FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D"
    "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C"
    "180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF695581718"
    "3995497CEA956AE515D2261898FA051015728E5A8AACAA68FFFFFFFFFFFFFFFF",
    16
)
g = 2

# Construção dos parâmetros
pn = dh.DHParameterNumbers(p, g)
parameters = pn.parameters()


def alice_process(conn):
    
    private_key = parameters.generate_private_key()
    public_key = private_key.public_key()

    alice_bytes = public_key.public_bytes(
            encoding=serialization.Encoding.PEM,
            format=serialization.PublicFormat.SubjectPublicKeyInfo
    )

    # Alice -> Bob
    conn.send(alice_bytes)

    # Bob -> Alice
    bob_bytes = conn.recv()
    bob_public_key = serialization.load_pem_public_key(bob_bytes)

    shared_key = private_key.exchange(bob_public_key)

    print("Alice K:", shared_key.hex())


def bob_process(conn):

    private_key = parameters.generate_private_key()
    public_key = private_key.public_key()

    # Alice -> Bob
    alice_bytes = conn.recv()
    alice_public_key = serialization.load_pem_public_key(alice_bytes)

    # PEM
    bob_bytes = public_key.public_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PublicFormat.SubjectPublicKeyInfo
    )

    # Bob -> Alice
    conn.send(bob_bytes)

    shared_key = private_key.exchange(alice_public_key)

    print("Bob K:", shared_key.hex())


if __name__ == '__main__':
    parent_conn, child_conn = Pipe()

    p1 = Process(target=alice_process, args=(parent_conn,))
    p2 = Process(target=bob_process, args=(child_conn,))

    p1.start()
    p2.start()

    p1.join()
    p2.join()
