#define NV 10
#include <stdlib.h>

typedef struct aresta {
    int dest;
    int custo;
    struct aresta *prox;
} *LAdj, *GrafoL [NV];

typedef int GrafoM [NV][NV];

// 1 Representações

// Ex 1
// Constrói o grafo out a partir do grafo in. Considera-se que in[i][j] == 0
// sse não existe a aresta i->j
void fromMat (GrafoM in, GrafoL out) {
    for (int i = 0; i < NV; i++) {
        out[i] = NULL;
        for (int j = 0; j < NV; j++) {
            if (in[i][j] != 0) {
                LAdj novo = malloc(sizeof(struct aresta));
                novo[i].dest = j;
                novo[i].custo = in[i][j];
                novo[i].prox = out[i];
                out[i] = novo;
            }
        } 
    }
}

// Ex 2
// Constói o grafo out como o inverso do grafo in
void inverte (GrafoL in, GrafoL out) {    
    for (int i = 0; i < NV; i++) {
        out[i] = NULL;
        for (LAdj j = in[i]; j != NULL; j = j->prox) {
            LAdj novo = malloc(sizeof(struct aresta));
            novo->dest = i;
            novo->custo = j->custo;
            novo->prox = out[j->dest];
            out[j->dest] = novo;
        }
    }
}

// Ex 3
// Calcula o grau de entrada de um grafo
// Grau de entrada: o nº máximo de arestas que têm como destido um qualquer vértice
int inDegree (GrafoL g) {
    int a[NV];

    for (int i = 0; i < NV; i++) {
        a[i] = NULL;
    }

    for (int j = 0; j < NV; j++) {
        for (LAdj k = g[j]; k != NULL; k = k->prox) {
            a[j]++;
        }
    }

    return maximum(a, NV);
}

int maximum (int s[], int N) {
    int i, max = s[0];
    
    if (N <= 0) return -1;

    for (i = 1; i < N; i++) {
        if (s[i] > max) {
            max = s[i];
        }
    }

    return max;
}

// Ex 4
// Verifica se o array cor corresponde a uma coloração válida do grafo
// Coloração válida: Dois vértices adjacentes não podem ter a mesma cor
int colorOK (GrafoL g, int cor[]) {
    struct aresta *x;
    for (int i = 0; i < NV; i++) {
        for (x = g[i]; x != NULL; x = x->prox) {
            if (cor[i] == cor[x->dest]) return -1;
        }
    }
    return 0;
}

// Ex 5
// Verifica se a função f é um homomorfismo de g para h
// Um homomorfismo de um grafo g para um grafo h é uma função f (representada como um array de inteiros) 
// que converte os vértices de g nos vértices de h tal que, para cada aresta a−>b de g existe uma aresta f(a)->f(b) em h
int homomorfOK (GrafoL g, GrafoL h, int f[]) {
    for (int i = 0; i < NV; i++) {
        for (LAdj j = g[i]; j != NULL; j = j->prox) {
            int m = f[i];
            int n = f[j->dest];

            struct aresta *x;
            int found = 0;

            for (x = h[m]; x != NULL; x = x->prox) {
                if (x->dest == n) {
                    found = 1;
                    break;
                }
            }
            if (!found) return 0;
        }
    }
    return 1;
}


// 2 Travessias
int DF (GrafoL g, int or, int v[], int p[], int l[]) {
    int i;
    for (i=0; i<NV; i++) {
        v[i]=0;
        p[i] = -1;
        l[i] = -1;
    }
    p[or] = -1; l[or] = 0;
    return DFRec (g,or,v,p,l);
}

int DFRec (GrafoL g, int or, int v[], int p[], int l[]) {
    int i; LAdj a;
    i=1;
    v[or]=-1;
    for (a=g[or]; a!=NULL; a=a->prox)
        if (!v[a->dest]) {
            p[a->dest] = or;
            l[a->dest] = 1+l[or];
            i+=DFRec(g,a->dest,v,p,l);
        }
    v[or]=1;
    return i;
}

int BF (GrafoL g, int or, int v[], int p[], int l[]) {
    int i, x; LAdj a;
    int q[NV], front, end;
    for (i=0; i<NV; i++) {
        v[i]=0;
        p[i] = -1;
        l[i] = -1;
    }
    front = end = 0;
    q[end++] = or; //enqueue
    v[or] = 1; l[or]=0; p[or]=-1; //redundante
    i=1;
    while (front != end) {
        x = q[front++]; //dequeue
        for (a=g[x]; a!=NULL; a=a->prox)
            if (!v[a->dest]){
                i++;
                v[a->dest]=1;
                p[a->dest]=x;
                l[a->dest]=1+l[x];
                q[end++]=a->dest; //enqueue
            }
    }
return i;
}

int maisLonga (GrafoL g, int or, int path[]) {
    int v[NV], p[NV], l[NV], max, n, i;

    BF(g,or,v,p,l);
    n = or;
    max = 0;
    for (i = 0; i < NV; i++) {
        if (max < l[i]) {
            n = i;
            max = l[i];
        }
    }
    for (i = max; i >= 0; i--) {
        path[i] = n;
        n = p[n];
    }
    return max;\        
}

int componentes (GrafoL g, int c[]) {
    
}