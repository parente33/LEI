#include <stdlib.h>

int prefSum (int v[], int N, int lim){
    // pre: N >= 0 && lim >= 0
    int sum = 0, i = 0;
    while (i < N && sum + v[i] <= lim){
        // inv: ??
        sum = sum + v[i]; i = i + 1;
    }
    // pos: ??
    return i;
}

/*
Pós: (0 <= i < N && ∑j=0 i-1 (v[j]) <= lim && ∑j=0 i (v[j]) > lim) ||
     (i == N && ∑i=0 N-1 (v[i]) <= lim)

Inv: (0 <= i <= N && forall{0 <= k <= i} ∑i=0 k (v[i]) <= lim)
*/

typedef struct abin {
    int valor;
    struct abin *esq, *dir;
} *ABin;

ABin fromArray (int v[], int N){
    ABin r=NULL; int m;
    if (N>0) {
    r = malloc (sizeof (struct abin));
    m = N/2;
    r->esq = fromArray (v,m);
    r->valor = v[m];
    r->dir = fromArray (v+m+1,N-m-1);
    }
return r;
}

/*
Caso N = 0: T(0) = 0

Caso Geral: 1 acesso (v[m]) para obter o valor da raiz
            Para a subárvore esquerda -> fromArray(v,m)
            Para a subárvore direita  -> fromArray(v + m + 1, N - m - 1)

    T(N) = T(m) + T(N-m-1) + 1, onde m = N/2
         = T(N/2) + T(N/2 - 1) + 1 ≃ 2*T(N/2) + 1
         = 2[2*T(N/4) + 1] + 1 = 4*T(N/4) + 2 + 1
         = 2^k * T(N/2^k) + (2^k - 1), onde k = log2(N)
         = 2^(log2(N)) * T(1) + (2^(log2(N)) - 1) = N + (N - 1) = 2*N - 1
*/

/*
O custo real de uma garbage collection é de O(N + k), onde N é o tamanho da tabela e k é o nº de elementos ativos
Portanto, é linear no tamanho da tabela

Custo de N/4 remoções = N/4, logo
Custo total = N/4 + N

Custo amortizado = O((N/4 + N)/(N/4)) = O(4 + 1) = O(5), que é constante 
*/

/*
Para realizar este problema, usa-se uma abordagem DFS:
    - Percorrer a matriz e, para cada ponto, calcular o maior caminho descendente possível começando nesse ponto
    - Usar uma matriz auxiliar (dp[N][N]) para armazenar o maior caminho a partir de cada ponto da matriz -> evita
    recalcular caminhos a partir de pontos já visitados
    - Implementar uma função recursiva para explorar todos os movimentos válidos a partir de cada ponto
 */

#define N 10
int dp[N][N];

int calcula_descida (int mapa[N][N], int x, int y) {
    if (dp[x][y] != -1) return dp[x][y];
    int max_len = 1;

    int dx[] = {-1,1,0,0};
    int dy[] = {0,0,-1,1};

    for (int k = 0; k < 4; k++) {
        int nx = x + dx[k];
        int ny = y + dy[k];

        if (nx >= 0 && nx < N && ny >= 0 && ny < N && mapa[nx][ny] < mapa[x][y]) {
            max_len = max(max_len, 1 + calcula_descida(mapa, nx, ny));
        }
    }
    dp[x][y] = max_len;
    return max_len;
}

int maior_descida (int mapa[N][N]) {
    int max_caminho = 0;

    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            dp[i][j] = -1;
        }
    }
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            max_caminho = max(max_caminho, calcula_descida(mapa, i , j));
        }
    }
    return max_caminho;
}