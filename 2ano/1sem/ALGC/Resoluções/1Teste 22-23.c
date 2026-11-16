#include <stdlib.h>

int difInd (int a[], int b[], int N) {
    int i = 0;
    while (i < N && a[i] == b[i]) i++;
    return i;
}
/*
Pre: N >= 0
Pos: (i == N && forall{0 <= k < N} a[k] == b[k]) || (0 <= i < N && a[i] != b[i])
*/

int sacos (int p[], int N, int c) {
    // Pre: N > 0 && c > 0 && forall{0 <= k < N} 0 < p[k] <= c
    int r = 1, t = p[0], i = 1, j = 0;
    while (i < N) {
        if (t + p[i] > c) {
            j = i; t = 0; r++;
        }
        t = t + p[i]; i++;
    }
    return r;
    // Pos: 0 < r <= N && r*c >= sum{k=0, N-1} p[k]
}
/*
Inv: 0 <= r <= i && t = sum{j, i-1} p[k]
Var: N - i
*/
/*
Melhor caso: Todos os produtos cabem no mesmo saco      T(N) = 3*N (2x t+p[i], i+1)
Pior caso: Todos os produtos precisam do seu próprio saco   T(N) = 4*N (2x t+p[i], r+1, i+1)
*/

int ensaca (int p[], int N, int c) {
    int r, t;
    if (N == 0) return 0;
    r = ensaca(p+1, N-1, c);
    if (p[0] <= c) {
        t = 1 + ensaca(p+1, N-1, c-p[0]);
        if (t > r) r = t;
    }
    return r;
}
/*
Pior caso: Todos os produtos cabem no saco      T(N) = 2^N (?)
Melhor caso: Nenhum produto pode ir para o saco     T(N) = N
*/