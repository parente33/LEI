#include <stdio.h>
#include <math.h>
#include <ctype.h>
#include <string.h>
#include <stdlib.h>

typedef struct lligada {
    int valor;
    struct lligada *prox;
} *LInt;

/* 1
Calcular o comprimento de uma lista ligada, não recursivamente*/
int length (LInt l) {
    int c = 0;

    while (l != NULL) {
        c++;
        l = l->prox;
    }

    return c;
}

/* 2
Libertar o espaço de uma lista, não-recursivamente*/
void freeL (LInt l) {
    LInt temp;
    while (l != NULL) {
        temp = l;
        l = l->prox;
        free(temp);
    }
}

/* 3
Imprimir os elementos de uma lista (um por linha)*/
void imprimeL (LInt l) {
    while (l != NULL) {
        printf("%d\n", l->valor);
        l = l->prox;
    }
}

/* 4
Inverter uma lista, sem criar outra*/
LInt reverseL (LInt l) {
    
}

/* 5
Insere ordenadamente um elemento numa lista ordenada*/
void insertOrd (LInt *l, int x) {
    while (*l != NULL && (*l)->valor < x) {
        l = &((*l)->prox);
    }
    LInt novo = malloc(sizeof(struct lligada));
    novo->valor = x;

    novo->prox = *l;
    *l = novo;
}

/* 6
Remover um elemento de uma lista ordenada. Retorna 1 se o elemento não existir*/
int removeOneOrd (LInt *l, int x) {

}

/* 7
Juntar duas listas ordenadas numa única lista*/
void merge (LInt *r, LInt a, LInt b) {
    while (a != NULL && b != NULL) {
        if (a->valor < b->valor) {
            *r = a;
            a = a->prox;
        } else {
            *r = b;
            b = b->prox;
        }
        r = &((*r)->prox);
    }
    
    if (a != NULL) *r = a;
    else *r = b;
}

/* 8
Parte uma lista em duas, uma com os elementos menores que x, e a outra com os restantes*/
void splitQS (LInt l, int x, LInt *mx, LInt *Mx) {

}

/* 9
Parte uma lista a meio*/
LInt parteAmeio (LInt *l) {
    int c = 0;
    LInt temp = *l;

    while (temp != NULL) {
        c++;
        temp = temp->prox;
    }

    int meio = c / 2;
    if (meio == 0) return NULL;

    LInt res = *l;
    LInt prev = NULL;

    for (int i = 0; i < meio; i++) {
        prev = *l;
        l = &((*l)->prox);
    }

    if (prev != NULL) prev->prox = NULL;

    return res;
}

/* 10
Remove todas as ocorrências de um número numa lista*/
int removeAll (LInt *l, int x) {
    if (*l == NULL) return 0;
    int c = 0;

    while (*l != NULL) {
        if ((*l)->valor == x) {
            LInt temp = *l;
            *l = (*l)->prox;
            free(temp);
            c++;
        } else {
            *l = &((*l)->prox);
        }
    }

    return c;
}

/* 11
Remove os valores repetidos de uma lista (deixando a primeira ocorrência de cada)*/
int removeDups (LInt *l) {

}

/* 12
Remove a primeira ocorrência do maior elemento da lista*/
int removeMaiorL (LInt *l) {

}

/* 13
Remove o último elemento de uma lista*/
void init (LInt *l) {
    while ((*l)->prox != NULL && (*l)->prox->prox != NULL) {
        l = &((*l)->prox);
    }
    free((*l)->prox);
    (*l)->prox = NULL;
}

/* 14
Acrescenta um elemento ao fim da lista*/
void appendL (LInt *l, int x) {
    while (*l != NULL) {
        l = &((*l)->prox);
    }

    *l = malloc(sizeof(struct lligada));
    (*l)->valor = x;
    (*l)->prox = NULL;
}

/* 15
Acrescenta uma lista a outra*/
void concatL (LInt *a, LInt b) {

}

/* 16
Cria uma nova lista com os elementos na ordem que aparecem na lista argumento*/
LInt cloneL (LInt l) {
    if (l == NULL) return NULL;
    LInt nova = malloc(sizeof(struct lligada));
    nova->valor = l->valor;
    nova->prox = NULL;
    
    LInt atual = nova;
    l = l->prox;

    while (l != NULL) {
        LInt novo = malloc(sizeof(struct lligada));
        nova->valor = l->valor;
        nova->prox = NULL;
        
        atual->prox = novo;
        atual = novo;

        l = l->prox;
    }

    return nova;
}

