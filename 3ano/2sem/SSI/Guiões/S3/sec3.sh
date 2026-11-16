#!/bin/bash

exit

# Exercício 1
cat << 'EOF' > leitor.c
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[]) {
	if (argc != 2) {
		fprintf(stderr, "Erro: apenas um ficheiro a ser indicado por favor.\n");
		return 1;
	}

	FILE *f = fopen(argv[1], "r");
	if (f == NULL) {
		perror("Erro ao abrir ficheiro");
		return 1;
	}

	int c;
	while ((c = fgetc(f)) != EOF) {
		putchar(c);
	}

	fclose(f);
	return 0;
}
EOF

gcc -Wall -o leitor leitor.c

# Exercício 2
sudo adduser userssi

# Exercício 3
sudo chown userssi leitor
sudo chown userssi braga.txt

# Exercício 4
./leitor braga.txt

# Exercício 5
sudo chmod u+s leitor

# Exercício 6
./leitor braga.txt

# Comentário
# O programa agora é executado com o UID do dono (userssi). Dessa forma, mesmo que um outro
# utilizador execute o programa, o acesso ao ficheiro será feito com privilégios de userssi,
# podendo permitir a leitura caso esse user não tivesse permissões diretas
