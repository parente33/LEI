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

// FUNÇÕES TRAVESSIAS DF FUNÇÕES TRAVESSIAS DF FUNÇÕES TRAVESSIAS DF FUNÇÕES TRAVESSIAS DF FUNÇÕES TRAVESSIAS DF FUNÇÕES TRAVESSIAS DF FUNÇÕES TRAVESSIAS DF
// Quantos alcançavéis um vértice tem
int DF (GrafoL g, int x) {
    int v[MAX], i;
    for (i = 0; i < MAX; i++) {
        v[i] = 0;
    }
    return (DF_R(g,x,v));
}

int DF_R (GrafoL g, int x, int vis[]) {
    int r = 1;
    struct aresta *a;
    vis[x] = 1;

    for (a = g[x]; a != NULL; a = a->prox) {
        if (!vis[a->dest]) {
            r += DF_R(g,a->dest, vis);
        }
    }

    return r;
}

// Quais são os alcançavéis
int DF2 (GrafoL g, int x, int a[]) {
    int i;
    for (i = 0; i < MAX; i++) {
        a[i] = 0;
    }
    return (DF_R(g,x,a));
}

// Preenche o array dos pais também
int DF3 (GrafoL g, int x, int a[], int p[]) {
    int i;
    for (i = 0; i < MAX; i++) {
        a[i] = 0; p[i] = -1;
    }
    return (DF3_R2(g,x,a,p));
}

int DF3_R2 (GrafoL g, int x, int vis[], int p[]) {
    int r = 1;
    struct aresta *a;
    vis[x] = -1; // Estado "em visita"

    for (a = g[x]; a != NULL; a = a->prox) {
        if (vis[a->dest] == 0) {
            p[a->dest] = x;
            r += DF_2(g,a->dest,vis);
        }
    }
    vis[x] = 1;
    return r;
}

// FUNÇÕES TRAVESSIAS BF FUNÇÕES TRAVESSIAS BF FUNÇÕES TRAVESSIAS BF FUNÇÕES TRAVESSIAS BF FUNÇÕES TRAVESSIAS BF FUNÇÕES TRAVESSIAS BF FUNÇÕES TRAVESSIAS BF
int BF (GrafoL g, int x) {
    int orla[MAX], inicio, fim;
    int r = 0, vis[MAX] = {0};
    struct aresta *a;

    inicio = fim = 0; orla[fim++] = x; // Inicializar a queue
    vis[x] = -1;

    while (inicio != fim) {
        int y = orla[inicio++];
        vis[y] = 1; r++;
        for (a = g[y]; a != NULL; a = a->prox) {
            if (vis[a->dest] == 0) {
                orla[fim++] = a->dest;
                vis[a->dest] = -1;
            }
        }
    }
    return r;
}

int BF2 (GrafoL g, int x, int vis[], int p[], int d[]) {
    int orla[MAX], inicio, fim, y, i;
    int r = 0; struct aresta *a;

    for (i = 0; i < MAX; i++) {
        vis[i] = 0;
        p[i] = -1;
    }
    
    inicio = fim = 0; orla[fim++] = x;
    vis[x] = -1; d[x] = 0;

    while (inicio != fim) {
        y = orla[inicio++];
        vis[y] = 1; r++;
        for (a = g[y]; a != NULL; a = a->prox) {
            if (vis[a->dest] == 0) {
                p[a->dest] = y;
                orla[fim++] = a->dest;
                d[a->dest] = d[y] + 1;
                vis[a->dest] = -1;
            }
        }
    }
    return r;
}