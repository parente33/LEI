#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define tamanhoAlfabeto 26

const double frequencia_expectada[tamanhoAlfabeto] = {
    43.31, 10.56, 23.13, 17.25, 56.88, 9.24, 12.59, 15.31,
    38.45, 1.00, 5.61, 27.98, 15.36, 33.92, 36.51, 16.14,
    1.00, 38.64, 29.23, 35.43, 18.51, 5.13, 6.57, 1.48,
    9.06, 1.39
};

double partials(double frequencia_observada[tamanhoAlfabeto]) {
    double soma = 0.0;
    int i;
    for (i = 0; i < tamanhoAlfabeto; i++) {
        double diff = frequencia_expectada[i] - frequencia_observada[i];
        soma += (diff * diff) / frequencia_expectada[i];
    }

    return soma;
}

void decifrar_a_cifra_de_cesar(const char *texto_cifra) {
    double frequencia_observada[tamanhoAlfabeto] = {0};
    int tamanho_texto = strlen(texto_cifra);
    int i;

    // Contagem da frequência de cada letra no texto cifrado
    for (i = 0; i < tamanho_texto; i++) {
        char c = texto_cifra[i];
        if (isalpha(c)) {
            c = toupper(c);
            frequencia_observada[c - 'A']++;
        }
    }

    // Decifrar usando deslocamentos diferentes e encontrando o melhor
    double melhor_partial = partials(frequencia_observada);
    double melhor_deslocamento = 0.0;

    for (int deslocamento = -tamanhoAlfabeto / 2; deslocamento <= tamanhoAlfabeto / 2; deslocamento++) {
        double freq[tamanhoAlfabeto] = {0};

        // Decifrar a mensagem com o deslocamento atual
        for (int i = 0; i < tamanho_texto; i++) {
            char c = texto_cifra[i];
            if (isalpha(c)) {
                c = toupper(c);
                int indice = (c - 'A' - deslocamento + tamanhoAlfabeto) % tamanhoAlfabeto;
                // Ajustar o índice para permitir deslocamentos para a esquerda
                if (indice < 0) {
                    indice += tamanhoAlfabeto;
                }
                // Ajustar o índice para começar a contar a partir da primeira letra da frase
                if (isupper(texto_cifra[i])) {
                    c = 'A' + indice;
                } else {
                    c = 'a' + indice;
                }
                freq[indice]++;
            }
        }

        // Calcula a medida de similaridade para o deslocamento atual
        double deslocamento_atual = partials(freq);

        // Atualizar o melhor deslocamento se encontrarmos uma medida de similaridade melhor
        if (deslocamento_atual < melhor_partial) {
            melhor_partial = deslocamento_atual;
            melhor_deslocamento = deslocamento;
        }
    }

    // Decifrar a mensagem usando o melhor deslocamento encontrado
    printf("%d ", abs((int)melhor_deslocamento)); // Imprime o valor absoluto do deslocamento necessário
    for (int i = 0; i < tamanho_texto; i++) {
        char c = texto_cifra[i];
        if (isalpha(c)) {
            c = toupper(c);
            int indice = (c - 'A' - (int)melhor_deslocamento + tamanhoAlfabeto) % tamanhoAlfabeto;
            // Ajustar o índice para começar a contar a partir da primeira letra da frase
            if (isupper(texto_cifra[i])) {
                c = 'A' + indice;
            } else {
                c = 'a' + indice;
            }
            printf("%c", islower(texto_cifra[i]) ? tolower(c) : c);
        } else {
            printf("%c", texto_cifra[i]);
        }
    }

    printf("\n");
}

int main() {
    char texto_cifra[10001];
    if (fgets(texto_cifra, sizeof(texto_cifra), stdin) == NULL) {
        fprintf(stderr, "Erro ao ler entrada.\n");
        return 1;
    }

    // Remover o caractere da nova linha se estiver presente
    if (texto_cifra[strlen(texto_cifra) - 1] == '\n')
        texto_cifra[strlen(texto_cifra) - 1] = '\0';

    // Decifrar a mensagem
    decifrar_a_cifra_de_cesar(texto_cifra);

    return 0;
}
