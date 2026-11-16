#!/bin/bash

# Exercício 1

# O url tem 62 bytes, e assume-se o tamanho da chave como 32 bytes
# Logo, |k| + |m| = 62 + 32 = 94
# 94 mod 64 = 30
# Assim, depois de se adicionar 0x80 (31 bytes), queremos chegar a 56 mod 64 (faltam 25 bytes de zeros - 0x00)
# No final, acrescenta-se o comprimento - 8 bytes
#
# Tamanho total: 34 bytes


# Exercício 2

# Tamanho do ficheiro: CTR+HMAC é maior, por a nonce usada ser maior (16 bytes em vez de 12), e de a tag ser também ela maior (32 bytes em vez de 16)
# Logo, AES-GCM produz ficheiros mais pequenos
