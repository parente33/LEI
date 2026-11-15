#include "Guiao4.h"

// Compara combinações para ordenação lexicográfica
int compararCombinacoes(const void *a, const void *b) {
    const Combinacao *ca = (const Combinacao *)a;
    const Combinacao *cb = (const Combinacao *)b;

    int minTam = ca->tamanho < cb->tamanho ? ca->tamanho : cb->tamanho;
    for (int i = 0; i < minTam; i++) {
        if (ca->cartas[i].valor != cb->cartas[i].valor) {
            return ca->cartas[i].valor - cb->cartas[i].valor;
        }
        if (ca->cartas[i].naipe != cb->cartas[i].naipe) {
            return ca->cartas[i].naipe - cb->cartas[i].naipe;
        }
    }
    return ca->tamanho - cb->tamanho;
}

// Verifica se a combinação é melhor que a última carta
int combinaValida(Combinacao comb, Carta ultimaCarta) {
    if (comb.tamanho == 0) return 0;

    Carta maisAlta = comb.cartas[comb.tamanho - 1];

    if (maisAlta.valor > ultimaCarta.valor) {
        return 1;
    } else if (maisAlta.valor == ultimaCarta.valor && maisAlta.naipe > ultimaCarta.naipe) {
        return 1;
    }

    return 0;
}

// Gera todas as combinações de conjuntos de tamanho n com cartas do mesmo valor
void gerarConjuntos(Carta mao[], int tamMao, int tamanho, Combinacao *combinacoes, int *numComb) {
    // Agrupa cartas por valor
    Carta cartasPorValor[14][4];
    int countPorValor[14] = {0};

    for (int i = 0; i < tamMao; i++) {
        int val = mao[i].valor;
        if (countPorValor[val] < 4) {
            cartasPorValor[val][countPorValor[val]++] = mao[i];
        }
    }

    // Para cada valor, gera todas as combinações de 'tamanho' cartas
    for (int val = 0; val <= 13; val++) {
        int n = countPorValor[val];
        if (n < tamanho) continue;

        // Gera C(n, tamanho) combinações
        if (tamanho == 1) {
            for (int i = 0; i < n; i++) {
                Combinacao nova;
                nova.tamanho = 1;
                nova.cartas[0] = cartasPorValor[val][i];
                combinacoes[(*numComb)++] = nova;
            }
        } else if (tamanho == 2) {
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    Combinacao nova;
                    nova.tamanho = 2;
                    nova.cartas[0] = cartasPorValor[val][i];
                    nova.cartas[1] = cartasPorValor[val][j];
                    qsort(nova.cartas, nova.tamanho, sizeof(Carta), comparaCartas);
                    combinacoes[(*numComb)++] = nova;
                }
            }
        } else if (tamanho == 3) {
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    for (int k = j + 1; k < n; k++) {
                        Combinacao nova;
                        nova.tamanho = 3;
                        nova.cartas[0] = cartasPorValor[val][i];
                        nova.cartas[1] = cartasPorValor[val][j];
                        nova.cartas[2] = cartasPorValor[val][k];
                        qsort(nova.cartas, nova.tamanho, sizeof(Carta), comparaCartas);
                        combinacoes[(*numComb)++] = nova;
                    }
                }
            }
        } else if (tamanho == 4) {
            if (n >= 4) {
                Combinacao nova;
                nova.tamanho = 4;
                nova.cartas[0] = cartasPorValor[val][0];
                nova.cartas[1] = cartasPorValor[val][1];
                nova.cartas[2] = cartasPorValor[val][2];
                nova.cartas[3] = cartasPorValor[val][3];
                qsort(nova.cartas, nova.tamanho, sizeof(Carta), comparaCartas);
                combinacoes[(*numComb)++] = nova;
            }
        }
    }
}

