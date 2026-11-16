#!/bin/bash

# Exercício 1
# Um nonce fixo compromete a segurança da cifra, pois faz com que, usando a mesma chave, seja gerado sempre o mesmo keystream. Isso leva a que seja usado o mesmo keystream para cifrar diferentes dados, o que é inseguro.
# Se dois criptogramas forem gerados com o mesmo keystream, um atacante pode fazer XOR nos dois e obter a relação entre os plaintexts, podendo recuperar informação secreta.
# Dessa forma, o nonce não pode ser fixo.

# Exercício 2
# Apenas um bit do texto-limpo muda (visível através da análise dos ficheiros message.txt.enc e altered_message.enc). Isto acotnece porque não há difusão no criptograma, apenas na geração do keystream (que aqui não foi alterado).

# Exercício 3
# AES-CTR: plaintext = ciphertext ⊕ keystream
# Alterar 1 bit no criptograma altera exatamente 1 bit no plaintext, na mesma posição
#
# AES-CBC: P[i] = D(C[i]) ⊕ C[i-1]
# O número de bits alterados vai depender de onde ocorre o bit flip
# Caso 1: bit alterado num bloco C[i]
# 	Todos os bits de P[i] ficam corrompidos (efeito avalanche)
#	Exatamente um bit de P[i+1] é alterado (na mesma posição
# Caso 2: bit alterado num IV (primeiros 16 bytes):
# 	Afeta apenas P[0], apenas um bit é alterado

# Exercício 4
# CTR:
# 	O impacto é equivalente ao observado no ChaCha20. O modo CTR transforma o AES numa cifra de fluxo, e, porque o criptograma resulta do XOR entre o plaintext e o fluxo de chave, qualquer alteração feita num bit do criptograma provoca uma alteração exatamente no mesmo bit do plaintext após a decifração.
#
# CBC:
# 	Aqui é diferente, porque a alteração de um bit no criptograma resulta no bloco correspondente a corromper por completo, e a um bit do bloco seguinte a alterar. Logo, não é possível, com o chacha20_int_attack, subsituir um fragmento de texto de forma limpa e previsível. Mesmo assim, continua a ser possível manipular parte da mensagem.

# Exercício 5
# O salt é um valor aleatório usado na derivação da chave a partir da passphrase; impede que a mesma passphrase gere a mesma chave em cifragens diferentes. Dessa forma, torna inviável ataques de dicionário reutilizando o mesmo hash. Deve ser enviado junto com o criptograma para que a decrifração consiga derivar a mesma chave.
#
# O nonce é um valor aleatório usado diretamente na cifra; garante que, mesmo que se use a mesma chave em cifragens diferentes, que o criptograma varia. Também deve ser armazenado com o criptograma para decifração.
#
# Ambos são portamto, simultaneamente necessários para a segurança e integridade da cifra.
