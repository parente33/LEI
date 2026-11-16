#include <stdlib.h>
#include <stdio.h>

#define Pai(i) ((i-1)/2)
#define Esq(i) (2*i+1)
#define Dir(i) (2*i+2)
#define H -1

// EXERCÍCIO 2 EXERCÍCIO 2 EXERCÍCIO 2 EXERCÍCIO 2 EXERCÍCIO 2 EXERCÍCIO 2 EXERCÍCIO 2 EXERCÍCIO 2 EXERCÍCIO 2 EXERCÍCIO 2 
// ALÍNEA A) ALÍNEA A) ALÍNEA A) ALÍNEA A) ALÍNEA A) ALÍNEA A) ALÍNEA A) ALÍNEA A) ALÍNEA A) ALÍNEA A) ALÍNEA A) ALÍNEA A) 
int checkBST (int t[], int N) {
    int i;
    for (i = 0; i < N; i++) {
        if (t[i] == H) continue;

        int e = Esq(i);
        int d = Dir(i);

        if (e < N && t[e] != H && t[e] >= t[i]) {
            return 0;
        }
        if (d < N && t[d] != H && t[d] <= t[i]) {
            return 0;
        }
    }
    return 1;
}

// ALÍNEA B) ALÍNEA B) ALÍNEA B) ALÍNEA B) ALÍNEA B) ALÍNEA B) ALÍNEA B) ALÍNEA B) ALÍNEA B) ALÍNEA B) ALÍNEA B) ALÍNEA B) 
// Altura da sub-árvore esq e dir diferem em, no máximo, uma unidade
int altura (int t[], int N, int i) {
    if (i >= N || t[i] == H) return 0;

    int alturaEsq = altura(t, N, Esq(i));
    int alturaDir = altura(t, N, Dir(i));

    if (alturaEsq == -1 || alturaDir == -1 || abs(alturaEsq - alturaDir) > 1) {
        return -1;
    }

    return 1 + (alturaEsq > alturaDir ? alturaEsq : alturaDir);
}

int checkAVL (int t[], int N) {
    if (!checkBST) return 0;

    return altura(t, N, 0) != -1;
}

// EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 
struct edge {
    int dest;
    struct edge *next;
};
#define MAX 100

typedef struct edge *GrafoL[MAX];

int is_clique (GrafoL g, int NV, int v[], int k) {
    // Se para cada um dos elementos do sub-conjunto, conseques chegar a todos os outros elementos do sub-conjunto
    // 0 falso, 1 verdadeiro
    if (NV <= 0 || k <= 0 || k > NV) return 0;

    for (int i = 0; i < k; i++) {
        for (int j = i + 1; j < k; j++) {
            int found = 0;
            struct edge *x = g[v[i]];
            while (x != NULL) {
                if (x->dest == v[j]) {
                    found = 1;
                    break;
                }
                x = x->next;
            }
            if (!found) return 0;
        }
    }

    return 1;
}