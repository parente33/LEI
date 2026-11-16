#include <stdlib.h>

/*
Min-Heap => Árvore binária em que cada nodo é menor ou igual a todos os seus sucessores

Árvore semi-completa: Todos os níveis da árvore estão completos, com a possível exceção do último, que pode estar parcialmente preenchido (da esq para a dir)
Têm uma representação económica em array: os nodos são armazenados por nível, sempre da esq para a dir

Invariante de forma: árvore semi-completa
Invariante de ordem: filhos de um nó são maiores do que o nó
*/

/*
                        10
              15                 11
        16          22      35         20
    21     23     34  37  80  43     22  25
  24  28

(0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16) N = 17

 10 15 11 16 22 35 20 21 23 34 37 80 43 22 25 24 28
*/

/*
Ex 1: Expressão que permite calcular:

    a) O índice onde se encontra a sub-árvore esquerda do nodo da posição i
        R: (i=0) => 1; (i=1) => 3; (i=2) => 5
            2*i + 1

    b) O índice onde se encontra a sub-árvore direita do nodo da posição i
        R: (i=0) => 2; (i=1) => 4; (i=2) => 6
            2*i + 2

    c) O índice onde se encontra o pai do nodo da posição i
        R: (i=1,i=2) => 0; (i=3,i=4) => 1; (i=5,i=6) => 2; (i=7,i=8) => 3
            (i-1)/2

    d) O índice onde se encontra a primeira folha, ie, o primeiro nodo que não tem sucessores
        R: N/2
*/

#define PAI(x) ((x-1)/2)
#define ESQ(x) (2*x+1)
#define DIR(x) (2*x+2)

// Ex 2
// Por sucessivas trocas com o pai, puxa o elemento na posição i da min-heap h até que satisfaça a propriedade das min-heaps
void bubbleUp (int i, int h[]) {
    while (PAI(i) > ESQ(i)) {
        swap(PAI(i), ESQ(i), h);
        i = PAI(i);
    }
    while (PAI(i) > DIR(i)) {
        swap(PAI(i), DIR(i), h);
        i = PAI(i);
    }
}
// Pior caso da função: O elemento está na "última" posição, e tem que ir até ao topo
// Complexidade: O(log n)

void swap (int a, int b, int v[]) {
    int temp = v[a];
    v[a] = v[b];
    v[b] = temp;
}

// Ex 3
// Por sucessivas trocas com um dos filhos, empurra o elemento na posição i da min-heap h até que satisfaça a propriedade das min-heaps
void bubbleDown (int i, int h[], int N) {
    int p = PAI(i);
    int e = ESQ(i);
    int d = DIR(i);

    if (e > N && d > N) return; // Não tem filhos

    if (h[p] <= h[e] && h[p] <= h[d]) return; // Está no sítio certo

    while ((e <= N && d <= N) && (h[p] > h[e] || h[p] > h[d])) {
        if (h[e] > h[d]) {
            swap(p,d,h);
        } else {
            swap(p,e,h);
        }
    }
}

#define Max 100
// Implementação de uma fila de prioridades: o próximo elemento a retirar da lista é o menor que lá estiver
typedef struct pQueue {
    int valores [Max];
    int tamanho;
} PriorityQueue;

// Ex 4
// Definições habituais sobre o tipo de dados

// Inicializa a queue como vazia
void empty (PriorityQueue *q) {
    q->tamanho = 0;
}

// Testa se a queue está vazia
int isEmpty (PriorityQueue *q) {
    return q->tamanho == 0;
}

// Adiciona um elemento à fila (retornando 0 se a operação for possível)
int add (int x, PriorityQueue *q) {
    if (q->tamanho == Max) return -1;

    q->valores[q->tamanho] = x;
    bubbleUp(q, q->tamanho);
    q->tamanho++;

    return 0;
}

// Remove o próximo elemento, devolvendo-o em *rem, e retornando 0 se a operação for possível
int remove (PriorityQueue *q, int *rem) {
    if (isEmpty) return -1;

    *rem = q->valores[0];
    q->tamanho--;
    q->valores[0] = q->valores[q->tamanho];
    bubbleDown(0, q->valores, q->tamanho);

    return 0;
}

// Ex 5
// Obter uma permutação de um array que seja uma min-heap (heapify)

// Estratégia 1: top-down => Assumindo que as primeiras p posições do array constituem uma min-heap (de tamanho p), efetuar a invocação bubbleUp (p,v,N) de forma a
// obtermos uma min-heap de tamanho p+1
void heapify (int v[], int N) {
    for (int i = 1; i < N; i++) bubbleUp(i,v);
}
// Pior caso: Percorre a árvore toda, para cada elemento do array
// Complexidade: O(N*log N)

// Estratégia 2: bottom-up => Para cada nodo da árvore, desde o mais profundo até à raiz, aplicar a função bubbleDown. A invocação das folhas é desnecessárias, 
// uma vez que não têm sucessores

void heapify2 (int v[], int N) {
    for (int i = PAI(i); i >= 0; i--) bubbleDown(i,v,N);
}
// Pior caso: Para cada nó não-folha, a soma dos custos é proporcional ao nº total de comparações feitas em cada nível da árvore
// Complexidade: O(N)

// Ex 6
// Usando a função bubbleDown, transforma a min-heap h num array ordenado por ordem decrescente
void ordenaHeap (int h[], int N) {
    for (int i = N-1; i > 0; i--) {
        swap(&h[0], &h[i], N);
        bubbleDown(0,h,i);
    }
}



/*
TABELAS DE HASH
*/

unsigned hash(char *str){
    unsigned hash = 5381;
    int c;

    while (c = *str++)
        hash = ((hash << 5) + hash) + c; /* hash * 33 + c */

    return hash;
}

