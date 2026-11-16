#include <stdlib.h>

/*
Árvore AVL => árvore binária de procura que satisfaz, em todos os nós, e a qualquer momento, a seguinte condição:
                                "As alturas da sub-árvore esquerda e da direita diferem no máximo em 1 unidade"
*/

/* Algoritmo de Inserção numa árvore AVL

Caso 0: Inserção à direita não provoca aumento da altura da sub-árvore da direita
Neste caso não há nada a fazer, a relação entre he e hd mantém-se.
Nos restantes casos, haverá aumento da altura da árvore da direita

Caso 1: he = hd + 1
A sub-árvore da esquerda é mais pesada, e neste caso passamos a ter, depois da inserção à
direita, he = hd

Caso 2: he = hd
As duas sub-árvores têm à partida a mesma altura. Neste caso passamos a ter, depois da inserção
à direita, he = hd - 1. A raiz continua a satisfazer o invariante

Caso 3: hd = he + 1
Neste caso, a sub-árvore da direita tem necessariamente duas sub-árvores com a mesma altura h (T2 e T3).
Então, podem surgir dois casos diferentes (ou aumenta-se T2 ou aumenta-se T3). Ambos os casos levam à
invalidação do invariante na raiz (he = hd - 2)

Caso 3a: Aumento da altura de T3

        A                       A
      /   \                   /   \
    T1      C       =>      T1     C
           /  \                  /   \
        T2      T3              T2    \
                                       T3


Procede-se a uma operação de rotação à esquerda: o nó C tomará o papel da raiz, descendo o nó A para a
esquerda de C. A árvore T2 passará a estar à direita de A

        C
      /   \
     A     \
   /   \    T3
  T1   T2

Caso 3b: Aumento da altura T2. Uma simples rotação à esquerda não resolveria o problema. É necessário
efetuar uma rotação à direita, com eixo no nó C. B (raiz de T2) sobe para o lugar de C, que desce para
a sua direita. Em seguida, faz-se à rotação à esquerda com eixo na raíz A, como em 3a.
Agora, o invariante é preservado.

        A                              A                        B
      /   \                          /   \                    /   \
    T1      C                       T1    B                  A      C
           /  \         =>              /  \       =>      /  \    /  \
          B    T3                      T2a   C            T1  T2b T2b  T3
        /  \                                /  \
      T2a  T2b                            T2b  T3
*/

typedef int TreeEntry;
typedef enum balancefactor {LH, EH, RH} BalanceFactor;
// LH -> sub-árvore esq tem maior altura
// RH -> sub-árvore dir tem maior altura
// EH -> alturas iguais

struct treenode {
    BalanceFactor bf;
    TreeEntry entry;
    struct treenode *left;
    struct treenode *right;
};

typedef struct treenode *Tree;

// Não funciona se (t == NULL) ou (t->right == NULL)
// Realiza uma rotação à esquerda da árvore com raiz apontada por t, devolvendo o endereço
// da nova raiz
Tree rotateLeft (Tree t) {
    if (t == NULL || t->right == NULL) return t;

    Tree novaRaiz = t->right; // A nova raiz é o filho direito
    t->right = novaRaiz->left; // A sub-árvore esquerda do filho direito torna-se o filho direito do nodo atual
    novaRaiz->left = t; // O nodo atual torna-se o filho esquerdo da nova raiz

    return novaRaiz;
}

Tree rotateRight (Tree t) {
    if (t == NULL || t->left == NULL) return t;

    Tree novaRaiz = t->left; // A nova raiz é o filho esquerdo
    t->left = novaRaiz->right; // A sub-árvore direita do filho esquerdo torna-se o filho esquerdo da raiz atual
    novaRaiz->right = t; // O nodo original torna-se o filho direito da nova raiz

    return novaRaiz;
}

// Recebendo uma árvore cuja raiz deixou de satisfazer o invariante AVL e que está
// desequilibrada para a direita, corrige a estrutura da árvore (he = hd - 2)
/*
->Começa por determinar se o caso exige uma rotação simples ou dupla, examinando o fato de
balanceamento do nó à direita;
->No segundo caso, é necessário ajustar os indicadores de balanceamento dos nós à esquerda
(A na figura) e à direita (C) da nova raiz da árvore (B), de acordo com o estado atual
do indicador de balanceamento de B;
->Finalmente, ajusta-se o indicador da raiz;
Tal como anteriormente, não funciona se (t == NULL) ou (t->right == NULL)
*/
Tree balanceRight (Tree t) {
    if (t->right->bf == RH) {
        // Rotação simples, caso 3a
        t = rotateLeft(t);
        t->bf = EH;
        t->left->bf = EH;
    } else {
        // Dupla rotação, caso 3b
        t->right = rotateRight(t->right);
        t = rotateLeft(t);
        switch (t->bf) {
            case EH:
                t->left->bf = EH;
                t->right->bf = EH;
                break;
            case LH:
                t->left->bf = EH;
                t->right->bf = RH;
                break;
            case RH:
                t->left->bf = LH;
                t->right->bf = EH;
        }
        t->bf = EH;
    }
    return t;
}

