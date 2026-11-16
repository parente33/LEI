#include <stdlib.h>

int bsearch (int a[], int x, int N) {
    // Pre: ...
    int i = 0;
    while (i <N  && a[i] != 'H' && a[i] != x)
        if (a[i] > x) i = 2*i+1; else i = 2*i+2;
    if (i == N || a[i] == 'H') i = -1;
    // Pos: ...
    return i;
}

/*
Pre: N > 0 && x != 'H'

Pos: (0 <= i < N && existe{0 <= k < N} tq a[k] == x && a[k] != 'H') ||
     ((i == N && ñ existe{0 <= k < N} tq a[k] == x) || (a[i] == 'H'))

Inv: O índice i é um índice válido no array e representa o nó atual da BST
que está a ser inspecionado. Para todos os índices anteriormente visitados
na busca, sabemos que nenhum deles contém o número pretendido

Var: log2(N) - log2(i + 1)
log2(N) representa a altura máxima da árvore (o nº de níveis da BST)
log2(i + 1) representa a profundidade do índice i na árvore
*/

int bsearch2 (int a[], int i, int x, int N) {
    if (i >= N || a[i] == 'H') return -1 ;
    if (a[i] > x) return bsearch2(a, (2*i+1), x, N);
    if (a[i] < x) return bsearch2(a, (2*i+2), x, N);
    return i ;
}

/*
Melhor caso: O elemento que procuramos é o da raiz (faz 2 comparações (1 a[i] > x, e outra a[i] < x)) T(N) = O(2)

Pior caso: O elemento que procuramos é o mais profundo à direita (faz 2 comparações por nível, logo, 2.log2(N) comparações)
           T(N) = 2 + log2(N)*T(N/2)


Caso médio:
    Profundidade média = Soma das profundidades de todos os nós / Número total de nós
    Soma das profundidades = sum{k=0; h-1} k * 2^k
                           = (h - 2) * 2^h + 2
    Nº total de nós: N = 2^h - 1

    Prof. média = ((h-2) * 2^h + 2) / (2^h - 1)
                ≃ log2(N) - 1 = O(log(N))
*/

struct edge {
    int dest;
    struct edge *next;
};
typedef struct edge *GrafoL[10];

int dfs_cycle (GrafoL g, int x, int vis[], int in_stack[]) {
    vis[x] = 1;
    in_stack[x] = 1;

    struct edge *adj = g[x];
    while (adj != NULL) {
        int vizinho = adj->dest;

        if (in_stack[vizinho]) return 1;
        if (!vis[vizinho]) {
            if (dfs_cycle(g, vizinho, vis, in_stack)) return 1;
        }
        adj = adj->next;
    }
    in_stack[x] = 0;
    return 0;
}

int is_cycle (GrafoL g, int x) {
    int vis[10] = {0};
    int in_stack[10] = {0};

    return dfs_cycle(g, x, vis, in_stack);
}