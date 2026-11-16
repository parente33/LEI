import os
from multiprocessing import Process, Pipe
from cryptography import x509
from cryptography.hazmat.primitives import serialization, hashes
from cryptography.hazmat.primitives.asymmetric import dh, padding
from cryptography.hazmat.primitives.kdf.hkdf import HKDF
from cryptography.hazmat.primitives.ciphers.aead import AESGCM

def mkpair(x, y):
    """Produz uma byte-string contendo o tuplo '(x,y)'"""
    len_x = len(x)
    len_x_bytes = len_x.to_bytes(2, "little")
    return len_x_bytes + x + y


def unpair(xy):
    """Extrai componentes de um par codificado com 'mkpair'"""
    len_x = int.from_bytes(xy[:2], "little")
    x = xy[2 : len_x + 2]
    y = xy[len_x + 2 :]
    return x, y


p = (
    "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E08"
    "8A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B"
    "302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9"
    "A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE6"
    "49286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8"
    "FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D"
    "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C"
    "180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF695581718"
    "3995497CEA956AE515D2261898FA051015728E5A8AACAA68FFFFFFFFFFFFFFFF"
)
g = 2

parameters = dh.DHParameterNumbers(int(p, 16), g).parameters()


def derive_key(shared_key):
    return HKDF(
        algorithm=hashes.SHA256(),
        length=32,
        salt=None,
        info=b"sts-aes-gcm",
    ).derive(shared_key)


def validate_cert(cert_bytes, ca_cert):
    """Valida se o certificado foi assinado pela CA"""
    cert = x509.load_pem_x509_certificate(cert_bytes)
    ca_pub_key = ca_cert.public_key()
    ca_pub_key.verify(
        cert.signature,
        cert.tbs_certificate_bytes,
        padding.PKCS1v15(), # OpenSSL req -x509 usa PKCS1v15 por defeito
        cert.signature_hash_algorithm,
    )
    return cert


def alice_process(conn):
    with open("Alice.key", "rb") as f:
        alice_priv_rsa = serialization.load_pem_private_key(f.read(), password=None)
    with open("Alice.crt", "rb") as f:
        alice_cert_bytes = f.read()
    with open("CA.crt", "rb") as f:
        ca_cert = x509.load_pem_x509_certificate(f.read())

    # 1: Alice -> Bob : gx
    alice_dh_priv = parameters.generate_private_key()
    gx_bytes = alice_dh_priv.public_key().public_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PublicFormat.SubjectPublicKeyInfo
    )
    conn.send(gx_bytes)

    # 2: Recebe gy, SigB(gy, gx), CertB
    msg2 = conn.recv()
    gy_bytes, resto = unpair(msg2)
    sig_b, cert_b_bytes = unpair(resto)

    bob_cert = validate_cert(cert_b_bytes, ca_cert)
    bob_pub_rsa = bob_cert.public_key()

    # SigB(gy, gx)
    bob_pub_rsa.verify(
        sig_b,
        gy_bytes + gx_bytes,
        padding.PSS(mgf=padding.MGF1(hashes.SHA256()), salt_length=padding.PSS.MAX_LENGTH),
        hashes.SHA256()
    )

    # 3: Alice -> Bob : SigA(gx, gy), CertA
    sig_a = alice_priv_rsa.sign(
        gx_bytes + gy_bytes,
        padding.PSS(mgf=padding.MGF1(hashes.SHA256()), salt_length=padding.PSS.MAX_LENGTH),
        hashes.SHA256()
    )
    conn.send(mkpair(sig_a, alice_cert_bytes))

    # Estabelecer Chave K
    bob_dh_pub = serialization.load_pem_public_key(gy_bytes)
    shared_key = alice_dh_priv.exchange(bob_dh_pub)
    k = derive_key(shared_key)
    print(f"Alice K: {k.hex()}")

    # AES-GCM
    aes = AESGCM(k)
    nonce = os.urandom(12)
    ciphertext = aes.encrypt(nonce, b"Ola Bob, aqui e a Alice!", None)
    conn.send(mkpair(nonce, ciphertext))


def bob_process(conn):
    with open("Bob.key", "rb") as f:
        bob_priv_rsa = serialization.load_pem_private_key(f.read(), password=None)
    with open("Bob.crt", "rb") as f:
        bob_cert_bytes = f.read()
    with open("CA.crt", "rb") as f:
        ca_cert = x509.load_pem_x509_certificate(f.read())

    # 1: Recebe gx
    gx_bytes = conn.recv()
    
    # 2: Bob -> Alice : gy, SigB(gy, gx), CertB
    bob_dh_priv = parameters.generate_private_key()
    gy_bytes = bob_dh_priv.public_key().public_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PublicFormat.SubjectPublicKeyInfo
    )
    
    sig_b = bob_priv_rsa.sign(
        gy_bytes + gx_bytes,
        padding.PSS(mgf=padding.MGF1(hashes.SHA256()), salt_length=padding.PSS.MAX_LENGTH),
        hashes.SHA256()
    )
    # Enviar (gy, (sigB, certB))
    conn.send(mkpair(gy_bytes, mkpair(sig_b, bob_cert_bytes)))

    # 3: Recebe SigA(gx, gy), CertA
    msg3 = conn.recv()
    sig_a, cert_a_bytes = unpair(msg3)

    alice_cert = validate_cert(cert_a_bytes, ca_cert)
    alice_pub_rsa = alice_cert.public_key()
    
    alice_pub_rsa.verify(
        sig_a,
        gx_bytes + gy_bytes,
        padding.PSS(mgf=padding.MGF1(hashes.SHA256()), salt_length=padding.PSS.MAX_LENGTH),
        hashes.SHA256()
    )

    # Estabelecer Chave K
    alice_dh_pub = serialization.load_pem_public_key(gx_bytes)
    shared_key = bob_dh_priv.exchange(alice_dh_pub)
    k = derive_key(shared_key)
    print(f"Bob K:   {k.hex()}")

    # Receber Mensagem
    msg_cifrada = conn.recv()
    nonce, ciphertext = unpair(msg_cifrada)
    aes = AESGCM(k)
    plaintext = aes.decrypt(nonce, ciphertext, None)
    print(f"Bob recebeu: {plaintext.decode()}")


if __name__ == '__main__':
    # Verificar se os certificados existem
    if not os.path.exists("CA.crt"):
        print("Erro: Certificados não encontrados. Execute os comandos openssl primeiro.")
    else:
        parent_conn, child_conn = Pipe()

        p1 = Process(target=alice_process, args=(parent_conn,))
        p2 = Process(target=bob_process, args=(child_conn,))
        
        p1.start(); p2.start()
        p1.join(); p2.join()
