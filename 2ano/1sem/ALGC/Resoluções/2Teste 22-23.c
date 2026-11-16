#include <stdlib.h>

typedef struct avlnode {
    int valor;
    int bal;
    struct avlnode *esq, *dir;
} *AVLTree;

AVLTree build (int a[], int N) {
    if (N <= 0) return NULL;
    int meio = N/2;
    AVLTree t = (AVLTree)malloc(sizeof(struct avlnode));

    t->valor = a[meio];
    t->esq = build(a, meio);
    t->dir = build(a+meio+1, N-meio-1);

    int alturaEsq = t->esq ? t->esq->bal + 1 : 0;
    int alturaDir = t->dir ? t->dir->bal + 1 : 0;

    t->bal = alturaEsq - alturaDir;

    return t;
}

#define Pai(x) ((x-1)/2)
#define Esq(x) (2*x + 1)
#define Dir(x) (2*x + 2)

void bubbleUp (int h[], int i) {
    while (i > 0) {
        int p = Pai(i);
        if (h[i] < h[p]) {
            int temp = h[i];
            h[i] = h[p];
            h[p] = temp;

            i = p;
        } else {
            break;
        }
    }
}

int decrease (int x, int y, int h[], int N) {
    int j = -1;
    for (int i = 0; i < N; i++) {
        if (h[i] == x) {
            j = i;
            break;
        }
    }
    h[j] = y;
    bubbleUp(h, j);

    return h; // ?
}

#define N 10
void dfs (int image[N][N], int vis[N][N], int i, int j, int *size) {
    vis[i][j] = 1;
    (*size)++;

    int dx[] = {-1,1,0,0};
    int dy[] = {0,0,-1,1};

    for (int k = 0; k < 4; k++) {
        int ni = i + dx[k];
        int nj = j + dy[k];

        if (ni >= 0 && ni < N && nj >= 0 && nj < N && image[ni][nj] == 1 && !vis[ni][nj]) {
            dfs(image, vis, ni, nj, size);
        }
    }
}

int maior (int image[N][N]) {
    int vis[N][N] = {0};
    int maxSize = 0;

    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            if (image[i][j] == 1 && !vis[i][j]) {
                int size = 0;
                dfs(image, vis, i, j, &size);
                if (size > maxSize) maxSize = size;
            }
        }
    }
    return maxSize;
}