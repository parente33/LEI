#include <stdlib.h>

#define Pai(x) ((x-1)/2)
#define Esq(x) (2*x + 1)
#define Dir(x) (2*x + 2)

int checkBST (int t[], int N) {
    if (N <= 0) return 0;
    for (int i = 0; i < N; i++) {
        if (t[i] == 'H') continue;

        int e = Esq(i);
        int d = Dir(i);

        if (e < N && t[e] != 'H' && t[e] >= t[i]) return 0;
        if (d < N && t[d] != 'H' && t[d] <= t[i]) return 0;
    }
    return 1;
}

int altura (int t[], int N, int i) {
    if (i >= N || t[i] == 'H') return 0;

    int alturaEsq = altura(t, N, Esq(i));
    int alturaDir = altura(t, N, Dir(i));

    if (alturaEsq == -1 || alturaDir == -1 || abs(alturaEsq - alturaDir) > 1) return -1;

    return 1 + (alturaEsq > alturaDir ? alturaEsq : alturaDir);
}

int checkAVL (int t[], int N) {
    if (!checkBST(t, N)) return 0;

    return (altura(t, N, 0) != -1);
}

struct edge {
    int dest;
    struct edge *next;
};
typedef struct edge *GrafoL[100];

int is_clique (GrafoL g, int NV, int v[], int k) {
    for (int i = 0; i < k; i++) {
        for (int j = i + 1; j < k; j++) {
            int vi = v[i], vj = v[j];

            struct edge *x = g[vi];
            int found = 0;
            while (x != NULL) {
                if (x->dest == vj) {
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