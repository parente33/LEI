from multiprocessing import Process, Pipe
from cryptography.hazmat.primitives.asymmetric import dh
from cryptography.hazmat.primitives import serialization, hashes
from cryptography.hazmat.primitives.kdf.hkdf import HKDF
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
import os

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

parameters = dh.DHParameterNumbers(p, g).parameters()


def derive_key(shared_key):
    hkdf = HKDF(
        algorithm=hashes.SHA256(),
        length=32,
        salt=None,
        info=b"dh-aes-gcm",
    )
    return hkdf.derive(shared_key)


def alice_process(conn):

    priv = parameters.generate_private_key()
    pub = priv.public_key()

    alice_bytes = pub.public_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PublicFormat.SubjectPublicKeyInfo
    )

    # 1
    conn.send(alice_bytes)

    # 2 
    bob_bytes = conn.recv()
    bob_pub = serialization.load_pem_public_key(bob_bytes)

    # 3
    shared = priv.exchange(bob_pub)
    key = derive_key(shared)

    print("Alice K:", key.hex())

    # Enviar mensagem cifrada
    aesgcm = AESGCM(key)
    nonce = os.urandom(12)
    plaintext = b"Mensagem secreta da Alice"
    ciphertext = aesgcm.encrypt(nonce, plaintext, None)

    conn.send((nonce, ciphertext))

    # Receber resposta
    nonce_b, ciphertext_b = conn.recv()
    plaintext_b = aesgcm.decrypt(nonce_b, ciphertext_b, None)

    print("Alice recebeu:", plaintext_b.decode())


def bob_process(conn):

    priv = parameters.generate_private_key()
    pub = priv.public_key()

    # 1
    alice_bytes = conn.recv()
    alice_pub = serialization.load_pem_public_key(alice_bytes)

    bob_bytes = pub.public_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PublicFormat.SubjectPublicKeyInfo
    )

    # 2
    conn.send(bob_bytes)

    # 3
    shared = priv.exchange(alice_pub)
    key = derive_key(shared)

    print("Bob K:", key.hex())

    aesgcm = AESGCM(key)

    # Receber mensagem
    nonce, ciphertext = conn.recv()
    plaintext = aesgcm.decrypt(nonce, ciphertext, None)

    print("Bob recebeu:", plaintext.decode())

    # Responder
    nonce_b = os.urandom(12)
    response = b"Mensagem secreta do Bob"
    ciphertext_b = aesgcm.encrypt(nonce_b, response, None)

    conn.send((nonce_b, ciphertext_b))


if __name__ == '__main__':
    parent_conn, child_conn = Pipe()

    p1 = Process(target=alice_process, args=(parent_conn,))
    p2 = Process(target=bob_process, args=(child_conn,))

    p1.start()
    p2.start()

    p1.join()
    p2.join()

