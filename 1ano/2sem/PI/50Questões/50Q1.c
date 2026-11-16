#include <stdio.h>
#include <math.h>
#include <ctype.h>
#include <string.h>

/* Funções auxiliares */

int mystrlen (char s[]) {
    int r = 0, i = 0;
    while (s[i] != '\0') {
        r++;
        i++;
    }
    return r;
}

int eVogal (char s[], int i) {
    return (s[i] == 'a' || s[i] == 'e' || s[i] == 'i' || s[i] == 'o' || s[i] == 'u'
            || s[i] == 'A' || s[i] == 'E' || s[i] == 'I' || s[i] == 'O' || s[i] == 'U');
}

int min (int a[], int i, int b[], int j) {
    int k = a[i];
    int l = b[j];

    return (k < l ? k : l);
}

int max (int a[], int i, int b[], int j) {
    int k = a[i];
    int l = b[j];

    return (k < l ? l : k);
}

int dist (int a, int b, int c, int d) {
    return sqrt(((a-c)*(a-c) + (b-d)*(b-d)));
}

/* 1
Definir um programa que lê uma sequência de nºs inteiros terminada com o nº 0 e imprime no ecrã o maior da sequência*/
void imprimirMaior() {
    int a;
    int max = a;
    scanf("%d", &a);
    while (a != 0) {
        if (a > max) max = a;
        scanf("%d", &a);
    }
    printf("O máximo da sequência inserida é: %d\n", max);
}

/* 2
Definir um programa que lê uma seq de nºs inteiros terminada com 0 e imprime a média da seq*/
void imprimeMedia() {
    int a;
    int media = 0;
    int soma = 0;
    int n = 1;
    scanf("%d", &a);
    while (a != 0) {
        soma += a;
        media = soma / n;
        scanf("%d", &a);
        n++;
    }
    printf("A média da sequência é: %d\n", media);
}

/*3
Definir um programa que lê uma seq de nºs inteiros terminada com 0 e imprime o segundo maior elemento*/
void imprimeSegMaior() {
    int a;
    int max = a;
    int segmaior;
    scanf("%d", &a);
    while (a != 0) {
        if (a > max) {
            segmaior = max;
            max = a;
        }
        if (a < max && a > segmaior) segmaior = a;
        scanf("%d", &a);
    }
    printf("O segundo maior da sequência é: %d\n", segmaior);
}

/*4
Definir uma função que calcula o nº de bits iguais a 1 numa representação binaria de um nº x*/
int bitsUm (unsigned int n) {
    int c = 0;
    while (n != 0) {
        if (n%2 == 1) {
            c++;
        } n /= 2;
    }
    return c;
}

/*5
Definir uma função que calcula o nº de bits a 0 no final da representação binária de um nº
(ie, o expoente da maior potência de 2 que é divisor desse nº)*/
int trailingZ (unsigned int n) {
    int c = 0;
    while ((n & 1) == 0) {
        c++;
        n >>= 1;
    }
    return c;
}

/*6
Definir uma função que calcula o nº de dígitos necessários para escrever um inteiro em base decimal*/
int qDig (unsigned int n) {
    if (n == 0) return 1;
    int i = 0;
    while (n != 0) {
        n /= 10;
        i++;
    }
    return i;
}

/*7
Apresentar uma definição da função pré-definida strcat*/
char *mystrcat (char s1[], char s2[]) {
    int i, j;
    if (s1 == NULL) return s2;
    if (s2 == NULL) return s1;

    for (i = 0; s1[i]; i++);
    for (j = 0; s2[j]; j++) {
        s1[i+j] = s2[j];
    }
    s1[i+j] = '\0';

    return s1;
}

/*8
Apresentar uma definição da função pré-definida strcpy*/
char *mystrcpy (char *dest, char source[]) {
    int i,j;
    if (source == NULL) return NULL;
    for (i = 0; source[i]; i++) {
        *(dest+i) = source[i];
    } *(dest+i) = '\0';

    return dest;
}

/*9
Apresentar uma definição da função pré-definida strcmp*/
int mystrcmp (char s1[], char s2[]) {
    int i = 0;
    while (s1[i] == s2[i]) {
        if (s1[i] == '\0') return 0;

        i++;
    }
    return s1[i] - s2[i];
}

