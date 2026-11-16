#include <stdlib.h>

#define NV 10

struct aresta {
    int dest;
    struct aresta *prox;
};

typedef struct aresta *GrafoL[NV];

int *bfs (GrafoL g, int i);

int middle_point (GrafoL g, int a, int b) {
    int *dA = bfs(g, a);
    int *dB = bfs(g, b);

    int melhor_vert = -1;
    int melhor_soma = __INT_MAX__;

    for (int i = 0; i < NV; i++) {
        if (dA[i] != __INT_MAX__ && dB[i] != __INT_MAX__) {
            int soma = dA[i] + dB[i];
            if (soma < melhor_soma) {
                melhor_soma = soma;
                melhor_vert = i;
            }
        }
    }
    free(dA);
    free(dB);
    return melhor_vert;
}