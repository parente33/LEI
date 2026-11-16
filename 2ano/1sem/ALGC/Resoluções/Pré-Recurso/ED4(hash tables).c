#include <stdlib.h>

/*
Funções de hashing => Não-injetivas por natureza

Quando N é um nº primo, a seguinte função funciona de forma razoavelmente uniforme:

int hash (int k, int N) {
    return k % N;
}

CONDICIONAMENTO DE CHAVES

Se as chaves não forem números naturais, devem ser previamente condicionadas, ie, mapeadas em números naturais
Por exemplo, tratando-se de chaves de tipo string de caracteres:

int condition (char *s) {
    int r = 0;
    while (*s) {
        r += *s++;
    }
    return r;
}

Uma tabela de hash tanto pode consistir, dependendo da estratégia de resolução de colisões adotada:
    - Open Addressing => um array com posições {0 ... N-1} de pares (key,value);
    - Closed Addressing => um array com posições {0 ... N-1} de (apontadores para) listas ligadas de pares (k,v)

- Se N = #(K), não há colisões e as operações de inserção, pesquisa, e remoção executam em tempo constante
- Ao diminuir substancialmente o tamanho N do array, melhora-se a gestão do espaço, às custas de piorar a performance temporal das operações
- O princípio por que se rege o desenho das tabelas é que o tempo deverá sempre ser *tendecialmente constante*

FATOR DE CARGA: @ = #chaves inseridas / N
Quando @ chega a um determinado valor (geralmente 0.7 ou 0.8), redimensiona-se a tabela (implementada por um array dinâmico)
As operações de redimensionamento (duplicação do tamanho da tabela) executam em *tempo amortizado constante*


CLOSED ADDRESSING - IMPLEMENTAÇÃO

typedef struct node {
    char key[MAXSTR];
    ValueType info;
    struct node *next;
};

typedef struct node *Hashtable[N];


OPEN ADDRESSING - IMPLEMENTAÇÃO C/ LINEAR PROBING
A colisão resolve-se inserindo na posição livre seguinte (com circularidade) do vetor


REMOÇÃO DE CHAVES
Efetua-se as operações *insert(k1, v1)*, *insert(k2, v2)*, e *remove(k1)*, por esta ordem, e h(k1) = h(k2) = p

    ______| empty, -        ______| empty, -
    ______| empty, -        ______| empty, -
    ______| empty, -        ______| empty, -
    _____p| k1, v1      =>  _____p| empty, -
    _p + 1| k2, v2          _p + 1| k2, v2s
    ______| empty, -        ______| empty, -
    ______| empty, -        ______| empty, -

Se agora fôssemos efetuar uma consulta com a chave k2, ia dar problemas
Não é portanto adequado marcar as posições onde ocorreram remoções como *empty*. Utiliza-se a chave alternativa *removed*, que indica que uma pesquisa deve continuar 
para além daquela posição
A utilização desta chave contribui para a degradação da performance da operação de consulta. 
Ao fim de algum tempo, já não haverá chaves *empty*, o que significa que a operação passará a executar em tempo linear
Será necessário usar um "garbage collection" periodicamente
*/

#define EMPTY       "-"
#define DELETED     "+"


struct pair {
    char key[20];
    int value;
};
    
typedef struct {
    int   size;
    int   used;
    struct pair *tbl;
} HT;

int hash (char key[], int size) {
    return hash_sol(key,size);
}

int hash_sol (char key[], int size) {
    int i = 0, sum = 0;
    while (key[i] != '\0') sum += key[i++];

    return (sum % size);
}

void initHT (HT *h, int size) {
    initHT_sol(h,size);
}

void initHT_sol (HT *h, int size) {
    int i;
    h->tbl = calloc(size, sizeof(struct pair));
    h->size = size;
    h->used = 0;

    for (i = 0; i < size; i++) {
        strcpy(h->tbl[i].key, EMPTY);
    }
}

int freeHT (HT *h, int k) {
    return freeHT_sol(h,k);
}

int freeHT_sol (HT *h, int k) {
    if (strcmp((h->tbl)[k].key, EMPTY) == 0) return 1;
    if (strcmp((h->tbl)[k].key, EMPTY) == 0) return 1;
    return 0;
}

int writeHT (HT *h, char key[], int value) {
    return writeHT_sol(h,key,value);
}

int writeHT_sol (HT *h, char key[], int value) {
    float load = h->used / h->size;
    // if (load > 0.8) grow(h, 2*(h->size))

    int i = hash(key, h->size);
    while (!freeHT(h,i)) {
        i = (i+1)%(h->size);
        printf("* ");
    }

    strcpy((h->tbl)[i].key, key);
    (h->tbl)[i].value = value;
    h->used++;

    return i;
}

int readHT (HT *h, char key[], int *value) {
    return readHT_sol(h,key,value);
}

int readHT_sol (HT *h, char key[], int *value) {
    int i, j;
    i = j = hash(key, h->size);

    while (strcmp((h->tbl)[i].key, value) != 0) {
        if (strcmp((h->tbl)[i].key, EMPTY) == 0) return -1;
        i = (i+1)%(h->size);
        printf("* ");
        if (i == j) return -1;
    }
    *value = (h->tbl)[i].value;
    return i;
}

int deleteHT (HT *h, char key[]) {
    return deleteHT_sol(h,key);
}

int deleteHT_sol (HT *h, char key[]) {
    int r, v;
    r = readHT(h, key, &v);
    if (r >= 0) {
        strcpy((h->tbl)[r].key, DELETED);
        h->used--;
    }
    return r;
}