// Gera todas as sequências de tamanho n
void gerarSequencias(Carta mao[], int tamMao, int tamanho, Combinacao *combinacoes, int *numComb) {
    // Agrupa cartas por valor
    Carta cartasPorValor[14][4];
    int countPorValor[14] = {0};

    for (int i = 0; i < tamMao; i++) {
        int val = mao[i].valor;
        if (countPorValor[val] < 4) {
            cartasPorValor[val][countPorValor[val]++] = mao[i];
        }
    }

    // Para cada valor inicial possível
    for (int valorInicial = 0; valorInicial <= 14 - tamanho; valorInicial++) {
        // Verifica se temos pelo menos uma carta para cada valor consecutivo
        int valido = 1;
        for (int i = 0; i < tamanho; i++) {
            if (countPorValor[valorInicial + i] == 0) {
                valido = 0;
                break;
            }
        }

        if (!valido) continue;

        // Gera todas as combinações de cartas para esta sequência
        int totalCombinacoes = 1;
        for (int i = 0; i < tamanho; i++) {
            totalCombinacoes *= countPorValor[valorInicial + i];
        }

        for (int c = 0; c < totalCombinacoes; c++) {
            Combinacao nova;
            nova.tamanho = tamanho;

            int temp = c;
            for (int i = 0; i < tamanho; i++) {
                int idx = temp % countPorValor[valorInicial + i];
                nova.cartas[i] = cartasPorValor[valorInicial + i][idx];
                temp /= countPorValor[valorInicial + i];
            }

            qsort(nova.cartas, nova.tamanho, sizeof(Carta), comparaCartas);
            combinacoes[(*numComb)++] = nova;
        }
    }
}

// Gera todas as duplas sequências de tamanho n (n/2 pares consecutivos)
void gerarDuplasSequencias(Carta mao[], int tamMao, int tamanho, Combinacao *combinacoes, int *numComb) {
    if (tamanho < 6 || tamanho % 2 != 0) return;

    int numPares = tamanho / 2;

    // Agrupa cartas por valor
    Carta cartasPorValor[14][4];
    int countPorValor[14] = {0};

    for (int i = 0; i < tamMao; i++) {
        int val = mao[i].valor;
        if (countPorValor[val] < 4) {
            cartasPorValor[val][countPorValor[val]++] = mao[i];
        }
    }

    // Para cada valor inicial possível
    for (int valorInicial = 0; valorInicial <= 14 - numPares; valorInicial++) {
        // Verifica se temos pelo menos 2 cartas para cada valor consecutivo
        int valido = 1;
        for (int i = 0; i < numPares; i++) {
            if (countPorValor[valorInicial + i] < 2) {
                valido = 0;
                break;
            }
        }

        if (!valido) continue;

        // Calcula o número de combinações de pares possíveis
        int numCombPorValor[14];
        int totalCombinacoes = 1;
        for (int i = 0; i < numPares; i++) {
            int n = countPorValor[valorInicial + i];
            // C(n, 2) = n * (n-1) / 2
            numCombPorValor[i] = n * (n - 1) / 2;
            totalCombinacoes *= numCombPorValor[i];
        }

        // Gera todas as combinações de pares
        for (int c = 0; c < totalCombinacoes; c++) {
            Combinacao nova;
            nova.tamanho = tamanho;

            int temp = c;
            int pos = 0;

            for (int i = 0; i < numPares; i++) {
                int idx = temp % numCombPorValor[i];
                temp /= numCombPorValor[i];

                // Converte idx para um par de índices
                int n = countPorValor[valorInicial + i];
                int idx1 = 0, idx2 = 1;
                int cnt = 0;
                for (int a = 0; a < n - 1; a++) {
                    for (int b = a + 1; b < n; b++) {
                        if (cnt == idx) {
                            idx1 = a;
                            idx2 = b;
                            goto found;
                        }
                        cnt++;
                    }
                }
                found:

                nova.cartas[pos++] = cartasPorValor[valorInicial + i][idx1];
                nova.cartas[pos++] = cartasPorValor[valorInicial + i][idx2];
            }

            qsort(nova.cartas, nova.tamanho, sizeof(Carta), comparaCartas);
            combinacoes[(*numComb)++] = nova;
        }
    }
}

