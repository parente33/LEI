#include <stdlib.h>

#define HSize 10
#define Del 2
#define Used 1
#define Free 0
typedef struct bucket {
    int status;
    int value;
} THash [HSize];

int hash (int k) {
    return k % HSize;
}

int max_probe (THash h) {
    int max_probe_count = 0;

    for (int i = 0; i < HSize; i++) {
        if (h[i].status == 1) {
            int probe_count = 1;
            int original_pos = hash(h[i].value) % HSize;
            int current_pos = i;

            while (current_pos != original_pos) {
                probe_count++;
                current_pos = (current_pos + 1) % HSize;
            }

            if (probe_count > max_probe_count) {
                max_probe_count = probe_count;
            }
        }
    }
    return max_probe_count;
}

// Ex 3
typedef struct abin {
    int valor;
    struct abin *esq, *dir;
} *Abin;

int toArray (Abin a, int h[], int N) {
    if (a == NULL || N <= 0) return 0;

    int i = 0;
    Abin fila[N];
    int front = 0, back = 0;

    fila[back++] = a;

    while (front < back && i < N) {
        Abin atual = fila[front++];
        h[i++] = atual->valor;

        if (atual->esq != NULL) fila[back++] = atual->esq;
        if (atual->dir != NULL) fila[back++] = atual->dir;
    }
    return i;
}

typedef struct aresta {
    int dest;
    struct aresta *prox;
} *GrafoL;

// Ex 4
void DFS (GrafoL g[], int v, int comp[], int marca, int n) {
    comp[v] = marca;
    for (struct aresta *a = g[v]; a != NULL; a = a->prox) {
        if (comp[a->dest] == -1) {
            DFS(g, a->dest, comp, marca, n);
        }
    }
}

int componentesOK(GrafoL g[], int c[], int n) {
    int *comp = malloc(n * sizeof(int));
    for (int i = 0; i < n; i++) comp[i] = -1;

    int marca = 0;
    for (int i = 0; i < n; i++) {
        if (comp[i] == -1) {
            DFS(g, i, comp, marca++, n);
        }
    }
    for (int i = 0; i < n; i++) {
        if (comp[i] != c[i]) {
            free(comp);
            return 0;
        }
    }
    free(comp);
    return 1;
}