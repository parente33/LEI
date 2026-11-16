#!/bin/bash

# Exercício 1
# Em termos de comportamento externo, não há diferenças entre os dois programas. A nivel interno contudo, existem grandes diferenças nos comportamentos:
# 	- Repetições de chaves: bad_otp.py tem uma probabilidade muito maior de ter colisões entre chaves, dado que o espaço de chaves é significativamente mais reduzido, ao passo do que em otp.py essa probabilidade é desprezável
# 	- Segurança: bad_otp.py é vulnerável a ataques, especialmente de força bruta
# 	- Previsibilidade: os bytes gerados em otp.py são verdadeiramente aleatórios, imprevisíveis

# Exercício 2
# Não. O One-Time Pad é absolutamente seguro sobre certas condições, condições essas que não foram cumpridas no ataque anterior. A chave não é verdadeiramente aleatória (2^16 possibilidades apenas - muito simples de fazer brute force), e, obviamente, isso faz com que esta não seja secreta, outra condição necessária para a segurança da cifra.

# Exercício 3
# C1 = M1 XOR K, C2 = M2 XOR K, faz com que C1 XOR C2 = M1 XOR M2
#
# Torna-se mais fácil construir um ataque, pois se, por exemplo, uma parte de uma mensagem for conhecida, recupera-se a outra; ou se houver redundância linguística (testando palavras prováveis). Isto destrói completamente a ideia da segurança absoluta oferecida pela cifra.
