#include <stdlib.h>
#include <stdio.h>

struct btree {
    int info;
    int weight;
    struct btree *left, *right;
};

typedef struct btree *Btree;

int checkWeight (Btree t) {
    //Ir buscar o nó N, contar quantos tem à esquerda, à direita, somar os dois com 1 e ver se é igual ao número que está em weight
    // 0 falso, 1 verdadeiro
    int r;
    if (!t) return 0;
    if (t->left) r += t->left->weight;
    if (t->right) r += t->right->weight;

    return (r + 1 == t->weight && checkWeight(t->left) && checkWeight(t->right));
}
// A função é linear no pior caso


struct edge {
    int dest;
    struct edge *next;
};
#define MAX 100
typedef struct edge *GraphL[MAX];

void bfs(GraphL g, int n, int start, int *dist, int *predecessor) {
    int queue[MAX], front = 0, rear = 0;
    int visited[MAX] = {0};

    for (int i = 0; i < n; i++) {
        dist[i] = -1;
        predecessor[i] = -1;
    }

    dist[start] = 0;
    visited[start] = 1;
    queue[rear++] = start;

    while (front < rear) {
        int u = queue[front++];
        struct edge *temp = g[u];
        while (temp) {
            int v = temp->dest;
            if (!visited[v]) {
                visited[v] = 1;
                dist[v] = dist[u] + 1;
                predecessor[v] = u;
                queue[rear++] = v;
            }
            temp = temp->next;
        }
    }
}

int shortestBy (GraphL g, int s, int d, int i) {
    // -1 sse d não é alcançável a partir de s
    // 0 sse nenhum caminho mais curto de s até d contém i
    // 1 sse um caminho mais curto de s até d contém i
    int n = MAX;
    int dist[MAX], predecessor[MAX];
    bfs(g, n, s, dist, predecessor);

    if (dist[d] == -1) {
        return -1;
    }

    int shortestDist = dist[d];
    int tempDist[MAX];
    int tempPred[MAX];

    struct edge *temp = g[i];
    g[i] = NULL;
    bfs(g, n, s, tempDist, tempPred);
    g[i] = temp;

    if (tempDist[d] > shortestDist) {
        return 1;
    } else if (tempDist[d] == shortestDist) {
        return 0;
    }

    return -1;
}