#ifndef GUIAO3_H
#define GUIAO3_H

#include "Guiao1.h"
#include "Guiao2.h"

#include <stdio.h>
#include <string.h>
#include <locale.h>
#include <wchar.h>
#include <stddef.h>
#include <stdlib.h>

int ePasso(Jogada jogada);
void removerCarta(Carta mao[], int *tamanho, Carta carta);
void removerCartasJogadas(Carta mao[], int *tamanho, Jogada jogada);
int aMinhaJogadaEValida(Jogada jogada);
int todosPassaram(Teste *teste);
int umRei(Jogada j);
int doisReis(Jogada j);
int tresReis(Jogada j);
int validaJogadaNaoPassou(Teste *teste, Jogada jogadaAtual);
int validaJogadaReis(Teste *teste, Jogada jogadaAtual);
int validaJogada(Teste *teste, Jogada jogadaAtual);
int validaAJogadaSeTodosPassaram(Teste *teste, Jogada jogadaAtual);
void processarCasoTeste(Carta mao[], int *tamMao, Teste *teste, Jogada jogadaAtual);
void lerMao(Carta mao[], int *tamanho);
void lerJogadasAnteriores(Teste *teste, int N);
void lerJogadaAtual(Jogada *jogadaAtual);

#endif // GUIAO3_H