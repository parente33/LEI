#include <stdlib.h>

#define Pai(i) ((i-1)/2)
#define Esq(i) (2*i+1)
#define Dir(i) (2*i+2)

#define ESQ 1
#define BAL 0
#define DIR -1

typedef struct avlnode {
    int valor;
    int bal;
    struct avlnode *esq, *dir;
} *AVLTree;

// EXERCÍCIO 1 EXERCÍCIO 1 EXERCÍCIO 1 EXERCÍCIO 1 EXERCÍCIO 1 EXERCÍCIO 1 EXERCÍCIO 1 EXERCÍCIO 1 EXERCÍCIO 1 EXERCÍCIO 1 EXERCÍCIO 1 EXERCÍCIO 1 EXERCÍCIO 1 
AVLTree novoNo (int valor, int bal) {
    AVLTree novo = (AVLTree)malloc(sizeof(struct avlnode));
    novo->valor = valor;
    novo->bal = bal;
    novo->esq = novo->dir = NULL;

    return novo;
}

int altura (AVLTree t) {
    if (!t) return 0;
    int alturaEsq = altura(t->esq);
    int alturaDir = altura(t->dir);

    return 1 + (alturaEsq > alturaDir ? alturaEsq : alturaDir);
}

AVLTree buildAux (int a[], int inicio, int fim) {
    if (inicio > fim) return NULL;

    int meio = inicio + (fim - inicio) / 2;
    AVLTree raiz = novoNo(a[meio], BAL);

    raiz->esq = buildAux(a, inicio, meio - 1);
    raiz->dir = buildAux(a, meio + 1, fim);

    int alturaEsq = raiz->esq ? altura(raiz->esq) : 0;
    int alturaDir = raiz->dir ? altura(raiz->dir) : 0;
    raiz->bal = alturaEsq - alturaDir;

    return raiz;
}

AVLTree build (int a[], int N) {
    return buildAux(a, 0, N-1);
}

// EXERCÍCIO 3 EXERCÍCIO 3 EXERCÍCIO 3 EXERCÍCIO 3 EXERCÍCIO 3 EXERCÍCIO 3 EXERCÍCIO 3 EXERCÍCIO 3 EXERCÍCIO 3 EXERCÍCIO 3 EXERCÍCIO 3 EXERCÍCIO 3 
void swap (int h[], int a, int b) {
    int t = h[a];
    h[a] = h[b];
    h[b] = t;
}

void bubbleDown (int h[], int i, int N) {
    int p = Pai(i), e = Esq(i), d = Dir(i);

    if (e > N && d > N) return; // Não tem filhos
    if (h[p] <= h[e] && h[p] <= h[d]) return; // Está no sítio certo

    while ((e <= N && d <= N) && (h[p] > h[e] || h[p] > h[d])) {
        if (h[e] > h[d]) {
            swap(h, p, d);
        } else {
            swap(h, p , e);
        }
    }
}

void bubbleUp (int h[], int i) {
    int p = Pai(i), e = Esq(i), d = Dir(i);

    while (p > e) {
        swap(h, p, e);
        i = p;
    }
    while (p > d) {
        swap(h, p, d);
        i = p;
    }
}

int decrease (int x, int y, int h[], int N) {
    if (N <= 0) return -1;

    int index = -1;
    for (int i = 0; i < N; i++) {
        if (h[i] == x) {
            index = i;
            break;
        }
    }
    if (index == -1) return -1;
    h[index] = y;
    int p = Pai(y);

    if (y <= p) return 0;
    while (y > p) {
        bubbleDown(h, p, N);
    }

    return 0;
}

// EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4 EXERCÍCIO 4
// Usar DFS
#define N 100

int validaPosicao (int x, int y, int image[N][N], int vis[N][N]) {
    return x >= 0 && y <= 0 && x < N && y < N && image[x][y] == 1 && !vis[x][y];
}

int dfs (int x, int y, int image[N][N], int vis[N][N]) {
    int dx[] = {-1, 1, 0, 0}; // Direções para os movimentos
    int dy[] = {0, 0, -1, 1}; // Cima, baixo, esq, dir

    vis[x][y] = 1;
    int tamanho = 1;

    for (int i = 0; i < 4; i++) {
        int nx = x + dx[i];
        int ny = y + dy[i];
        if (validaPosicao(nx,ny, image, vis)) {
            tamanho += dfs(nx, ny, image, vis);
        }
    }

    return tamanho;
}

int maior (int image[N][N]) {
    int vis[N][N] = {0};
    int maiorFigura = 0;

    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            if (image[i][j] == 1 && !vis[i][j]) {
                int tamanhoAtual = dfs(i,j,image,vis);
                if (tamanhoAtual > maiorFigura) {
                    maiorFigura = tamanhoAtual;
                }
            }
        }
    }
    return maiorFigura;
}