/* 17
Cria uma nova lista com os elementos em ordem inversa*/
LInt cloneRev (LInt l) {

}

/* 18
Calcula o maior valor armazenado numa lista*/
int maximo (LInt l) {
    int max = l->valor;

    while (l != NULL) {
        if (l->valor > max) max = l->valor;
        l = l->prox;
    }

    return max;
}

/* 19
Dado um n, apaga da lista todos os nodos para além do n-ésimo*/
int take (int n, LInt *l) {
    int len = 0;
    LInt temp = *l;

    while (temp != NULL) {
        len++;
        temp = temp->prox;
    }

    if (len > n) {
        for (int i = 1; i < n; i++) {
            l = &((*l)->prox);
        }

        while (*l != NULL) {
            LInt temp = *l;
            *l = (*l)->prox;
            free(temp);
        }
        return n;
    } else {
        return len;
    }
}

/* 20
Dado um n, apaga da lista os primeiros n nodos*/
int drop (int n, LInt *l) {
    int c = 0;
    
    while (*l != NULL && c < n) {
        LInt temp = *l;
        *l = (*l)->prox;
        free(temp);
        c++;
    }

    return c;
}

/* 21
Dada uma lista circular, retorna o endereço do elemento que está n posições à frente*/
LInt Nforward (LInt l, int N) {
    if (l == NULL) return NULL;

    int a = 1;
    while (a < N) {
        l = l->prox;
        a++;
    }

    return l;
}

/* 22
Dada uma lista, preenche um array com os elementos da lista*/
int listToArray (LInt l, int v[], int N) {
    int i = 0;

    while (l != NULL && i < N) {
        v[i] = l->valor;
        i++;
        l = l->prox;
    }

    return i;
}

/* 23
Constrói uma lista com os elementos de um array (pela mesma ordem)*/
LInt arrayToList (int v[], int N) {
    if (N <= 0) return NULL;

    LInt l = malloc(sizeof(struct lligada));
    l->valor = v[0];
    l->prox = NULL;

    LInt temp = l;

    for (int i = 1; i < N; i++) {
        LInt novo = malloc(sizeof(struct lligada));
        novo->valor = v[i];
        novo->prox = NULL;

        temp->prox = novo;
        temp = novo;
    }

    return l;
}

/* 24
Constrói uma lista contendo as somas acumuladas da lista original*/
LInt somasAcL (LInt l) {

}

/* 25
Dada uma lista ordenada, elimina todos os valores repetidos*/
void remreps (LInt l) {

}

/* 26
Coloca o primeiro elemento da lista no fim*/
LInt rotateL (LInt l) {
    if (l == NULL || l->prox == NULL) return l;

    LInt primeiro = l;
    LInt atual = l->prox;

    LInt *ptr = &atual;
    while ((*ptr)->prox != NULL) {
        ptr = &((*ptr)->prox);
    }

    (*ptr)->prox = primeiro;
    primeiro->prox = NULL;

    return atual;
}

/* 27
Parte uma lista em duas. Na primeira ficam os elementos das posiões ímpares, na outra ficam os restantes*/
LInt parte (LInt l) {

}

//---------------------------------------//

typedef struct nodo {
    int valor;
    struct nodo *esq, *dir;
} *ABin;

/* 28
Calcula a altura de uma árvore*/
int altura (ABin a) {
    if (a == NULL) return 0;

    int alturaEsq = altura(a->esq);
    int alturaDir = altura(a->dir);

    return 1 + (alturaEsq > alturaDir ? alturaEsq : alturaDir);
}

/* 29
Cria uma cópia da árvore*/
ABin cloneAB (ABin a) {
    if (a == NULL) return NULL;

    ABin b = malloc(sizeof(struct nodo));
    b->valor = a->valor;
    b->esq = cloneAB(a->esq);
    b->dir = cloneAB(a->dir);

    return b;
}

/* 30
Inverte uma árvore, sem criar uma nova*/
void mirror (ABin *a) {

}

