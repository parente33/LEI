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

// CONVERSÕES CONVERSÕES CONVERSÕES CONVERSÕES CONVERSÕES CONVERSÕES CONVERSÕES CONVERSÕES CONVERSÕES CONVERSÕES CONVERSÕES CONVERSÕES CONVERSÕES 
// Constrói o grafo em listas de adjacências a partir de uma matriz
void graphMtoL (GrafoM gm, GrafoL gl) {
    int i, j;
    for (i = 0; i < MAX; i++) {
        gl[i] = NULL;
        for (j = 0; j < MAX; j++) {
            if (gm[i][j] != NE) {
                GrafoL novaAresta = malloc(sizeof(struct aresta));
                novaAresta[i]->dest = j;
                novaAresta[i]->custo = gm[i][j];
                novaAresta[i]->prox = gl[i];
                gl[i] = novaAresta;
            }
        }
    }
}

// Constrói o grafo em matriz a partir de listas de adjacência
void graphLtoM (GrafoM gm, GrafoL gl) {
    int i, j, k;
    struct aresta *x;

    for (j = 0; j < MAX; j++) {
        for (k = 0; k < MAX; k++) {
            gm[j][k] = NE;
        }
    }

    for (i = 0; i < MAX; i++) {
        for (x = gl[i]; x != NULL; x = x->prox) {
            gm[i][x->dest] = x->custo;
        }
    }
}

// GRAU DE ENTRADA GRAU DE ENTRADA GRAU DE ENTRADA GRAU DE ENTRADA GRAU DE ENTRADA GRAU DE ENTRADA GRAU DE ENTRADA GRAU DE ENTRADA GRAU DE ENTRADA 
// Grau de entrada do vértice i = quantas arestas têm como destino i
int grauEntrM (GrafoM gm, int v) {
    int i, r = 0;
    for (i = 0; i < MAX; i++) {
        if (gm[i][v] != NE) r++;
    }

    return r;
}

// Maior grau de entrada entre todos os vértices do grafo
int grauEntrM2 (GrafoM gm) {
    int i, j, r, max = 0;
    for (j = 0; j < MAX; j++) {
        r = 0;
        for (i = 0; i < MAX; i++) {
            if (gm[i][j] != NE) {
                r++;
            }
        }
        if (r > max) {
            max = r;
        }
    }

    return max;
}

int grauEntrL (GrafoL gl, int v) {
    int i, r = 0;
    struct aresta *x;

    for (i = 0; i < MAX; i++) {
        for (x = gl[i]; x != NULL; x = x->prox) {
            if (x->dest == v) r++;
        }
    }

    return r;
}

int grauEntrL2 (GrafoL gl) {
    int i, j;
    struct aresta *x;
    int aux[MAX];

    for (j = 0; j < MAX; j++) {
        aux[j] = 0;
    }

    for (i = 0; i < MAX; i++) {
        for (x = gl[i]; x != NULL; x = x->prox) {
            aux[x->dest]++;
        }
    }

    return maximum(aux, MAX);
}

int maximum (int v[], int N) {
    int i, max = 0;
    for (i = 0; i < N; i++) {
        if (v[i] > max) {
            max = v[i];
        }
    }

    return max;
}

// GRAU DE SAÍDA GRAU DE SAÍDA GRAU DE SAÍDA GRAU DE SAÍDA GRAU DE SAÍDA GRAU DE SAÍDA GRAU DE SAÍDA GRAU DE SAÍDA GRAU DE SAÍDA GRAU DE SAÍDA
// Grau de saída do vértice i = quantas arestas saem de i
int grauSaidaM (GrafoM gm, int v) {
    int j, r = 0;
    for (j = 0; j < MAX; j++) {
        if (gm[v][j] != NE) r++;
    }

    return r;
}

int grauSaidaM2 (GrafoM gm) {
    int i, j, r, max = 0;
    for (i = 0; i < MAX; i++) {
        r = 0;
        for (j = 0; j < MAX; j++) {
            if (gm[i][j] != NE) r++;
        }
        if (r > max) max = r;
    }

    return max;
}

int grauSaidaL (GrafoL gl, int v) {
    int r = 0;
    struct aresta *x = gl[v];
    while (x != NULL) {
        r++;
        x = x->prox;
    }
    return r;
}

int grauSaidaL2 (GrafoL gl) {
    int i;
    int aux[MAX];
    struct aresta *x;

    for (i = 0; i < MAX; i++) {
        aux[i] = 0;
    }

    for (i = 0; i < MAX; i++) {
        for (x = gl[i]; x != NULL; x = x->prox) {
            aux[i]++;
        }
    }
    return maximum(aux, MAX);
}

// CAPACIDADE DE UM VÈRTICE V CAPACIDADE DE UM VÈRTICE V CAPACIDADE DE UM VÈRTICE V CAPACIDADE DE UM VÈRTICE V CAPACIDADE DE UM VÈRTICE V
// Capacidade de um vértice v = diferença entre a soma dos pesos das arestas que têm v como destino e a soma dos pesos das arestas que têm v como origem
int capacidadadeL (GrafoL gl, int v) {
    int i, r1 = 0, r2 = 0;
    struct aresta *x;

    for (i = 0; i < MAX; i++) {
        for (x = gl[i]; x != NULL; x = x->prox) {
            if (x->dest == v) {
                r1 += x->custo;
            }
            if (i == v) {
                r2 += x->custo;
            }
        }
    }

    return (r1-r2);
}

int maxCap (GrafoL gl) {
    int i;
    struct aresta *x;
    int aux[MAX] = {0};

    for (i = 0; i < MAX; i++) {
        for (x = gl[i]; x != NULL; x = x->prox) {
            aux[i] -= gl[i]->custo;
            aux[x->dest] += x->custo;
        }
    }

    return maximum(aux, MAX);
}

// COLORAÇÂO DO GRAFO COLORAÇÂO DO GRAFO COLORAÇÂO DO GRAFO COLORAÇÂO DO GRAFO COLORAÇÂO DO GRAFO COLORAÇÂO DO GRAFO COLORAÇÂO DO GRAFO COLORAÇÂO DO GRAFO
int colorOK (GrafoL g, int color[]) {
    int i;
    struct aresta *x;

    for (i = 0; i < MAX; i++) {
        for (x = g[i]; x != NULL; x = x->prox) {
            if (color[i] == color[x->dest]) return -1;
        }
    }
    return 0;
}

// INVERSO DO GRAFO INVERSO DO GRAFO INVERSO DO GRAFO INVERSO DO GRAFO INVERSO DO GRAFO INVERSO DO GRAFO INVERSO DO GRAFO INVERSO DO GRAFO INVERSO DO GRAFO
void inverte (GrafoL in, GrafoL out) {
    int i;
    struct aresta *x;

    for (i = 0; i < MAX; i++) {
        out[i] = NULL;
        for (x = in[i]; x != NULL; x = x->prox) {
            struct aresta *novo = malloc(sizeof(struct aresta));
            novo->dest = i;
            novo->custo = x->custo;
            novo->prox = out[x->dest];
            out[x->dest] = novo;
        }
    }
}