/*10
Apresentar uma definição da função pré-definida strstr*/
char *mystrstr (char s1[], char s2[]) {
    if (*s2 == '\0') return s1;
    int j;
    for (int i = 0; s1[i] != '\0'; i++) {
        for (j = 0; s2[j] != '\0'; j++) {
            if (s1[i + j] != s2[j]) break;
        }
        if (s2[j] == '\0') return &s1[i];
    }
    return NULL;
}

/*11
Definir uma função que inverte uma string*/
void strrev (char s[]) {
    int len = mystrlen(s);
    int j = 0;
    char aux[len + 1];
    for (int i = len - 1; i >= 0; i--) {
        aux[j] = s[i];
        j++;
    }
    aux[j] = '\0';

    printf("%s\n", aux);
}

/*12
Definir uma função que retira todas as vogais de uma string*/
void strnoV (char s[]) {
    int len = mystrlen(s), i, j = 0;
    char aux[len + 1];
    for (i = 0; i < len; i++) {
        if (eVogal(s,i)) continue;
        else {
            aux[j] = s[i];
            j++;
        }
    }
    aux[j] = '\0';

    printf("%s\n", aux);
}

/*13
Definir uma função que dado um texto t com várias palavras, e um nº n, trunca todas as
palavras de forma a terem no máximo n caracteres*/
void truncW (char t[], int n) {
    int i = 0, j;
    while (t[i] != '\0') {
        if (!isspace(t[i])) {
            for (j = 0; j < n && t[i] != '\0' && !isspace(t[i]); j++, i++) {
                t[j] = t[i];
            }
            t[j] = '\0';

            while (t[i] != '\0' && !isspace(t[i])) {
                i++;
            }
            if (t[i] == ' ') {
                t[i++] = ' ';
            }
        }
    }
}

/*14
Definir uma função que determina qual é o caracter mais frequente numa string*/
char charMaisfreq (char s[]) {
    if (s[0] == '\0') return 0;
    int freq[256] = {0};
    int maxFreq = 0;
    char maisFreq = s[0];

    for (int i = 0; s[i] != '\0'; i++) {
        freq[(unsigned char)s[i]]++;
        if (freq[(unsigned char)s[i]] > maxFreq) {
            maxFreq = freq[(unsigned char)s[i]];
            maisFreq = s[i];
        }
    }
    return maisFreq;
}

/*15
Definir uma função que dada uma string, calcula o comprimento da maior sub-string com caracteres iguais*/
int iguaisConsecutivos (char s[]) {
    if (s[0] == '\0') return 0;
    int maxLen = 1, currLen = 1;

    for (int i = 1; s[i] != '\0'; i++) {
        if (s[i] == s[i - 1]) {
            currLen++;
        } else {
            if (currLen > maxLen) maxLen = currLen;
            currLen = 1;
        }
    }
    if (currLen > maxLen) maxLen = currLen;

    return maxLen;
}

/*16
Definir uma função que dada uma string, calcula o comprimento da maior sub-string com caracteres diferentes*/
int difConsecutivos (char s[]) {
    if (s[0] == '\0') return 0;
    int maxLen = 1, currLen = 1;
    
    for (int i = 1; s[i] != '\0'; i++) {
        if (s[i] != s[i - 1]) {
            currLen++;
        } else {
            if (currLen > maxLen) maxLen = currLen;
            currLen = 1;
        }
    }
    if (currLen > maxLen) maxLen = currLen;

    return maxLen;
}

/*17
Definir uma função que calcula o comprimento do maior prefixo comum entre duas strings*/
int maiorPrefixo (char s1[], char s2[]) {
    int max = 0;
    for (int i = 0; (s1[i] != '\0' || s2[i] != '\0') && s1[i] == s2[i]; i++) {
        max++;
    }

    return max;
}

/*18
Definir uma função que calcula o comprimento do maior sufixo comum entre duas string*/
int maiorSufixo (char s1[], char s2[]) {
    int c = -1, i, j;
    for (i = 0; s1[i] != '\0'; i++);
    for (j = 0; s2[j] != '\0'; j++);

    while (s1[i--] == s2[j--]) {
        c++;
    }
    return c;
}

/*19
Definir uma função que calcula o tamanho do maior sufixo de uma string que é um prefixo de outra string*/
int sufPref (char s1[], char s2[]) {
    int len1 = mystrlen(s1), len2 = mystrlen(s2);
    int maxLen = 0;

    for (int i = 0; i < len1; i++) {
        int j = 0;
        while ((i + j < len1) && (j < len2) && (s1[i + j] == s2[j])) {
            j++;
        }
        if (i + j == len1) {
            maxLen = j;
            break;
        }
    }
    return maxLen;
}

