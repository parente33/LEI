#!/bin/bash

# Exercício 3
# Não, pois sem a validação do certificado pela CA, a Alice não tem como saber se a chave pública que está a usar para verificar a assinatura pertence ao Bob ou a um impostor. Num exemplo prático:
# Alice envia g^x ao António (pensando ser o Bob)
# O António gera o seu próprio par de chaves DH (e, g^e), o seu próprio par de chaves RSA e um certificado (que não está assinado pela CA de confiança, ou é assinado por uma CA falsa)
# António envia g^e, a assinatura sig_ant(g^e, g^x) e o seu certificado falso
# A Alice recebe o certificado do António. Como omite a verificação com a CA, ela não percebe que aquele certificado não é legítimo. Apenas extrai a chave pública do António do certificado e verifica a assinatura. Esta vai ser sempre válida (pois foi feita pelo António com a chave privada do António)
# Dessa forma, a Alice acredita que estabeleceu uma chave segura com o Bob, mas na verdade estabeleceu-a com o António. Se o António fizer o mesmo com o Bob, consegue decifrar, ler e alterar as mensagens entre os dois sem que nenhum deles se aperceba.
