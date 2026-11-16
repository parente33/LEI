#include <stdio.h>
#include <stdlib.h>

/* Ex 1 */
int pesquisa(int x, int a[], int N) {
    if (N <= 0) return -1;
    int inicio = 0, fim = N - 1;

    while (inicio <= fim) {
        int meio = inicio + (fim - inicio) / 2;
        if (a[meio] == x) {
            return meio;
        } else if (a[meio] > x) {
            fim = meio - 1;
        } else {
            inicio = meio + 1;
        }
    }
    return -1;
}

/* Ex 2 */
typedef struct lint_nodo {
    int valor;
    struct lint_nodo *prox;
} *LInt;

void duplicaTodos(LInt l) {
    if (l == NULL) return;

    while (l != NULL) {
        LInt novo = malloc(sizeof(struct lint_nodo));
        novo->valor = l->valor;
        novo->prox = l->prox;

        l->prox = novo;
        l = novo->prox;
    }
}

/* Ex 3 */
int expande(char s[]) {
    int len_original = strlen(s);
    int tam = 0;

    for (int i = 0; i < len_original; i += 2) {
        if (isdigit(s[i+1])) tam += s[i+1] - '0';
    }

    int pos = tam - 1;

    for (int i = len_original - 2; i >= 0; i -= 2) {
        char c = s[i];
        int rep = s[i+1] - '0';

        for (int j = 0; j < rep; j++) s[pos--] = c;
    }
    s[tam] = '\0';
    return tam;
}

/* Ex 4 */
int fizz(int n) {}
int buzz(int n) {}

int fizzbuzz(int n) {
    for (int i = 0; i < n; i++) {
        if (fizz(i) && buzz(i))
            printf("FizzBuzz\n");
        else if (fizz(i))
            printf("Fizz\n");
        else if (buzz(i))
            printf("Buzz\n");
        else
            printf("%d\n", i);
    }
    return 0;
}

int fizzbuzzAlt(int n) {
    int primfizzbuzz = -1;
    int ultbuzz = -1;

    for (int i = 0; i < n; i++) {
        if (fizz(i) && buzz(i)) {
            printf("FizzBuzz\n");
            if (primfizzbuzz == -1)
                primfizzbuzz = i;
        }
        else if (fizz(i))
            printf("Fizz\n");
        else if (buzz(i)) {
            printf("Buzz\n");
            if (primfizzbuzz != -1)
                ultbuzz = i;
        }
        else printf("%d\n", i);
    }

    if (primfizzbuzz != -1 && ultbuzz != -1 && ultbuzz > primfizzbuzz)
        return ultbuzz - primfizzbuzz - 1;
    
    return -1;
}

typedef struct abin_nodo {
    int valor;
    struct abin_nodo *esq, *dir;
} *ABin;

int todosIguaisAux(ABin a, int nivelAtual, int n, int *valor, int *found) {
    if (a == NULL) return 1;

    if (nivelAtual == n) {
        if (!*found) {
            *valor = a->valor;
            *found = 1;
        }
        else if (a->valor != *valor)
            return 0;
        return 1;
    }
    return todosIguaisAux(a->esq,nivelAtual + 1,n,valor,found) &&
           todosIguaisAux(a->dir,nivelAtual + 1,n,valor,found);
}

int todosIguais(ABin a, int n) {
    int valor = 0;
    int found = 0;
    return todosIguaisAux(a,0,n,&valor,&found);
}

/* Ex 6 */
int calcula(char s[]) {
    int stack[100];
    int sp = 0;

    for (int i = 0; s[i]; i++) {
        if (isdigit(s[i]))
            stack[sp++] = s[i] - '0';
        else if (s[i] == '+' || s[i] == '*') {
            if (sp < 2) return -1;

            int b = stack[--sp];
            int a = stack[--sp];

            if (s[i] == '+')
                stack[sp++] = a + b;
            else if (s[i] == '*')
                stack[sp++] = a * b;
        }
    }
    return (sp == 1) ? stack[0] : -1;
}