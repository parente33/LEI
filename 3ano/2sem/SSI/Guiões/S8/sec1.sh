#!/bin/bash

# Exercício 1
# Depende no contexto em que se pretende responder. Como as chaves DH não são persistidas em disco (são efémeras), se fecharmos o programa e corrermos de novo, vai ser gerada uma nova chave. Portanto, neste contexto, garante PFS.
# Contudo, entre mensagens na mesma sessão, isso já não acontece, porque todas as mensagens dependem da mesma chave derivada. Comprometer a chave K ou a chave AES dessa sessão expõe todas as mensagens dessa sessão.
