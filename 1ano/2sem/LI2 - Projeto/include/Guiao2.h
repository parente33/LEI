#ifndef GUIAO2_H
#define GUIAO2_H

#include "Guiao1.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <wchar.h>
#include <locale.h>

// Estrutura que guarda as cartas de uma jogada
typedef struct {
    wchar_t texto[56];
    int tamanho;
} Jogada;

// Estrutura que guarda um teste
typedef struct {
    Jogada jogadas[56];
    int num_jogadas;
} Teste;

void parseJogadaParaCartas(const Jogada *j, Carta *dest);
int compararJogadasPorCartaAlta(const Jogada *a, const Jogada *b);
int combinacoesIguais(const Jogada *a, const Jogada *b);
void processarTestes(const Teste *testes, int num_testes);

#endif // GUIAO2_H