// Chaining
#define Size 10
typedef struct nodo {
    char *chave;
    int ocorr;
    struct nodo *prox;
} Nodo, *THash[Size];

// Inicializa um multi-conjunto a vazio
void initEmpty (THash t) {
    for (int i = 0; i < Size; i++) {
        t[i] = NULL;
    }
}

// Regista mais uma ocorrˆencia de um elemento a um multi-conjunto
void add (char *s, THash t) {
    int idx = hash(s);
    Nodo *current = t[idx];

    while (current != NULL) {
        if (strcmp(current->chave, s) == 0) {
            current->ocorr++;
            return;
        }
        current = current->prox;
    }
    
    Nodo *newNode = (Nodo *)malloc(sizeof(Nodo));
    newNode->chave = strdup(s);
    newNode->ocorr = 1;
    newNode->prox = t[idx];
    t[idx] = newNode;
}

// Calcula a multiplicidade de um elemento num multi-conjunto (quantas vezes aparece)
int lookup (char *s, THash t) {
    int idx = hash(s);
    Nodo *current = t[idx];

    while (current != NULL) {
        if (strcmp(current->chave, s) == 0) {
            return current->ocorr;
        }
        current = current->prox;
    }

    return 0;
}

// Remove uma ocorrência de um elemento de um multi-conjunto
int remove (char *s, THash t) {
    int idx = hash(s);
    Nodo *current = t[idx];
    Nodo *prev = NULL;

    while (current != NULL) {
        if (strcmp(current->chave, s) == 0) {
            if (current->ocorr > 1) {
                current->ocorr--;
                return current->ocorr;
            } else {
                if (prev = NULL) {
                    t[idx] = current->prox;
                } else {
                    prev->prox = current->prox;
                }
                free(current->chave);
                free(current);
                return 0;
            }
        }
        prev = current;
        current = current->prox;
    }

    return 0;
}

// Open-Addressing

#define Size 10
#define Free 0
#define Used 1
#define Del 2

typedef struct bucket {
    int status; // Free | Used | Del
    char *chave; 
    int ocorr;
} THash [Size];

// Calcula o índice de t onde s está (ou devia estar) armazenada
int where(char *s, THash t) {
    unsigned idx = hash(s); // Índice inicial
    unsigned original = idx; // Para detetar loops
    while (t[idx].status != 0 && (t[idx].status == 2 || strcmp(t[idx].chave, s) != 0)) {
        idx = (idx + 1); // Linear probing: avança para o próximo índice
        if (idx == original) // Se voltarmos ao índice inicial, a tabela está cheia
            return -1;
    }
    return idx;
}

// Inicializa um multi-conjunto a vazio
void initEmpty(THash t) {
    for (int i = 0; i < Size; i++) {
        t[i].status = 0;
        t[i].chave = NULL;
        t[i].ocorr = 0;
    }
}

// Regista mais uma ocorrência de um elemento a um multi-conjunto
void add (char *s, THash t) {
    int idx = where(s, t);
    if (idx == -1) {
        return;
    }

    if (t[idx].status == 1) {
        t[idx].ocorr++;
    } else {
        t[idx].status = 1;
        t[idx].chave = strdup(s);
        t[idx].ocorr = 1;
    }
}

// Calcula a multiplicidade de um elemento num multi-conjunto
int lookup(char *s, THash t) {
    int idx = where(s, t);
    if (idx == -1 || t[idx].status != 1) {
        return 0;
    }

    return t[idx].ocorr;
}

// Remove uma ocorrência de um elemento num multi-conjunto
int remove (char *s, THash t) {
    int idx = where(s, t);
    if (idx == -1 || t[idx].status != 1) return 0;

    if (t[idx]->ocorr > 1) {
        t[idx]->ocorr--;
        return t[idx].ocorr;
    }

    t[idx]->status = 2;
    free(t[idx]->chave);
    t[idx->chave] = NULL;
    t[idx]->ocorr = 0;
    return t[idx->ocorr];
}

// Reconstrói a tabela t de modo a não haver chaves apagadas (status == Del)
int garb_collection (THash t) {
    THash temp;
    int r = 0;

    for (int i = 0; i < Size; i++) {
        temp[i].status = Free;
        temp[i].chave = NULL;
        temp[i].ocorr = 0;
    }

    for (int i = 0; i < Size; i++) {
        if (t[i].status == 1) {
            int idx = where(t[i].chave, temp);
            if (idx != -1) {
                temp[idx].status = 1;
                temp[idx].chave = strdup(t[i].chave);
                temp[idx].ocorr = t[i].ocorr;
                r++;
            }
        }
    }

    for (int i = 0; i < Size; i++) {
        if (t[i].status == 2) {
            free(t[i].chave);
        }
        t[i] = temp[i];
    }

    return r;
}
// Complexidade: O(N + k), onde N é o tamanho da tabela, e k é o número de elementos ativos

// Assumindo que se mantém um registo de quantas células da tabela estão apagadas, e que, sempre que o número atinge 25% do tamanho da tabela, é feita
// uma garb_collection, e que o custo de uma remoção sem garb_collection é constante, determinar o custo amortizado da remoção

/*
Antes de aplicar um garb_collection: Custo = 0.25*Size, efetivamente O(Size) (existem 0.25*Size elementos removidos, no máximo, e cada um custa 1)
Aplicação de um garb_collection: Custo = O(Size + k), que no pior caso é O(2*Size) ~= O(Size)
Custo Amortizado: O(Size/(0.25*Size)) = O(4)

Custo total: O(4) + O(1) = O(5)
Efetivamente, é sempre constante, o que significa que não compromete a eficiência global do programa
*/