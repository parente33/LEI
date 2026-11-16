#include <stdio.h>
#include <stdlib.h>

int fizz(int n) {}

int buzz(int n) {}

int fizzbuzz(int n) {
    for (int i = 0; i < n; i++) {
        if (fizz(i) && buzz(i)) printf("FizzBuzz\n");
        else if (fizz(i)) printf("Fizz\n");
        else if (buzz(i)) printf("Buzz\n");
        else printf("%d\n", i);
    }
    return 0;
}

/* Ex 1*/
int fizzbuzzAlt(int n) {
    int fizzbuzz = 0;
    int c = 0;

    for (int i = 0; i < n; i++) {
        if (fizz(i) && buzz(i)) {
            printf("FizzBuzz\n");
            if (!fizzbuzz) {
                fizzbuzz = 1;
                c = 0;
            } else if (fizzbuzz) c++;
        } 
        else if (fizz(i)) {
            printf("Fizz\n");
            if (fizzbuzz) c++;
        } 
        else if (buzz(i)) {
            printf("Buzz\n");
            if (fizzbuzz) return c;
        } 
        else {
            printf("%d\n", i);
            if (fizzbuzz) c++;
        }
    }
    return -1;
}

/* Ex 2 */
void inverte(int a[], int l, int r) {
    while (l < r) {
        int temp = a[l];
        a[l] = a[r];
        a[r] = temp;
        l++; r--;
    }
}

void rodaEsq(int a[], int N, int r) {
    if (N <= 0 || r <= 0 || r >= N) return;

    inverte(a,0,r-1);
    inverte(a,r,N-1);
    inverte(a,0,N-1);
}

typedef struct lint_nodo {
    int valor;
    struct lint_nodo *prox;
} *LInt;

/* Ex 3 */
int delete(int n, LInt *l) {
    if (*l == NULL || n < 0) return -1;
    if (n == 0) {
        LInt temp = *l;
        *l = (*l)->prox;
        free(temp);
        return 0;
    }

    LInt temp = *l;
    for (int i = 0; i < n - 1; i++) {
        if (temp->prox == NULL) return -1;
        temp = temp->prox;
    }

    if (temp->prox == NULL) return -1;

    LInt apagado = temp->prox;
    temp->prox = apagado->prox;
    free(apagado);

    return 0;
}

/* Ex 4 */
int maxCresc(LInt l) {
    if (l == NULL) return 0;
    int max_cresc = 1;
    int atual = 1;

    while (l->prox != NULL) {
        if (l->prox->valor > l->valor) {
            atual++;
            if (atual > max_cresc) {
                max_cresc = atual;
            }
        } else {
            atual = 1;
        }
        l = l->prox;
    }
    return max_cresc;
}

/* Ex 5 */
typedef struct abin_nodo {
    int valor;
    struct abin_nodo *esq, *dir;
} *ABin;

ABin folhaEsq(ABin a) {
    if (a == NULL) return NULL;
    if (a->esq == NULL && a->dir == NULL) return a;

    ABin folha = folhaEsq(a->esq);
    if (folha != NULL) return folha;

    return folhaEsq(a->dir);
}

/* Ex 6 */
int parentesisOk(char exp[]) {
    char stack[1000];
    int top = -1;

    for (int i = 0; exp[i]; i++) {
        char c = exp[i];
        if (c == '(' || c == '[' || c == '{') stack[++top] = c;
        else if (c == ')' || c == ']' || c == '}') {
            if (top == -1) return 0;

            char open = stack[top--];

            if ((c == ')' && open != '(') ||
                (c == ']' && open != '[') ||
                (c == '}' && open != '}')) return 0;
        }
    }
    return top == -1;
}