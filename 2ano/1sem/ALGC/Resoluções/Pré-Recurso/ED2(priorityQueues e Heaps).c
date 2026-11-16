#include <stdlib.h>
#include <stdio.h>

#define PARENT(i) ((i-1)/2)
#define LEFT(i) (2*i + 1)
#define RIGHT(i) (2*i + 2)

typedef int Elem; // os elementos da heap

typedef struct {
    int size; // comp do array alocado
    int used; // tamanho atual da heap
    Elem *value;
} Heap;

void swap (Elem h[], int a, int b) {
    int t = h[a];
    h[a] = h[b];
    h[b] = t;
}

void bubbleDown (int i, Heap h[]) {
    int p = PARENT(i);
    int e = LEFT(i);
    int d = RIGHT(i);

    if (e > h->used && d > h->used) return;

    if (h->value[p] <= h->value[e] && h->value[p] <= h->value[d]) return;

    while ((e <= h->used && d <= h->used) && (h->value[p] > h->value[e] || h->value[p] > h->value[d])) {
        if (h->value[e] > h->value[d]) {
            swap (h, p, d);
        } else {
            swap (h, p, e);
        }
    }
}

// Inicializa uma heap (passada por referência), alocando para isso um array de comprimento size
void initHeap (Heap *h, int size) {
    h->size = size;
    h->used = 0;
    h->value = calloc(size, sizeof(Elem));
    if (h->value == NULL) {
        fprintf(stderr, "Erro ao alocar memória\n");
        return;
    } 
}

int insertHeap (Heap *h, Elem x) {
    // Se a heap está cheia, duplica o seu tamanho
    if (h->used == h->size) {
        h->size *= 2;
        h->value = (Elem *)realloc(h->value, h->size * sizeof(Elem));
        if (h->value == NULL) {
            fprintf(stderr, "Erro ao alocar memória\n");
            return -1;
        }
    }
    // Insere na última posição
    int i = h->used++;
    h->value[i] = x;

    // Sobe o elemento até estar na posição correta
    while (i > 0 && i < h->size && h->value[PARENT(i)] > h->value[i]) {
        Elem temp = h->value[i];
        h->value[i] = h->value[PARENT(i)];
        h->value[PARENT(i)] = temp;
        i = PARENT(i);
    }
    return 0;
}

int extractMin (Heap *h, Elem *x) {
    if (h->used == 0) return -1;
    // O último elemento passa a estar na raiz
    *x = h->value[0];
    h->value[0] = h->value[--h->used];

    // Bubble down do elemento até estar na posição correta
    bubbleDown(h->value[0], h);
}

int minHeapOK (Heap h) {
    for (int i = 0; i < h.used; i++) {
        if (LEFT(i) < h.used && h.value[LEFT(i)] < h.value[i]) {
            return 0;
        }
        if (RIGHT(i) < h.used && h.value[RIGHT(i)] < h.value[i]) {
            return 0;
        }
    }
    return 1;
}