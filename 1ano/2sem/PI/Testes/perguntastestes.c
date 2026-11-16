#include <stdlib.h>

int nesimo(int a[], int N, int i) {
    for (int k = 0; k < N - 1; k++) {
        for (int j = 0; j < N - k - 1; j++) {
            if (a[j] > a[j+1]) swap(a,k,j);
        }
    }
    return a[i-1];
}

typedef struct LInt_nodo {
    int valor;
    struct LInt_nodo *prox;
} *LInt;

LInt removeMaiores(LInt l, int x) {
    LInt atual = l;

    while (atual != NULL && atual->prox != NULL) {
        if (atual->prox->valor > x) {
            LInt temp = atual->prox;
            atual->prox = NULL;

            while (temp != NULL) {
                LInt next = temp->prox;
                free(temp);
                temp = next;
            }
            break;
        }
        atual = atual->prox;
    }

    if (l != NULL && l->valor > x) {
        while (l != NULL) {
            LInt temp = l;
            l = l->prox;
            free(temp);
        }
        return NULL;
    }
    return l;
}

typedef struct ABin_nodo {
    int valor;
    struct ABin_nodo *esq, *dir;
} *ABin;

LInt caminho(ABin a, int x) {
    if (a == NULL) return NULL;

    if (a->valor == x) {
        LInt novo = malloc(sizeof(struct LInt_nodo));
        novo->valor = x;
        novo->prox = NULL;
        return novo;
    }
    LInt resto;
    if (x < a->valor)
        resto = caminho(a->esq,x);
    else
        resto = caminho(a->dir,x);

    if (resto == NULL) return NULL;

    LInt novo = malloc(sizeof(struct LInt_nodo));
    novo->valor = a->valor;
    novo->prox = resto;

    return novo;
}

void inc(char s[]) {
    int i = strlen(s) - 1;

    while (i >= 0 && s[i] == '9') {
        s[i] = '0';
        i--;
    }

    if (i >= 0) {
        s[i]++;
    } else {
        int len = strlen(s);
        for (int j = len; j >= 0; j--) {
            s[j+1] = s[j];
        }
        s[0] = '1';
    }
}

int pesquisa(int a[], int N, int x) {
    int inicio = 0, fim = N - 1;
    while (inicio <= fim) {
        int meio = inicio + (fim-inicio) / 2;
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

void roda(LInt *l) {
    if (*l == NULL || (*l)->prox == NULL) return;
    
    LInt prev = NULL;
    LInt curr = *l;

    while (curr->prox != NULL) {
        prev = curr;
        curr = curr->prox;
    }

    prev->prox = NULL;
    curr->prox = *l;
    *l = curr;
}

int apagaAux(ABin a, int *n) {
    if (a == NULL || *n == 0) return 0;
    int apagados = 0;

    apagados += apagaAux(a->esq,n);
    apagados += apagaAux(a->dir,n);

    if (*n > 0) {
        free(a);
        (*n)--;
        apagados++;
    }
    return apagados;
}

int apaga(ABin a, int n) {
    return apagaAux(a,&n);
}

int perfeito(int x) {
    int c = 0;
    for (int i = 1; i < x; i++) {
        if (x % i == 0) c += i;
    }
    return c == x ? 1 : 0;
}

typedef struct {
    int x,y;
} Ponto;

int comparaPontos(Ponto p1, Ponto p2) {
    int d1 = p1.x*p1.x + p1.y*p1.y;
    int d2 = p2.x*p2.x + p2.y*p2.y;

    if (d1 == d2) return 0;
    else if (d1 > d2) return 1;
    else return -1;
}

void swap(Ponto *a, Ponto *b) {
    Ponto temp = *a;
    *a = *b;
    *b = temp;
}

void ordena(Ponto pos[], int N) {
    for (int i = 0; i < N - 1; i++) {
        for (int j = 0; j < N - i - 1; j++) {
            if (comparaPontos(pos[j],pos[j+1]) > 0) {
                swap(&pos[j],&pos[j+1]);
            }
        }
    }
}

int depth(ABin a, int x) {
    if (a == NULL) return -1;

    if (a->valor == x) return 0;

    int esq = depth(a->esq,x);
    int dir = depth(a->dir,x);

    if (esq == -1 && dir == -1) return -1;

    if (esq == -1) return dir + 1;
    if (dir == -1) return esq + 1;

    return 1 + (esq < dir ? esq : dir);
}

LInt periodica(char s[]) {
    int i = 0, init = -1, fim = -1;

    while (s[i]) {
        if (s[i] == '(') init = i;
        else if (s[i] == ')') fim = i;
        i++;
    }
    if (init == -1 || fim == -1 || fim <= init + 1) return NULL;

    LInt head = NULL, tail = NULL, ciclo = NULL;

    for (int j = 0; s[j]; j++) {
        if (s[j] >= '0' && s[j] <= '9') {
            LInt novo = malloc(sizeof(struct LInt_nodo));
            novo->valor = s[j] - '0';
            novo->prox = NULL;

            if (head == NULL)
                head = tail = novo;
            else {
                tail->prox = novo;
                tail = novo;
            }

            if (j == init + 1)
                ciclo = novo;
        }
    }

    if (tail != NULL && ciclo != NULL)
        tail->prox = ciclo;

    return head;
}

int isFib(int x) {
    if (x < 0) return -1;
    if (x == 0 || x == 1) return 1;

    int i = 0, j = 1;
    while (j < x) {
        int temp = j;
        j = i + j;
        i = temp;
    }

    return j == x;
}

typedef struct {
    float teste, minis;
} Aluno;

float calculaNotaFinal(Aluno a) {
    return 0.8 * a.teste + 0.2 * a.minis;
}

int moda(Aluno turma[], int N) {
    if (N <= 0) return -1;
    int n[21] = {0};

    for (int i = 0; i < N; i++) {
        float nota = calculaNotaFinal(turma[i]);
        int arred = (int)(nota + 0.5);
        if (arred < 0) arred = 0;
        if (arred > 20) arred = 20;
        n[arred]++;
    }

    int notaMaisFreq = 0, c = 1;
    for (int i = 0; i < 21; i++) {
        if (n[i] > notaMaisFreq) {
            notaMaisFreq = n[i];
            c = i;
        }
    }

    return c > 10 ? c : 0;
}

int take(int n, LInt *l) {
    LInt atual = *l;
    int i = 0;

    while (atual != NULL && i < n - 1) {
        atual = atual->prox;
        i++;
    }
    if (atual == NULL) return 0;

    LInt remover = atual->prox;
    atual->prox = NULL;

    int c = 0;
    while (remover != NULL) {
        LInt temp = remover;
        remover = remover->prox;
        free(temp);
        c++;
    }
    return c;
}

int verifica(char frase[], int k) {
    int i = 0;

    while (frase[i]) {
        while (frase[i] == ' ') i++;

        int len = 0;
        while (frase[i] != '\0' && frase[i] != ' ') {
            len++;
            i++;
        }
        if (len > 0 && len < k) return 0;
    }
    return 1;
}