// Remove combinações duplicadas
int removerDuplicadas(Combinacao *combinacoes, int numComb) {
    if (numComb <= 1) return numComb;

    qsort(combinacoes, numComb, sizeof(Combinacao), compararCombinacoes);

    int novoTam = 1;
    for (int i = 1; i < numComb; i++) {
        int igual = 0;
        if (combinacoes[i].tamanho == combinacoes[novoTam - 1].tamanho) {
            igual = 1;
            for (int j = 0; j < combinacoes[i].tamanho; j++) {
                if (combinacoes[i].cartas[j].valor != combinacoes[novoTam - 1].cartas[j].valor ||
                    combinacoes[i].cartas[j].naipe != combinacoes[novoTam - 1].cartas[j].naipe) {
                    igual = 0;
                    break;
                }
            }
        }
        if (!igual) {
            combinacoes[novoTam++] = combinacoes[i];
        }
    }
    return novoTam;
}

void gerarCombinacoesDoMesmoTipo(Carta mao[], int tamMao, int tipo, int tamanho, Combinacao **combinacoes, int *numComb) {
    *numComb = 0;
    *combinacoes = malloc(100000 * sizeof(Combinacao));
    if (*combinacoes == NULL) {
        fwprintf(stderr, L"Erro: falha na alocação de memória.\n");
        exit(1);
    }

    if (tipo == 1) {
        // Conjuntos
        gerarConjuntos(mao, tamMao, tamanho, *combinacoes, numComb);
    } else if (tipo == 2) {
        // Sequências
        gerarSequencias(mao, tamMao, tamanho, *combinacoes, numComb);
    } else if (tipo == 3) {
        // Duplas sequências
        gerarDuplasSequencias(mao, tamMao, tamanho, *combinacoes, numComb);
    }

    *numComb = removerDuplicadas(*combinacoes, *numComb);
}

void gerarTodasCombinacoes(Carta mao[], int tamMao, Combinacao **combinacoes, int *numComb) {
    *numComb = 0;
    *combinacoes = malloc(100000 * sizeof(Combinacao));
    if (*combinacoes == NULL) {
        fwprintf(stderr, L"Erro: falha na alocação de memória.\n");
        exit(1);
    }

    // Gera conjuntos de 1 a 4 cartas
    for (int tam = 1; tam <= 4; tam++) {
        gerarConjuntos(mao, tamMao, tam, *combinacoes, numComb);
    }

    // Gera sequências de 3 a tamMao cartas
    for (int tam = 3; tam <= tamMao && tam <= 13; tam++) {
        gerarSequencias(mao, tamMao, tam, *combinacoes, numComb);
    }

    // Gera duplas sequências de 6 a tamMao cartas
    for (int tam = 6; tam <= tamMao; tam += 2) {
        gerarDuplasSequencias(mao, tamMao, tam, *combinacoes, numComb);
    }

    *numComb = removerDuplicadas(*combinacoes, *numComb);
}

void gerarTodasCombinacoesDeValor(Carta cartas[], int inicio, int tamCartas, int tamanho, Combinacao *combinacoes, int *numComb, Combinacao atual, int pos) {
    if (pos == tamanho) {
        combinacoes[(*numComb)++] = atual;
        return;
    }

    for (int i = inicio; i <= tamCartas - (tamanho - pos); i++) {
        atual.cartas[pos] = cartas[i];
        gerarTodasCombinacoesDeValor(cartas, i + 1, tamCartas, tamanho, combinacoes, numComb, atual, pos + 1);
    }
}

