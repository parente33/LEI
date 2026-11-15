#ifndef GUIAO1_H
#define GUIAO1_H

#include <stdio.h>
#include <stdlib.h>
#include <wchar.h>
#include <locale.h>
#include <string.h>

typedef struct carta {
    int valor; // 0 -> Ás, ..., 13 -> Rei
    int naipe; // 0 -> Espadas, 1 -> Copas, 2 -> Ouros, 3 -> Paus
} Carta;

int valorCheck(wchar_t c);
int naipeCheck(wchar_t c);
Carta wcharParaCarta(wchar_t wc);
wchar_t cartaUnicode(Carta c);
int comparaCartas(const void *a, const void *b);
int todasIguais(const Carta *cartas, int tam);
int verificaSequencia(const Carta *cartas, int tam);
int verificaDuplaSequencia(const Carta *cartas, int tam);
void processar(wchar_t linha[][BUFSIZ], int ntestes);

#endif // GUIAO1_H