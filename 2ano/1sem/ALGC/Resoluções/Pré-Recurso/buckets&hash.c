#include <stdlib.h>
#define SIZE 1000

typedef struct b {
    int estado; // 0 = ocup, 1 = livre
    int chave;
    int pos;
} bucket;

typedef bucket HASH[SIZE];

void initT(HASH t) {
    int i;
    for (i = 0; i < SIZE; i++)
        t[i].estado = 1;
        t[i].chave = NULL;
        t[i].pos = NULL;
}

/*
// -- // -- // -- // -- //
Resolução alternativa
*/

typedef struct b_ {
    int chave;
    int pos;
    struct b_ *prox;
} bucket_;

typedef bucket_ *HASH_[SIZE];

void initT_(HASH_ t) {
    int i;
    for (i = 0; i < SIZE; i++)
        t[i]->chave = NULL;
        t[i]->pos = NULL;
        t[i]->prox = NULL;
}