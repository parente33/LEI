#include "Guiao1.h"

int valorCheck(wchar_t c) {
    return (c - 0x1F0A1) % 16;
}

int naipeCheck(wchar_t c) {
    return (c - 0x1F0A1) / 16;
}

Carta wcharParaCarta(wchar_t c) {
    Carta carta;
    carta.valor = valorCheck(c);
    carta.naipe = naipeCheck(c);
    return carta;
}

wchar_t cartaUnicode(Carta c) {
    return 0x1F0A1 + c.naipe * 16 + c.valor;
}

int comparaCartas(const void *a, const void *b) {
    const Carta *ca = (const Carta *)a;
    const Carta *cb = (const Carta *)b;

    if (ca->valor != cb->valor) return ca->valor - cb->valor;
    return ca->naipe - cb->naipe;
}

int todasIguais(const Carta *cartas, int tamanho) {
    if (tamanho < 1 || tamanho > 4) return 0;
    for (int i = 1; i < tamanho; i++) {
        if (cartas[i].valor != cartas[0].valor) return 0;
    }
    return 1;
}

int verificaSequencia(const Carta *cartas, int tamanho) {
    if (tamanho < 3) return 0;

    Carta copia[56];
    for (int i = 0; i < tamanho; i++) {
        copia[i] = cartas[i];
    }
    qsort(copia, tamanho, sizeof(Carta), comparaCartas);

    for (int i = 1; i < tamanho; i++) {
        if (copia[i].valor != copia[i - 1].valor + 1) {
            return 0;
        }
    }

    return 1;
}

int verificaDuplaSequencia(const Carta *cartas, int tamanho) {
    if (tamanho < 6 || tamanho % 2 != 0) return 0;

    for (int i = 0; i < tamanho; i += 2) {
        if (cartas[i].valor != cartas[i + 1].valor)
            return 0;
    }

    for (int i = 2; i < tamanho; i += 2) {
        if (cartas[i].valor != cartas[i - 2].valor + 1)
            return 0;
    }

    return 1;
}

// Processa as jogadas
void processar(wchar_t linha[][BUFSIZ], int ntestes) {
    for (int i = 0; i < ntestes ; i++) {
        int tamanho = wcslen(linha[i]);
        Carta cartas[tamanho];

        for (int j = 0; j < tamanho; j++) {
            cartas[j] = wcharParaCarta(linha[i][j]);
        }

        qsort(cartas, tamanho, sizeof(Carta), comparaCartas);
        Carta maisAlta = cartas[tamanho - 1];

        int iguais = todasIguais(cartas, tamanho);
        int sequencia = verificaSequencia(cartas, tamanho);
        int duplaSequencia = verificaDuplaSequencia(cartas, tamanho);

        if (iguais) {
            wprintf(L"conjunto com %d cartas onde a carta mais alta é %lc\n", tamanho,
                    0x1F0A1 + maisAlta.naipe * 16 + maisAlta.valor);
        } else if (sequencia) {
            wprintf(L"sequência com %d cartas onde a carta mais alta é %lc\n", tamanho,
                    0x1F0A1 + maisAlta.naipe * 16 + maisAlta.valor);
        } else if (duplaSequencia) {
            wprintf(L"dupla sequência com %d cartas onde a carta mais alta é %lc\n", tamanho / 2,
                    0x1F0A1 + maisAlta.naipe * 16 + maisAlta.valor);
        } else {
            wprintf(L"Nada!\n");
        }
    }
}

/*
int main() {
    setlocale(LC_ALL, "C.UTF-8");

    int num_testes;
    wscanf(L"%d", &num_testes);
    while (getwchar() != L'\n');

    wchar_t (*cartas)[BUFSIZ] = malloc(num_testes * sizeof(*cartas));
    if (cartas == NULL) {
        fwprintf(stderr, L"Erro: falha na alocação de memória.\n");
        return 1;
    }

    for (int i = 0; i < num_testes; i++) {
        if (fgetws(cartas[i], BUFSIZ, stdin)) {
            size_t len = wcslen(cartas[i]);
            if (len > 0 && cartas[i][len - 1] == L'\n') {
                cartas[i][len - 1] = L'\0';
            }
        } else {
            fwprintf(stderr, L"Erro na leitura da linha %d\n", i + 1);
            free(cartas);
            return 1;
        }
    }

    processar(cartas, num_testes);

    free(cartas);
    return 0;
}
*/