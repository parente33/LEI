#include "Guiao2.h"

void parseJogadaParaCartas(const Jogada *j, Carta *dest) {
    for (int i = 0; i < j->tamanho; i++) {
        dest[i] = wcharParaCarta(j->texto[i]);
    }
}

int compararJogadasPorCartaAlta(const Jogada *a, const Jogada *b) {
    Carta cartasA[56], cartasB[56];
    parseJogadaParaCartas(a, cartasA);
    parseJogadaParaCartas(b, cartasB);

    qsort(cartasA, a->tamanho, sizeof(Carta), comparaCartas);
    qsort(cartasB, b->tamanho, sizeof(Carta), comparaCartas);

    return comparaCartas(&cartasA[a->tamanho - 1], &cartasB[b->tamanho - 1]);
}

int combinacoesIguais(const Jogada *a, const Jogada *b) {
    if (a->tamanho != b->tamanho) return 0;

    Carta cartasA[56], cartasB[56];
    parseJogadaParaCartas(a, cartasA);
    parseJogadaParaCartas(b, cartasB);

    qsort(cartasA, a->tamanho, sizeof(Carta), comparaCartas);
    qsort(cartasB, b->tamanho, sizeof(Carta), comparaCartas);

    int tipoA =
        todasIguais(cartasA, a->tamanho) ? 1 :
        verificaSequencia(cartasA, a->tamanho) ? 2 :
        verificaDuplaSequencia(cartasA, a->tamanho) ? 3 : -1;

    int tipoB =
        todasIguais(cartasB, b->tamanho) ? 1 :
        verificaSequencia(cartasB, b->tamanho) ? 2 :
        verificaDuplaSequencia(cartasB, b->tamanho) ? 3 : -1;

    return tipoA == tipoB;
}

void processarTestes(const Teste *testes, int num_testes) {
    for (int k = 0; k < num_testes; k++) {
        wprintf(L"Teste %d\n", k + 1);
        Teste copia = testes[k];

        int combinacoes_validas = 1;
        for (int i = 1; i < copia.num_jogadas; i++) {
            if (!combinacoesIguais(&copia.jogadas[0], &copia.jogadas[i])) {
                combinacoes_validas = 0;
                break;
            }
        }

        if (!combinacoes_validas) {
            wprintf(L"Combinações não iguais!\n");
            continue;
        }

        qsort(copia.jogadas, copia.num_jogadas, sizeof(Jogada), 
            (int (*)(const void *, const void *))compararJogadasPorCartaAlta);

        for (int i = 0; i < copia.num_jogadas; i++) {
            Jogada *jogada = &copia.jogadas[i];
        
            Carta cartas[56];
            parseJogadaParaCartas(jogada, cartas);
            qsort(cartas, jogada->tamanho, sizeof(Carta), comparaCartas);
        
            for (int j = 0; j < jogada->tamanho; j++) {
                wchar_t wc = cartaUnicode(cartas[j]);
                wprintf(L"%lc", wc);
                if (j < jogada->tamanho - 1) wprintf(L" ");
            }
            wprintf(L"\n");
        }
    }
}

/*
int main() {
    setlocale(LC_ALL, "C.UTF-8");

    int num_t;
    wscanf(L"%d", &num_t);
    while (getwchar() != L'\n');

    Teste *testes = malloc(num_t * sizeof(Teste));
    if (!testes) {
        fwprintf(stderr, L"Erro de alocação de memória\n");
        return 1;
    }

    for (int t = 0; t < num_t; t++) {
        int n;
        wscanf(L"%d", &n);
        while (getwchar() != L'\n');
        testes[t].num_jogadas = n;

        for (int j = 0; j < n; j++) {
            wchar_t linha[56];
            fgetws(linha, 56, stdin);
            size_t len = wcslen(linha);
            if (len > 0 && linha[len - 1] == L'\n') linha[len - 1] = L'\0';

            wcscpy(testes[t].jogadas[j].texto, linha);
            testes[t].jogadas[j].tamanho = wcslen(linha);
        }
    }

    processarTestes(testes, num_t);

    free(testes);
    return 0;
}
*/