/* 31
Cria uma lista ligada a partir da travessia inorder de uma árvore*/
void inorder (ABin a, LInt *l) {
    if (a == NULL) return;

    inorder(a->esq, *l);
    
    LInt novo = malloc(sizeof(struct lligada));
    novo->valor = a->valor;
    novo->prox = NULL;
    *l = novo;
    l = &(novo->prox);

    inorder(a->dir, *l);
}

/* 32
Cria uma lista ligada a partir da travessia preorder de uma árvore*/
void preorder (ABin a, LInt *l) {

}

/* 33
Cria uma lista ligada a partir da travessia posorder de uma árore*/
void posorder (ABin a, LInt *l) {

}

/* 34
Calcula o nível a que um elemento está numa árvore*/
int depth (ABin a, int x) {
    if (a == NULL) return -1;
    if (a->valor == x) return 0;

    int existeEsq = depth(a->esq,x);
    if (existeEsq != -1) return 1 + existeEsq;

    int existeDir = depth(a->dir,x);
    if (existeDir != -1) return 1 + existeDir;

    return -1;
}

/* 35
Liberta todo o espaço ocupado por uma árvore*/
int freeAB (ABin a) {
    if (a == NULL) return 0;

    int esq = freeAB(a->esq);
    int dir = freeAB(a->dir);

    free(a);

    return 1 + esq + dir;
}

/* 36
Remove todos os elementos que estão a uma profundidade superior a l*/
int pruneAB (ABin *a, int l) {

}

/* 37
Testa se duas árvores são iguais*/
int iguaisAB (ABin a, ABin b) {
    if (a == NULL && b == NULL) return 1;
    if (a == NULL || b == NULL) return 0;
    if (a->valor != b->valor) return 0;

    return iguaisAB(a->esq, b->esq) && iguaisAB(a->dir, b->dir);
}

/* 38
Constrói uma lista com os elementos da árvore que estão no nível n*/
LInt nivelL (ABin a, int n) {

}

/* 39
Preenche um array com os elementos da árvore que estão no nível n*/
int nivelV (ABin a, int n, int v[]) {

}

/* 40
Preenche um array com os elementos da árvore numa travessia inorder*/
int dumpAbin (ABin a, int v[], int N) {

}

/* 41
Dada uma árvore calcula a árvore das somas acumuladas dessa árvore*/
ABin somasAcA (ABin a) {

}

/* 42
Conta quantos nodos da árvore são folhas (não têm descendentes)*/
int contaFolhas (ABin a) {
    if (a == NULL) return 0;
    if (a->esq == NULL && a->dir == NULL) return 1;

    return contaFolhas(a->esq) + contaFolhas(a->dir);
}

/* 43
Cria uma árvore nova, invertendo a árvore original*/
ABin cloneMirror (ABin a) {

}

/* 44
Adiciona um elemento a uma árvore binária de procura*/
int addOrd (ABin *a, int x) {
    while (*a != NULL) {
        if ((*a)->valor == x) return 1;
        else if (x < (*a)->valor) a = &((*a)->esq);
        else a = &((*a)->dir);
    }

    *a = malloc(sizeof(struct nodo));
    (*a)->valor = x;
    (*a)->esq = NULL;
    (*a)->dir = NULL;

    return 0;
}

/* 45
Testa se um elemento pertence a uma árvore binária de procura*/
int lookupAB (ABin a, int x) {
    while (a != NULL) {
        if (a->valor == x) return 1;
        else if (a->valor > x) a = a->esq;
        else a = a->dir;
    }

    return 0;
}

/* 46
Calcula o nível de um elemento numa árvore binária de procura*/
int depthOrd (ABin a, int x) {

}

/* 47
Calcula o maior elemento de uma árvore binária de procura*/
int maiorAB (ABin a) {
    while (a->dir != NULL) {
        a = a->dir;
    }

    return a->valor;
}

/* 48
Remove o maior elemento de uma árvore binária de procura*/
void removeMaiorA (ABin *a) {
    while ((*a)->dir != NULL) {
        a = &((*a)->dir);
    }

    ABin temp = *a;
    *a = (*a)->esq;
    free(temp);
}

/* 49
Conta quantos elementos numa árv binária de procura são maiores do que o elemento dado*/
int quantosMaiores (ABin a, int x) {

}

/* 50
Constrói uma árv bin de procura a partir de uma lista ligada ordenada*/
void listToBTree (LInt l, ABin *a) {

}

/* 51
Testa se uma árvore é de procura*/
int deProcura (ABin a) {
    
}