void imprimirCombinacoesValidas(Carta mao[], int tamMao, wchar_t ultimaComb[], wchar_t **output, int *outputSize) {
    wchar_t buffer[500000] = L"";

    Carta cartasUltimaComb[56];
    int tamUltimaComb = wcslen(ultimaComb);
    for (int i = 0; i < tamUltimaComb; i++) {
        cartasUltimaComb[i] = wcharParaCarta(ultimaComb[i]);
    }
    qsort(cartasUltimaComb, tamUltimaComb, sizeof(Carta), comparaCartas);
    Carta ultimaCarta = cartasUltimaComb[tamUltimaComb - 1];

    // Verifica se é exceção de Reis
    int ehExcecaoReis = 0;
    if (tamUltimaComb == 1 && cartasUltimaComb[0].valor == 13) {
        ehExcecaoReis = 1;
    } else if (tamUltimaComb == 2 && cartasUltimaComb[0].valor == 13 && cartasUltimaComb[1].valor == 13) {
        ehExcecaoReis = 2;
    } else if (tamUltimaComb == 3 && cartasUltimaComb[0].valor == 13 && cartasUltimaComb[1].valor == 13 && cartasUltimaComb[2].valor == 13) {
        ehExcecaoReis = 3;
    }

    Combinacao *combinacoes;
    int numComb = 0;

    if (ehExcecaoReis) {
        combinacoes = malloc(100000 * sizeof(Combinacao));
        if (combinacoes == NULL) {
            fwprintf(stderr, L"Erro: falha na alocação de memória.\n");
            exit(1);
        }

        // Gera as exceções de Reis
        int numExcecoes = 0;
        Combinacao *excecoes = malloc(100000 * sizeof(Combinacao));

        if (ehExcecaoReis == 1) {
            // Conjuntos de 4 cartas
            gerarConjuntos(mao, tamMao, 4, excecoes, &numExcecoes);
            // Duplas sequências de 6 cartas (3 pares)
            gerarDuplasSequencias(mao, tamMao, 6, excecoes, &numExcecoes);
        } else if (ehExcecaoReis == 2) {
            // Duplas sequências de 8 cartas (4 pares)
            gerarDuplasSequencias(mao, tamMao, 8, excecoes, &numExcecoes);
        } else if (ehExcecaoReis == 3) {
            // Duplas sequências de 10 cartas (5 pares)
            gerarDuplasSequencias(mao, tamMao, 10, excecoes, &numExcecoes);
        }

        // Separar conjuntos e duplas sequências nas exceções
        Combinacao *conjuntos = malloc(100000 * sizeof(Combinacao));
        Combinacao *duplasSeq = malloc(100000 * sizeof(Combinacao));
        int numConj = 0, numDupla = 0;

        for (int i = 0; i < numExcecoes; i++) {
            qsort(excecoes[i].cartas, excecoes[i].tamanho, sizeof(Carta), comparaCartas);
            if (todasIguais(excecoes[i].cartas, excecoes[i].tamanho)) {
                conjuntos[numConj++] = excecoes[i];
            } else {
                duplasSeq[numDupla++] = excecoes[i];
            }
        }

        numConj = removerDuplicadas(conjuntos, numConj);
        numDupla = removerDuplicadas(duplasSeq, numDupla);

        // Imprime conjuntos de exceção
        for (int i = 0; i < numConj; i++) {
            for (int j = 0; j < conjuntos[i].tamanho; j++) {
                wchar_t cartaStr[10];
                swprintf(cartaStr, 10, L"%lc", cartaUnicode(conjuntos[i].cartas[j]));
                wcscat(buffer, cartaStr);
                if (j < conjuntos[i].tamanho - 1) wcscat(buffer, L" ");
            }
            wcscat(buffer, L"\n");
        }

        // Imprime duplas sequências de exceção
        for (int i = 0; i < numDupla; i++) {
            for (int j = 0; j < duplasSeq[i].tamanho; j++) {
                wchar_t cartaStr[10];
                swprintf(cartaStr, 10, L"%lc", cartaUnicode(duplasSeq[i].cartas[j]));
                wcscat(buffer, cartaStr);
                if (j < duplasSeq[i].tamanho - 1) wcscat(buffer, L" ");
            }
            wcscat(buffer, L"\n");
        }

        int totalValidas = numConj + numDupla;
        if (totalValidas == 0) {
            wcscat(buffer, L"PASSO\n");
        }

        free(excecoes);
        free(conjuntos);
        free(duplasSeq);
        free(combinacoes);
    } else {
        // Caso normal (não é exceção de Reis)
        int tipoUltimaComb =
            todasIguais(cartasUltimaComb, tamUltimaComb) ? 1 :
            verificaSequencia(cartasUltimaComb, tamUltimaComb) ? 2 :
            verificaDuplaSequencia(cartasUltimaComb, tamUltimaComb) ? 3 : -1;

        gerarCombinacoesDoMesmoTipo(mao, tamMao, tipoUltimaComb, tamUltimaComb, &combinacoes, &numComb);

        int numValidas = 0;
        Combinacao *validas = malloc(100000 * sizeof(Combinacao));
        for (int i = 0; i < numComb; i++) {
            qsort(combinacoes[i].cartas, combinacoes[i].tamanho, sizeof(Carta), comparaCartas);
            Carta maisAlta = combinacoes[i].cartas[combinacoes[i].tamanho - 1];
            if (maisAlta.valor > ultimaCarta.valor ||
                (maisAlta.valor == ultimaCarta.valor && maisAlta.naipe > ultimaCarta.naipe)) {
                validas[numValidas++] = combinacoes[i];
            }
        }

        numValidas = removerDuplicadas(validas, numValidas);

        for (int i = 0; i < numValidas; i++) {
            for (int j = 0; j < validas[i].tamanho; j++) {
                wchar_t cartaStr[10];
                swprintf(cartaStr, 10, L"%lc", cartaUnicode(validas[i].cartas[j]));
                wcscat(buffer, cartaStr);
                if (j < validas[i].tamanho - 1) wcscat(buffer, L" ");
            }
            wcscat(buffer, L"\n");
        }

        if (numValidas == 0) {
            wcscat(buffer, L"PASSO\n");
        }

        free(validas);
        free(combinacoes);
    }

    output[*outputSize] = malloc((wcslen(buffer) + 1) * sizeof(wchar_t));
    if (output[*outputSize] == NULL) {
        fwprintf(stderr, L"Erro: falha na alocação de memória.\n");
        exit(1);
    }
    wcscpy(output[*outputSize], buffer);
    (*outputSize)++;
}

