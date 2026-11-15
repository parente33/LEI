#include "Guiao3.h"

int ePasso(Jogada jogada) {
    return wcscmp(jogada.texto, L"PASSO") == 0;
}

// Remove uma carta da mão
void removerCarta(Carta mao[], int *tamanho, Carta carta) {
    for (int i = 0; i < *tamanho; i++) {
        if (mao[i].valor == carta.valor && mao[i].naipe == carta.naipe) {
            for (int j = i; j < *tamanho - 1; j++) {
                mao[j] = mao[j + 1];
            }
            (*tamanho)--;
            return;
        }
    }
}

// Remove todas as cartas de uma jogada da mão
void removerCartasJogadas(Carta mao[], int *tamanho, Jogada jogada) {
    if (ePasso(jogada)) return;
    Carta cartas[56];
    parseJogadaParaCartas(&jogada, cartas);

    for (int i = 0; i < jogada.tamanho; i++) {
        removerCarta(mao, tamanho, cartas[i]);
    }
}

// Verifica se uma jogada é válida
int aMinhaJogadaEValida(Jogada jogada) {
    //wprintf(L"DEBUG: aMinhaJogadaEValida chamada\n");
    if (ePasso(jogada)) return 1;

    Carta cartas[56];
    parseJogadaParaCartas(&jogada, cartas);

    if (verificaSequencia(cartas, jogada.tamanho)) {
        //wprintf(L"DEBUG: Jogada é uma SEQUÊNCIA\n");
        return 1;
    }

    if (verificaDuplaSequencia(cartas, jogada.tamanho)) {
        //wprintf(L"DEBUG: Jogada é uma DUPLA SEQUÊNCIA\n");
        return 1;
    }

    if (todasIguais(cartas, jogada.tamanho)) {
        //wprintf(L"DEBUG: Jogada é um CONJUNTO IGUAL\n");
        return 1;
    }

    //wprintf(L"DEBUG: Jogada INVÁLIDA\n");
    return 0;
}

// Verifica se todos passaram nas últimas 3 jogadas
int todosPassaram(Teste *teste) {
    if (teste->num_jogadas < 3) return 0;
    for (int i = teste->num_jogadas - 3; i < teste->num_jogadas; i++) {
        //wprintf(L"DEBUG: jogada[%d] = %ls, ePasso = %d\n", i, teste->jogadas[i].texto, ePasso(teste->jogadas[i]));
        if (!ePasso(teste->jogadas[i])) return 0;
    }
    return 1;
}

int umRei(Jogada j) {
    if (j.tamanho != 1) return 0;
    Carta c = wcharParaCarta(j.texto[0]);
    return c.valor == 13;
}

int doisReis(Jogada j) {
    if (j.tamanho != 2) return 0;
    Carta c1 = wcharParaCarta(j.texto[0]);
    Carta c2 = wcharParaCarta(j.texto[1]);
    return c1.valor == 13 && c2.valor == 13;
}

int tresReis(Jogada j) {
    if (j.tamanho != 3) return 0;
    Carta c1 = wcharParaCarta(j.texto[0]);
    Carta c2 = wcharParaCarta(j.texto[1]);
    Carta c3 = wcharParaCarta(j.texto[2]);
    return c1.valor == 13 && c2.valor == 13 && c3.valor == 13;
}

int validaJogadaNaoPassou(Teste *teste, Jogada jogadaAtual) {
    int ult = -1;
    for (int i = teste->num_jogadas - 1; i >= 0 && i >= teste->num_jogadas - 3; i--) {
        if (!ePasso(teste->jogadas[i])) {
            ult = i;
            break;
        }
    }

    if (ePasso(jogadaAtual)) return 1;

    if (ult != -1 && teste->jogadas[ult].tamanho == jogadaAtual.tamanho) {
        Carta anterior[56], atual[56];
        parseJogadaParaCartas(&teste->jogadas[ult], anterior);
        parseJogadaParaCartas(&jogadaAtual, atual);

        Carta c_ant = anterior[teste->jogadas[ult].tamanho - 1];
        Carta c_atual = atual[jogadaAtual.tamanho - 1];

        if (aMinhaJogadaEValida(jogadaAtual) &&
            (c_atual.valor > c_ant.valor ||
             (c_atual.valor == c_ant.valor && c_atual.naipe > c_ant.naipe))) {
            return 1;
        }
    }

    return 0;
}

