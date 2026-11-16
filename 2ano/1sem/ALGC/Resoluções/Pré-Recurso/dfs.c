#include <stdlib.h>
#include <stdio.h>

#define NE 0
#define MAX 100

struct aresta {
    int dest;
    int custo;
    struct aresta *prox;
};

typedef struct aresta *GrafoL[MAX];
typedef int GrafoM[MAX][MAX];

#define WHITE 0
#define GRAY 1
#define BLACK 2

void dfs (GrafoL g, int n) {
    int cor[MAX];
    for (int i = 0; i < n; i++) {
        cor[i] = WHITE;
    }

    for (int i = 0; i < n; i++) {
        if (cor[i] == WHITE) {
            dfsVisita(g,i,cor);
        }
    }
}

void dfsVisita (GrafoL g, int u, int *cor) {
    struct aresta *a;
    cor[u] = GRAY;

    while (a != NULL) {
        if (cor[a->dest] == WHITE) {
            dfsVisita(g,a->dest,cor);
        }
        a = a->prox;
    }
    cor[u] = BLACK;
}

// Testa se um grafo é cíclico ou não
int verificaCiclo (GrafoL g, int N) {
    int cor[MAX], i;
    for (i = 0; i < N; i++) {
        cor[i] = WHITE;
    }

    for (i = 0; i < N; i++) {
        if (cor[i] == WHITE) {
            if (dfsCiclo(g,i,cor)) return 1;
        }
    }

    return 0;
}

int dfsCiclo (GrafoL g, int u, int *cor) {
    cor[u] = GRAY;
    struct aresta *a = g[u];

    while (a != NULL) {
        if (cor[a->dest] == GRAY) return 1;

        if (cor[a->dest] == WHITE) {
            if (dfsCiclo(g,a->dest,cor)) return 1;
        }
        a = a->prox;
    }
    cor[u] = BLACK;
    return 0;
}

// Visita completa ao grafo
void dfsCompleta (GrafoL g, int N) {
    int vis[MAX], i;
    for (i = 0; i < N; i++) {
        vis[i] = 0;
    }

    for (i = 0; i < N; i++) {
        if (!vis[i]) dfsVisitaCompleta(g,i,vis);
    }
}

void dfsVisitaCompleta (GrafoL g, int u, int *vis) {
    vis[u] = 1;
    struct aresta *a = g[u];

    while (a != NULL) {
        if (!vis[a->dest]) {
            dfsVisitaCompleta(g,a->dest,vis);
        }
        a = a->prox;
    }
}