Tree balanceLeft (Tree t) {
    if (t == NULL) return NULL;

    if (t->left->bf == LH) {
        // Rotação simples à direita
        return rotateRight(t);
    } else if (t->left->bf == RH) {
        // Rotação dupla esquerda-direita
        t->left = rotateLeft(t->left); // Roda à esquerda no filho esquerdo
        return rotateRight(t); // Roda à direita no nodo atual
    } else {
        // Caso 3: Sub-árvore esquerda está equilibrada, apenas ajusta-se o fator de balanceamento
        t->bf = EH;
        t->left->bf = EH;
        return t;
    }
}

// Função de inserção numa árvore binária de procura comum
Tree insertTree (Tree t, TreeEntry e) {
    if (t == NULL) {
        t = (Tree)malloc(sizeof(struct treenode));
        t->entry = e;
        t->right = t->left = NULL;
    } else if (e > t->entry) {
        t->right = insertTree(t->right, e);
    } else {
        t->left = insertTree(t->left, e);
    }

    return t;
}

// Função de inserção numa árvore AVL
Tree insertTree (Tree t, TreeEntry e, int *cresceu) {
    if (t == NULL) {
        t = (Tree)malloc(sizeof(struct treenode));
        t->entry = e;
        t->right = t->left = NULL;
        t->bf = EH;
        *cresceu = 1;
    } else if (e > t->entry) {
        t->right = insertTree(t->right,e);
        if (*cresceu) {
            switch (t->bf) {
                case LH:
                    t->bf = EH;
                    *cresceu = 0;
                    break;
                case EH:
                    t->bf = RH;
                    *cresceu = 1;
                    break;
                case RH:
                    t = balanceRight(t);
                    *cresceu = 0;
                    break;
            }
        }
    } else {
        t->left = insertTree(t->left,e);
        if (*cresceu) {
            switch (t->bf) {
                case RH:
                    t->bf = EH;
                    *cresceu = 0;
                    break;
                case EH:
                    t->bf = LH;
                    *cresceu = 1;
                    break;
                case LH:
                    t = balanceLeft(t);
                    *cresceu = 0;
                    break;
            }
        }
        *cresceu = 0;
    }
    return t;
}

struct treenodeAVL {
    BalanceFactor bf;
    TreeEntry entry;
    struct treenodeAVL *left;
    struct treenodeAVL *right;
    int height; // Armazena a altura do nodo
};

typedef struct treenodeAVL *Tree2;

// Calcular a altura de uma árvore binária
int nonAVL_treeHeightON (Tree t) {
    int l, r;
    if (t == NULL) return 0;
    l = treeHeight(t->left);
    r = treeHeight(t->right);
    if (l > r) return l+1;
    else return r+1;
}

int nonAVL_treeHeightOlogN (Tree2 t) {
    if (t == NULL) return 0;
    return t->height;
}

// Recalcular a altura de um nó (por exemplo)
void updateHeigth (Tree2 t) {
    if (t == NULL) return;
    int leftHeight = t->left ? t->left->height : 0;
    int rightHeight = t->right ? t->right->height : 0;
    t->height = (leftHeight > rightHeight ? leftHeight : rightHeight) + 1;
}

// Testar se uma árvore é uma AVL
int isAVL (Tree t) {
    int l, r;
    if (t == NULL) return 1;
    l = treeHeight(t->left);
    r = treeHeight(t->right);
    
    return (abs (l-r) <= 1 && isAVL(t->left) && isAVL(t->right));
}

// Calcula simultaneamente a altura da árvore, dispensando a utilização da função treeHeight
int isAVL_aux (Tree t, int *p) {
    if (t == NULL) {
        *p = 0;
        return 1;
    }
    int lh = 0, rh = 0;

    int leftAVL = isAVL_aux(t->left, &lh);
    int rightAVL = isAVL_aux(t->right, &rh);

    *p = (lh > rh ? lh : rh) + 1;

    return (abs(lh - rh) <= 1) && leftAVL && rightAVL;
}

int isAVL_opt (Tree a) {
    int p;
    return (isAVL_aux(a,&p));
}