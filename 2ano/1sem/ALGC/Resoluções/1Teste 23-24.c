#include <stdlib.h>

int addsTo10 (int x, int a[], int N) {
    int i = 0;
    while (i < N && a[i] + x != 10) i++;
    return i;
}
/*
Pre: N > 0 && 1 <= x <= 9 && forall{0 <= i < N} 1 <= a[i] <= 9
Pos: (i == N && ñ existe{0 <= i < N} a[i] + x == 10) ||
     (i < N && i >= 0 && a[i] + x == 10 && forall{0 <= k <= i-1} a[k] + x != 10)
*/
/*
Var: N - i
Inv: 0 <= i <= N && forall{0 <= k < i} a[k] + x != 10
*/
/* a[i] + x != 10
Melhor caso: O 1º elemento é 10-x                               T(N) = 1
Pior caso: O último elemento é 10-x OU nenhum elemento é 10-x   T(N) = N

Caso médio: P(elemento ser o 10-x) = 1/9

T(N) = sum{i = 1, N} (8/9)^i-1 * 1/9 + (8/9)^N * N
*/

int par10Rec (int u[], int N) {
    int p;
    if (N < 2) return 0;
    p = addsTo10(u[0], u+1, N-1);
    if (p < N-1) return 1;
    return (par10Rec(u+1, N-1));
}
/*
Melhor caso: O primeiro e o segundo elementos de u somados dão 10   T(N) = 1
Pior caso: Nenhum par dá 10     T(N) = (N-1) + T(N-1)
                                     = (N-1) + (N-2) + T(N-2)
                                    ...
                                     = N(N-1)/2 ≃ O(N^2)
*/