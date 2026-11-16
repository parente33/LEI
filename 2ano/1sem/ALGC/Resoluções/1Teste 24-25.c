#include <stdlib.h>

int intSqrt (int x) {
    // Pre: …
    int r = 0, p = 1, i = 1;
    while (p <= x){
        r += 1; i += 2; p += i;
    }
    // Pos:
    return r ;
}

/*
Pre: x > 0
Pos: r*r <= x && forall{j} tq j*j <= x, r > j
*/

/*
Inv: r*r + i == p && r*r <= p <= x
Var: x - p
*/

int subsetsum (int x, int v[], int N){
    if (x==0) return 1;
    if (N==0) return 0;
    return (subsetsum (x, v+1, N-1) || subsetsum (x-v[0], v+1, N-1));
}
/**
 * Pior Caso: A função testa todos as combinações de subconjuntos possíveis de v[],
 * e portanto, faz duas chamadas a cada nível de recursão:
 *          - a 1ª chamada testa se é possível encontrar o subconjunto sem considerar v[0]
 *          - a 2ª chamada testa se é possível encontrar o subconjunto considerando v[0] na soma
 * 
 * T(N) = 2*T(N-1) + 1
 *      = 2(2*T(N-2) + 1) + 1
 *      = 2^2 * T(N-2) + 2 1
 *      = 2^3 * T(N-3) + 2^2 + 2 + 1
 *      = 2^k * T(N-k) + sum{0 <= i <= k-1} (2^i)
 * 
 * Para k = N, temos T(N) = 0
 *          T(N) = sum{0 <= i <= k-1} (2^i)
 *               = 2^N - 1
 * 
 * A função tem complexidade exponencial no pior caso
*/

int inc (char s[], int N){
    int i =N - 1;
    while (i >= 0 && s[i] == '9')
        s[i--] = '0';
    if (i < 0) return 1;
    s[i] = s[i] + 1;
    return 0;
}
/**
 * 1) Pior Caso -> Alterar todos os dígitos de '9' para '0'
 *    Altera N dígitos
 * 2) T(N) = (9/10 * (1/10)^k) * sum{0 <= k <= N-1} (k+1) ≃ N/2, ou seja,
 *    em média, a função muda metade dos caracteres da string
 * 3) Custo real = k + 1
 *    Φantigo(s) = soma dos dígitos de s / 9
 *    Φnovo(s) = soma dos dígitos do novo s / 9
 * 
 *    Φnovo(s) - Φantigo(s) = (1-9k)/9
 *    Custo amortizado = creal + (Φnovo(s) - Φantigo(s))
 *                     = 1 + 1/9 = 10/9
 * 
 *    O custo amortizado é constante
*/