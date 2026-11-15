#ifndef GUIAO4_H
#define GUIAO4_H

#include "Guiao1.h"
#include "Guiao2.h"
#include "Guiao3.h"

#include <stdio.h>
#include <stdlib.h>
#include <wchar.h>
#include <locale.h>
#include <string.h>

typedef struct {
    Carta cartas[56];
    int tamanho;
} Combinacao;

int compararCombinacoes(const void *a, const void *b);
int combinaValida(Combinacao comb, Carta ultimaCarta);
void gerarConjuntos(Carta mao[], int tamMao, int tamanho, Combinacao *combinacoes, int *numComb);
void gerarSequencias(Carta mao[], int tamMao, int tamanho, Combinacao *combinacoes, int *numComb);
void gerarDuplasSequencias(Carta mao[], int tamMao, int tamanho, Combinacao *combinacoes, int *numComb);
int removerDuplicadas(Combinacao *combinacoes, int numComb);
void gerarCombinacoesDoMesmoTipo(Carta mao[], int tamMao, int tipo, int tamanho, Combinacao **combinacoes, int *numComb);
void gerarTodasCombinacoes(Carta mao[], int tamMao, Combinacao **combinacoes, int *numComb);
void gerarTodasCombinacoesDeValor(Carta cartas[], int inicio, int tamCartas, int tamanho, Combinacao *combinacoes, int *numComb, Combinacao atual, int pos);
void imprimirCombinacoesValidas(Carta mao[], int tamMao, wchar_t ultimaComb[], wchar_t **output, int *outputSize);
void processarTestesGui4();

#endif // GUIAO4_H