/*20
Definir uma função que conta as palavras de uma string*/
int contaPal (char s[]) {
    int c = 0;
    for (int i = 0; s[i] != '\0'; i++) {
        if (!isspace(s[i]) && isspace(s[i + 1])) c++;
        if (!isspace(s[i]) && !s[i + 1]) c++;
    }

    return c;
}

/*21
Definir uma função que retorna o nº de vogais de uma string*/
int contaVogais (char s[]) {
    int c = 0;
    for (int i = 0; s[i] != '\0'; i++) {
        if (eVogal(s,i)) c++;
    }

    return c;
}

/*22
Definir uma função que testa se todos os caracteres de uma string aparecem noutra string*/
int contida (char a[], char b[]) {
    int found = 1;
    for (int i = 0; a[i] != '\0' && found; i++) {
        found = 0;
        for (int j = 0; b[j] != '\0' && !found; j++) {
            if (a[i] == b[j]) found = 1;
        }
        if (!found) return 0;
    }

    return 1;
}

/*23
Definir uma função que testa se uma palavra é um palíndrome*/
int palindrome (char s[]) {
    int i = 0, j = mystrlen(s) - 1;
    while (i < j) {
        if (s[i] != s[j]) return 0;
        i++; j--;
    }

    return 1;
}

/*24
Definir uma função que elimina de uma string todos os caracteres que se repetem sucessivamente*/
int remRep (char x[]) {
    if (x[0] == '\0') return 0;
    int j = 1;
    for (int i = 1; x[i] != '\0'; i++) {
        if (x[i] != x[i - 1]) {
            x[j] = x[i];
            j++;
        }
    }
    x[j] = '\0';

    return j;
}

/*25
Definir uma função que elimina repetições sucessivas de espaços por um único espaço*/
int limpaEspaços (char t[]) {
    int j = 0, espaco = 0;

    for (int i = 0; t[i] != '\0'; i++) {
        if (t[i] != ' ') {
            t[j++] = t[i];
            espaco = 0;
        } else if (!espaco) {
            t[j++] = t[i];
            espaco = 1;
        }
    }
    t[j] = '\0';
    return j;
}

/*26
Definir uma função que insere um elemento num array ordenado*/
void insere (int v[], int N, int x) {
    if (N <= 0) return;
    int i;

    for (i = 0; v[i] != '\0' && v[i] < x; i++);
    for (int j = N; j > i; j--) v[j] = v[j - 1];
    v[i] = x;
}

/*27
Definir uma função que, dada dois arrays ordenados, junta os dois arrays num só, ordenadamente*/
void merge (int r[], int a[], int b[], int na, int nb) {
    if (na <= 0 || nb <= 0) return;
    int i = 0, j = 0, k = 0;

    while (i < na && j < nb) {
        if (a[i] < b[j]) {
            r[k++] = a[i++];
        } else {
            r[k++] = b[j++];
        }
    }
    while (i < na) r[k++] = a[i++];
    while (j < nb) r[k++] = b[j++];
}

/*28
Definir uma função que testa se os elementos de um array, entre duas posições, estão ordenados por ordem crescente*/
int crescente (int a[], int i, int j) {
    if (i > j) return 0;

    for (; i < j; i++) {
        if (a[i] > a[i+1]) return 0;
    }

    return 1;
}

/*29
Definir uma função que retira os nºs negativos de um array. Retorna o nº de elementos que não foram retirados*/
int retiraNeg (int v[], int N) {
    if (N <= 0) return 0;
    int j = 0;

    for (int i = 0; i < N; i++) {
        if (v[i] >= 0) {
            v[j] = v[i];
            j++;
        }
    }

    return j;
}

/*30
Definir uma função que recebe um array ordenado por ordem crescente e retorna o menos frequente
Critério de desempate: o que aparece primeiro no índice mais baixo*/
int menosFreq (int v[], int N) {
    if (N <= 0) return -1;
    int cont_min = N + 1, j = v[0], i = 0;
    while (i < N) {
        int k = i;
        while (k < N - 1 && v[k] == v[k+1]) k++;

        int freq = k - i + 1;

        if (freq < cont_min) {
            cont_min = freq;
            j = v[i];
        }
        i = k + 1;
    }

    return j;
}

