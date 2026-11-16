#include <stdlib.h>

/*
Tabelas de Hash => Tprocura(N) = θ(1)

Árvores binárias de procura "baixinhas" => Tprocura(N) = θ(log N)

Árvores equilibradas sse |size(esq) - size(dir)| <= 1
Árvores balanceadas sse |altura(esq) - altura(dir)| <= 1
*/

typedef struct abin {
    int bal;
    int chave;
    struct abin *esq, *dir;
} *AVL;

#define ESQ 1
#define BAL 0
#define DIR -1

AVL add(int x, AVL a, int *aum) {
    if (a == NULL) {
        a = malloc(sizeof(struct abin));
        a->chave = x;
        a->bal = BAL;
        a->esq = a->dir = NULL;
        *aum = 1;
    }
    if (a->chave > x) {
        a->esq = add(x, a->esq, aum);
        if (*aum) {
            if (a->bal == DIR) {
                a->bal = BAL;
                *aum = 0;
            }
            if (a->bal == BAL) {
                a->bal = ESQ;
                *aum = 1;
            }
        }
    } else {
        a = corrigeEsq(a);
        *aum = 0;
    }
}

AVL corrigeEsq(AVL a) {
    AVL b = a->esq;

    if (b->bal == ESQ) {
        a = rodaDir(a);
        a->bal = BAL;
        a->dir->bal = BAL;
    }
    if (b->bal == DIR) {
        a->esq = rodaEsq(a->esq);
        a = rodaDir(a);
        a->bal = BAL;
    }
}