void processarTestesGui4() {
    setlocale(LC_ALL, "C.UTF-8");

    int T;
    wscanf(L"%d", &T);

    wchar_t **output = malloc(T * 2 * sizeof(wchar_t *));
    if (output == NULL) {
        fwprintf(stderr, L"Erro: falha na alocação de memória.\n");
        exit(1);
    }
    int outputSize = 0;

    for (int t = 0; t < T; t++) {
        wchar_t ultimaComb[56];
        wscanf(L"%ls", ultimaComb);

        wchar_t maoStr[56];
        wscanf(L"%ls", maoStr);

        Carta mao[56];
        int tamMao = wcslen(maoStr);
        for (int i = 0; i < tamMao; i++) {
            mao[i] = wcharParaCarta(maoStr[i]);
        }
        qsort(mao, tamMao, sizeof(Carta), comparaCartas);

        wchar_t testeHeader[20];
        swprintf(testeHeader, 20, L"Teste %d\n", t + 1);
        output[outputSize] = malloc((wcslen(testeHeader) + 1) * sizeof(wchar_t));
        if (output[outputSize] == NULL) {
            fwprintf(stderr, L"Erro: falha na alocação de memória.\n");
            exit(1);
        }
        wcscpy(output[outputSize], testeHeader);
        outputSize++;

        imprimirCombinacoesValidas(mao, tamMao, ultimaComb, output, &outputSize);
    }

    for (int i = 0; i < outputSize; i++) {
        wprintf(L"%ls", output[i]);
        free(output[i]);
    }
    free(output);
}

int main() {
    processarTestesGui4();
    return 0;
}