int validaJogadaReis(Teste *teste, Jogada jogadaAtual) {
    if (ePasso(jogadaAtual)) return 1;

    int ult = -1;
    for (int i = teste->num_jogadas - 1; i >= 0 && i >= teste->num_jogadas - 3; i--) {
        if (!ePasso(teste->jogadas[i])) {
            ult = i;
            break;
        }
    }
    if (ult == -1) return 0;

    Jogada anterior = teste->jogadas[ult];
    Carta cartasAtual[56];
    parseJogadaParaCartas(&jogadaAtual, cartasAtual);

    qsort(cartasAtual, jogadaAtual.tamanho, sizeof(Carta), comparaCartas);

    if (umRei(anterior) && todasIguais(cartasAtual, jogadaAtual.tamanho) && jogadaAtual.tamanho == 4) return 1;
    if (umRei(anterior) && verificaDuplaSequencia(cartasAtual, jogadaAtual.tamanho) && jogadaAtual.tamanho == 6) return 1;
    if (doisReis(anterior) && verificaDuplaSequencia(cartasAtual, jogadaAtual.tamanho) && jogadaAtual.tamanho == 8) return 1;
    if (tresReis(anterior) && verificaDuplaSequencia(cartasAtual, jogadaAtual.tamanho) && jogadaAtual.tamanho == 10) return 1;

    if ((umRei(anterior) || doisReis(anterior)) && validaJogadaNaoPassou(teste, jogadaAtual)) return 1;

    return 0;
}

int validaJogada(Teste *teste, Jogada jogadaAtual) {
    //wprintf(L"DEBUG: validaJogada chamada\n");
    if (validaJogadaNaoPassou(teste, jogadaAtual) || validaJogadaReis(teste, jogadaAtual)) {
        return 1;
    }
    if (teste->num_jogadas == 0 && aMinhaJogadaEValida(jogadaAtual)) return 1;

    return 0;
}

int validaAJogadaSeTodosPassaram(Teste *teste, Jogada jogadaAtual) {
    int passos = todosPassaram(teste);
    int valida = aMinhaJogadaEValida(jogadaAtual);
    //wprintf(L"DEBUG: todosPassaram=%d, aMinhaJogadaEValida=%d\n", passos, valida);
    return passos && valida;
}

void processarCasoTeste(Carta mao[], int *tamMao, Teste *teste, Jogada jogadaAtual) {
    int pode = validaAJogadaSeTodosPassaram(teste, jogadaAtual) ||
               validaJogada(teste, jogadaAtual);

    if (pode) removerCartasJogadas(mao, tamMao, jogadaAtual);
}

void lerMao(Carta mao[], int *tamanho) {
    wchar_t entrada[56];
    *tamanho = 0;

    wscanf(L"%ls", entrada);
    int len = wcslen(entrada);

    for (int i = 0; i < len; i++) {
        mao[*tamanho] = wcharParaCarta(entrada[i]);
        (*tamanho)++;
    }

    qsort(mao, *tamanho, sizeof(Carta), comparaCartas);
}

void lerJogadasAnteriores(Teste *teste, int N) {
    teste->num_jogadas = N;

    for (int i = 0; i < N; i++) {
        wchar_t linha[56];
        wscanf(L"%ls", linha);

        if (wcscmp(linha, L"PASSO") == 0) {
            wcscpy(teste->jogadas[i].texto, L"PASSO");
            teste->jogadas[i].tamanho = wcslen(L"PASSO");
            teste->jogadas[i].texto[teste->jogadas[i].tamanho] = L'\0';
        } else {
            wcscpy(teste->jogadas[i].texto, linha);
            teste->jogadas[i].tamanho = wcslen(linha);
            teste->jogadas[i].texto[teste->jogadas[i].tamanho] = L'\0';
        }
    }
}

void lerJogadaAtual(Jogada *jogadaAtual) {
    wchar_t linha[56];
    wscanf(L"%ls", linha);

    if (wcscmp(linha, L"PASSO") == 0) {
        jogadaAtual->tamanho = 0;
        jogadaAtual->texto[0] = L'\0';
        return;
    }

    jogadaAtual->tamanho = (int)wcslen(linha);
    for (int i = 0; i < jogadaAtual->tamanho; i++) {
        jogadaAtual->texto[i] = linha[i];
    }
    jogadaAtual->texto[jogadaAtual->tamanho] = L'\0';
}

/*
int main() {
    setlocale(LC_ALL, "C.UTF-8");

    int T;
    wscanf(L"%d", &T);

    Carta resultados[T][56];
    int tamanhos_resultado[T];

    for (int t = 0; t < T; t++) {
        int N;
        wscanf(L"%d", &N);

        Carta mao[56];
        int tamanhoMao;
        lerMao(mao, &tamanhoMao);

        Teste teste;
        lerJogadasAnteriores(&teste, N);

        Jogada jogadaAtual;
        lerJogadaAtual(&jogadaAtual);

        processarCasoTeste(mao, &tamanhoMao, &teste, jogadaAtual);

        tamanhos_resultado[t] = tamanhoMao;
        for (int i = 0; i < tamanhoMao; i++) {
            resultados[t][i] = mao[i];
        }
    }

    for (int i = 0; i < T; i++) {
        wprintf(L"Teste %d\n", i + 1);

        for (int j = 0; j < tamanhos_resultado[i]; j++) {
            qsort(resultados[i], tamanhos_resultado[i], sizeof(Carta), comparaCartas);
            
            wprintf(L"%lc", cartaUnicode(resultados[i][j]));
            if (j + 1 < tamanhos_resultado[i]) {
                wprintf(L" ");
            }
        }
        wprintf(L"\n");
    }

    return 0;
}
*/