/*31
Definir uma função que recebe um array ordenado por ordem crescente e retorna o mais frequente
Critério de desempate: o que aparece primeiro no índice mais baixo*/
int maisFreq (int v[], int N) {
    if (N <= 0) return -1;
    int cont_max = 1, j = v[0], i = 0;
    while (i < N) {
        int k = i;
        while (k < N - 1 && v[k] == v[k + 1]) k++;

        int freq = k - i + 1;

        if (freq > cont_max) {
            cont_max = freq;
            j = v[i];
        }
        i = k + 1;
    }
    return j;
}

/*32
Definir uma função que calcula o comprimento da maior sequência crescente de elementos consecutivos num array*/
int maxCresc (int v[], int N) {
    if (N <= 0) return 0;
    int max = 1;
    for (int i = 0; i < N; i++) {
        int k = i;
        while (k < N - 1 && v[k] < v[k+1]) k++;

        int freq = k - i + 1;

        if (freq > max) max = freq;
    }

    return max;
}

/*33
Definir uma função que recebe um array e elimina as repetições. Devolve o nº de elementos do array resultante*/
int elimRep (int v[], int N) {
    if (N <= 0) return 0;
    int novoTam = 0;

    for (int i = 0; i < N; i++) {
        int repetido = 0;
        for (int j = 0; j < novoTam; j++) {
            if (v[i] == v[j]) {
                repetido = 1;
                break;
            }
        }
        if (!repetido) {
            v[novoTam] = v[i];
            novoTam++;
        }
    }
    return novoTam;
}

/*34
Definir uma função que recebe um array ordenado por ordem crescente e elimina as repetições. Devolve o nº de elementos do array resultante*/
int elimRepOrd (int v[], int N) {
    if (N <= 0) return 0;
    int j = 1;
    for (int i = 1; i < N; i++) {
        if (v[i - 1] != v[i]) v[j++] = v[i];
    }
    return j;
}

/*35
Definir uma função que calcula quantos elementos dois arrays têm em comum. Ambos estão ordenados por ordem crescente*/
int comunsOrd (int a[], int na, int b[], int nb) {
    int i = 0, j = 0, c = 0;

    while (i < na && j < nb) {
        if (a[i] == b[j]) {
            c++;
            i++;
            j++;
        } else if (a[i] < b[j]) i++;
        else j++;
    }
    return c;
}

/*36
Definir uma função que calcula quantos elementos dois arrays têm em comum.*/
int comuns (int a[], int na, int b[], int nb) {
    if (na <= 0 || nb <= 0) return 0;
    int c = 0;
    
    for (int i = 0; i < na; i++) {
        for (int j = 0; j < nb; j++) {
            if (a[i] == b[j]) {
                c++;
                break;
            }
        }
    }

    return c;
}

/*37
Definir uma função que dado um array, retorna o índice do menor elemento*/
int minInd (int v[], int N) {
    if (N <= 0) return -1;
    int i = v[0], j = 0;
    for (int k = 1; k < N; k++) {
        if (v[k] < i) {
            i = v[k];
            j = k;
        }
    }

    return j;
}

/*38
Definir uma função que preenche um array com as somas acumuladas de um outro array*/
void somasAc (int v[], int Ac[], int N) {
    if (N <= 0) return;
    int soma = 0;
    for (int i = 0; i < N; i++) Ac[i] = soma += v[i];
}

/*39
Definir uma função que testa se uma matriz quadrada é triangular superior, ie, que todos os elementos abaixo da diagonal são zero*/
int triSup (int N, float m[N][N]) {
    if (N <= 0) return 0;

    for (int i = 0; i < N; i++) {
        for (int j = 0; j < i; j++) {
            if (m[i][j] != 0) return 0;
        }
    }
    return 1;
}

/*40
Definir uma função que transforma uma matriz na sua transposta*/
void transposta (int N, float m[N][N]) {
    if (N <= 0) return;
    float temp;
    for (int i = 0; i < N; i++) {
        for (int j = i + 1; j < N; j++) {
                temp = m[i][j];
                m[i][j] = m[j][i];
                m[j][i] = temp;
        }
    }
}

/*41
Definir uma função que adiciona a segunda matriz à primeira*/
void addTo (int N, int M, int a[N][M], int b[N][M]) {
    if (N <= 0 || M <= 0) return;
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < M; j++) {
            a[i][j] += b[i][j];
        }
    }
}

