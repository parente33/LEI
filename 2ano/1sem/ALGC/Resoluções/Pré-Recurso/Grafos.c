#include <stdlib.h>

/*
Grafos (Vertices,Arestas) = (V,A)

A <= V^2

Listas de Adjacências (Tempo (V+A))

Matrizes de Adjacências (Memória V^2)
*/

#define NV 10

struct aresta {
    int dest;
    int custo;
    struct aresta *prox;
};

typedef struct aresta *Grafo[NV];

int nArestas(Grafo g) {
    int i, r = 0;
    struct aresta *x;

    for (i = 0; i < NV; i++) {
        for (x = g[i]; x != NULL; x = x->prox)
            r++;
    }
    return r;
}

/*
TnA(V,A) = sum (i=0) .. (v-1) 1 + for i

= sum (i=0) .. (v-1) 1 + sum (i=0) .. (v-1) for 1

= V + A = θ(V + A) => linear no tamanho do grafo
*/

int procuraNAOFUNCIONA(Grafo g, int a, int b) {
    if (a == b) return 1;
    struct aresta *x;
    
    for (x = g[a]; x != NULL; x = x->prox) {
        if (procura(g,x->dest,b)) return 1;
    }
    return 0;
}

int procura(Grafo g, int a, int b) {
    int v[NV], i;

    for (i = 0; i < NV; i++) v[i] = 0;

    return (procuraR(g,a,b,v));
}

// Vê se há um caminho de a até b
int procuraR(Grafo g, int a, int b, int vis[]) {
    struct aresta *x;
    vis[a] = 1;

    if (a == b) return 1;

    for (x = g[a]; x != NULL; x = x->prox) {
        if (!vis[x->dest] && procuraR(g, x->dest,b,vis)) return 1;
    }
    return 0;
}



int travessiaDF(Grafo g, int a) {
    int v[NV], i;
    for (i = 0; i < NV; i++) v[i] = 0;

    return (travR(g,a,v));
}

// Quantos destinos é que podemos chegar, a partir da mesma origem
int travR(Grafo g, int a, int vis[]) {
    int r = 1;
    struct aresta *x;
    vis[a] = 1;

    for (x = g[a]; x != NULL; x = x->prox) {
        if (!vis[x->dest]) r += travR(g,x->dest,vis);
    }
    return r;
}

// Quais são os alcançáveis
int DF(Grafo g, int x, int a[]) {
    int i;
    for (i = 0; i < NV; i++) a[i] = 0;

    return (DF_R(g,x,a));
}

int DF_preenche_array_dos_pais(Grafo g, int x, int a[], int p[]) {
    int i;
    for (i = 0; i < NV; i++) {
        a[i] = 0;
        p[i] = -1;
    }
    return (DF_preenche_R(g,x,a,p));
}

// Introdução do estado "em visita"
int DF_preenche_R(Grafo g, int x, int vis[], int p[]) {
    int r = 1;
    struct aresta *a;
    vis[x] = -1;

    for (a = g[x]; a != NULL; a = a->prox) {
        if (vis[a->dest] == 0) {
            p[a->dest] = x;
            r += DF_preenche_R(g,a->dest,vis, p);
        }
    }
    vis[x] = 1;
    return r;
}

// BREADTH-FIRST

int BF(Grafo g, int x) {
    int orla[NV], inicio, fim;
    int r = 0, vis[NV] = {0};
    struct aresta *a;

    // Inicializa a queue
    inicio = fim = 0;
    // Acrescenta o 'x'
    orla[fim++] = x;
    vis[x] = -1;

    while (inicio != fim) {
        int y = orla[inicio++];
        vis[y] = 1;
        r++;
        
        for (a = g[y]; a != NULL; a = a->prox) {
            if (vis[a->dest] == 0) {
                orla[fim++] = a->dest;
                vis[a->dest] = -1;
            }
        }
    }
    return r;
}

int BF2(Grafo g, int x, int vis[], int p[], int d[]) {
    int orla[NV], inicio, fim, y, i;
    int r = 0;
    struct aresta *a;

    for (i = 0; i < NV; i++) {
        vis[i] = 0;
        p[i] = -1;
    }
    inicio = fim = 0;
    orla[fim++] = x;
    vis[x] = -1;
    d[x] = 0;

    while (inicio != fim) {
        y = orla[inicio++];
        vis[y] = 1;
        r++;
        
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
