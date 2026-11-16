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

typedef struct {
    int itens[MAX];
    int inicio, fim;
} Fila;

void inicializaFila (Fila *f) {
    f->inicio = 0;
    f->fim = 0;
}

int filaVazia (Fila *f) {
    return (f->inicio == f->fim);
}

void enfileira (Fila *f, int valor) {
    f->itens[f->fim++] = valor;
}

int desenfileira (Fila *f) {
    return f->itens[f->inicio++];
}

void bfs (GrafoL g, int N, int o) {
    int vis[MAX] = {0};
    Fila fila;
    inicializaFila(&fila);

    vis[o] = 1;
    enfileira(&fila, o);

    while (!filaVazia(&fila)) {
        int u = desenfileira(&fila);
        struct aresta *a = g[u];

        while (a != NULL) {
            if (!vis[a->dest]) {
                vis[a->dest] = 1;
                enfileira(&fila, a->dest);
            }
            a = a->prox;
        }
    }
}

void bfsComDist (GrafoL g, int N, int o, int dist[]) {
    int vis[MAX] = {0}, i;
    Fila fila;
    inicializaFila(&fila);

    for (i = 0; i < N; i++) {
        dist[i] = -1;
    }

    vis[o] = 1; dist[o] = 0; 
    enfileira(&fila, o);

    while (!filaVazia(&fila)) {
        int u = desenfileira(&fila);
        struct aresta *a = g[u];

        while (a != NULL) {
            if (!vis[a->dest]) {
                vis[a->dest] = 1;
                dist[a->dest] = dist[u] + 1;
                enfileira(&fila, a->dest);
            }
            a = a->prox;
        }
    }
}

void bfsCaminhoMaisCurto (GrafoL g, int N, int o, int d) {
    int vis[MAX] = {0}, i;
    int pais[MAX];
    Fila fila;
    inicializaFila(&fila);

    for (i = 0; i < N; i++) {
        pais[i] = -1;
    }
    vis[o] = 1;
    pais[o] = o;
    enfileira(&fila, o);

    while (!filaVazia(&fila)) {
        int u = desenfileira(&fila);
        struct aresta *x = g[u];

        while (x != NULL) {
            if (!vis[x->dest]) {
                vis[x->dest] = 1;
                pais[x->dest] = u;
                enfileira(&fila, x->dest);

                if (x->dest == d) {
                    break;
                }
            }
            x = x->prox;
        }
    }
    if (pais[d] == -1) {
        printf("Não há caminho entre %d e %d\n", o, d);
    } else {
        int caminho[MAX];
        int tam = 0;
        for (int v = d; v != o; v = pais[v]) {
            caminho[tam++] = v;
        }
        caminho[tam++] = o;

        printf("Caminho mais curto entre %d e %d: ", o, d);
        for (int i = tam - 1; i >= 0; i--) {
            printf("%d", caminho [i]);
            if (i > 0) {
                printf(" -> ");
            }
        }
        printf("\n");
    }
}


void bfsCompleta (GrafoL g, int N, int o, int *visitado) {
    Fila fila;
    inicializaFila(&fila);
    visitado[o] = 1;
    enfileira(&fila, o);

    while (!filaVazia(&fila)) {
        int u = desenfileira(&fila);
        struct aresta *x = g[u];

        while (x != NULL) {
            if (!visitado[x->dest]) {
                visitado[x->dest] = 1;
                enfileira(&fila, x->dest);
            }
            x = x->prox;
        }
    }
}

void travCompleta (GrafoL g, int N) {
    int vis[MAX] = {0};

    for (int i = 0; i < N; i++) {
        if (!vis[i]) {
            bfsCompleta(g,N,i,vis);
        }
    }
}

#define INTREE 0
#define FRINGE 1
#define UNSEEN 2

int AGM (GrafoL g, int N, int pais[]) {
    return AGM_sol(g, N, pais);
}

int AGM_sol (GrafoL g, int N, int pais[]) {
    int status[MAX]; int nextFr[MAX]; int custoFr[MAX];
    int fringe; int x, y, edgeCount, wxy, prev;
    struct aresta *a;

    fringe = -1;
    for (y = 1; y < N; y++) status[y] = UNSEEN;
    edgeCount = 0;

    x = 0;
    status[x] = INTREE;
    pais[x] = -1;

    while (edgeCount < N) {
        a = g[x];
        while (a) {
            y = a->dest;
            wxy = a->custo;
            if (status[y] == UNSEEN) {
                status[y] = FRINGE;
                nextFr[y] = fringe;
                fringe = y;
                pais[y] = x;
                custoFr[y] = wxy;
            } else if (status[y] == FRINGE && wxy < custoFr[y]) {
                pais[y] = x;
                custoFr[y] = wxy;
            }
            a = a->prox;
        }
        if (fringe == -1) return 0;

        prev = -1;
        x = fringe;
        for (y = fringe; nextFr[y]; y = nextFr[y]) {
            if (custoFr[nextFr[y]] < custoFr[y]) {
                x = nextFr[y];
                prev = y;
            }
        }
        if (x == fringe) fringe = nextFr[x];
        else nextFr[prev] = nextFr[x];
        nextFr[x] = 0;
        status[x] = INTREE;
        edgeCount++;
    }
    return 1;
}