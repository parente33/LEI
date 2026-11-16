#!/bin/bash

# Exercício 1
gcc -o passwdleak passwdleak.c
./passwdleak

# Exercício 2
# A vulnerabilidade, tal como na secção 1, é que não se fecha o file descriptor antes de retirar os privilégios
# e chamar o execl, dando a possibilidade de dar a um utilizador normal a capacidade de aceder a um ficheiro com privilégios que não deveria ter direito (write)

# Exercício 3
# Um utilizador normal pode alterar a consituição do ficheiro, potencialmente apagando entradas deste.
./passwdleak << 'EOF'
echo "ssihacker::0:0::/root:/bin/sh" >&3
EOF

# Exercício 4
# A linha adicionada cria um novo utilizador, com username "ssihacker", UID = 0, GID = 0, /root como home directory e /bin/sh como shell.
# Ao ter UID a 0, significa que o utilizador passa a ter privilégios de root, o que pode ter ações catastróficas.

# Exercício 5
# Correção: mais uma vez, fechar o fd antes de retirar os privilégios
cat << 'EOF' > passwdleak.c
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>

int main() 
{
	int fd = open("/etc/passwd", O_WRONLY | O_APPEND);
	if (fd < 0)
	{
		perror("open /etc/passwd");
		exit(1);
	}

	printf("Passwd FD leaked: %d\n", fd);

	close(fd); // correção aqui!	

	setuid(getuid());
	execl("/bin/sh", "sh", NULL);
}
EOF