/*42
Definir uma função que coloca num array o resultado da união de dois conjuntos
Usar um array de inteiros (com 1s ou 0s), consoante um índice pertence a um conjunto*/
int unionSet (int N, int v1[N], int v2[N], int r[N]) {
    if (N <= 0) return -1;
    for (int i = 0; i < N; i++) {
        if (v1[i] == 1 || v2[i] == 1) r[i] = 1;

        else r[i] = 0;
    }
    return 0;
}

/*43
Definir uma função que coloca num array o resultado da interseção de dois conjuntos*/
int intersectSet (int N, int v1[N], int v2[N], int r[N]) {
    if (N <= 0) return -1;
    for (int i = 0; i < N; i++) {
        if (v1[i] == 1 && v2[i] == 1) r[i] = 1;
        
        else r[i] = 0;
    }
    return 0;
}

/*44
Definir uma função que coloca num array o resultado da interseção de dois multi-conjuntos
Usar um array de inteiros contendo em cada posição o nº de ocorrências desse índice no multi-conjunto*/
int intersectMSet (int N, int v1[N], int v2[N], int r[N]) {
    if (N <= 0) return -1;
    for (int i = 0; i < N; i++) {
        r[i] = min(v1,i,v2,i);
    }
    return 0;
}

/*45
Definir uma função que coloca num array o resultado da união de dois multi-conjuntos*/
int unionMSet (int N, int v1[N], int v2[N], int r[N]) {
    if (N <= 0) return -1;
    for (int i = 0; i < N; i++) {
        r[i] = max(v1,i,v2,i);
    }
    return 0;
}

/*46
Definir uma função que calcula o nº de elementos de um multi-conjunto*/
int cardinalMSet (int N, int v[N]) {
    if (N <= 0) return -1;
    int card = 0;
    for (int i = 0; i < N; i++) {
        card += v[i];
    }
    return card;
}

/*47
Definir uma função que, dada uma posição inicial e um array com movimentos, calcula a posição final de um robô*/

typedef enum movimento {Norte, Oeste, Sul, Este} Movimento;
typedef struct posicao {
    int x, y;
} Posicao;

Posicao posFinal (Posicao inicial, Movimento mov[], int N) {
    if (N <= 0) return inicial;

    for (int i = 0; i < N; i++) {
        if (mov[i] == Norte) inicial.y++;
        else if (mov[i] == Oeste) inicial.x--;
        else if (mov[i] == Sul) inicial.y--;
        else if (mov[i] == Este) inicial.x++;
    }
    return inicial;
}

/*48
Definir uma função que dadas as posições inicial e final do robô, preenche um array com os movimentos suficientes para que o trajeto fosse possível*/
int caminho (Posicao inicial, Posicao final, Movimento mov[], int N) {
    int passos = 0;
    while (inicial.x < final.x && passos < N) {
        mov[passos++] = Este;
        inicial.x++;
    }
    while (inicial.x > final.x && passos < N) {
        mov[passos++] = Oeste;
        inicial.x--;
    }
    while (inicial.y < final.y && passos < N) {
        mov[passos++] = Norte;
        inicial.y++;
    }
    while (inicial.y > final.y && passos < N) {
        mov[passos++] = Sul;
        inicial.y--;
    }
    if (inicial.x != final.x || inicial.y != final.y) return -1;

    return passos;
}

/*49
Definir uma função que dado um array com posições, determina o índice da que está mais perto da origem*/
int maisCentral (Posicao pos[], int N) {
    if (N <= 0) return -1;

    int min = 0;
    float dist_min = dist(pos[0].x,pos[0].y,0,0);
    for (int i = 1; i < N; i++) {
        float dis = dist(pos[i].x,pos[i].y,0,0);
        if (dis < dist_min) {
            dist_min = dis;
            min = i;
        }
    }
    return min;
}

/*50
Definir uma função que dada uma posição e um array com posições, calcula quantas dessas posições são adjacentes à posição dada*/
int vizinhos (Posicao p, Posicao pos[], int N) {
    int c = 0;
    for (int i = 0; i < N; i++) {
        if ((pos[i].x == p.x + 1 && pos[i].y == p.y) ||
            (pos[i].x == p.x && pos[i].y == p.y + 1) || 
            (pos[i].x == p.x - 1 && pos[i].y == p.y) || 
            (pos[i].x == p.x && pos[i].y == p.y - 1)) c++;
    }

    return c;
}

int main() {
   return 0;
}