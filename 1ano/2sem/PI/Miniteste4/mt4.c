#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct aluno {
    char *nome;
    int nr;
    int nota;
} Aluno;

typedef struct cel {
    Aluno aluno;
    struct cel *prox;
} *Turma;

int adicionaAluno(Turma *t, char *nome, int nr, int nota) {
    Aluno novo_aluno;
    novo_aluno.nome = malloc(sizeof(char) * (strlen(nome) + 1)); // Espaço 1
    if (novo_aluno.nome == NULL) {
        return 1; 
    }
    strcpy(novo_aluno.nome, nome); 
    novo_aluno.nr = nr;
    novo_aluno.nota = nota;
    struct cel *nova_cel = malloc(sizeof(struct cel)); 
    if (nova_cel == NULL) {
        free(novo_aluno.nome); // Espaço 2
        return 1; 
    }
    nova_cel->aluno = novo_aluno; 

    while (*t != NULL && (*t)->aluno.nr < nr) { // Espaço 3
       t = &((*t)->prox);
    }
 
    nova_cel->prox = *t; // Espaço 4
    *t = nova_cel;
    return 0; 
}