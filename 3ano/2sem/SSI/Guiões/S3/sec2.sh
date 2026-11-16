#!/bin/bash

# Exercício 0
cat /etc/passwd
cat /etc/group

# Exercício 1
sudo adduser andre
sudo adduser daniel
sudo adduser pedro

# Exercício 2
sudo groupadd grupo-ssi
sudo usermod --append --groups grupo-ssi andre
sudo usermod --append --groups grupo-ssi daniel
sudo usermod --append --groups grupo-ssi pedro

sudo groupadd par-ssi
sudo usermod --append --groups par-ssi daniel
sudo usermod --append --groups par-ssi pedro

# Exercício 3
cat /etc/passwd
cat /etc/group

# Diferenças
# Novas entradas em /etc/passwd para os users criados (andre, daniel e pedro)
# Novas entradas em /etc/group para os grupos criados (grupo-ssi e par-ssi)

# Exercício 4
sudo chown pedro braga.txt

# Exercício 5
cat braga.txt

# Exercício 6
su - daniel
# Não sei se é este o user, no exercício 3 não se fala num em específico

# Exercício 7
id
groups

# Comentário
# groups mostra apenas os nomes dos grupos a que o user pertence, id mostra os UIDs e IDs para estes grupos

# Exercício 8
cat braga.txt

# Comentário
# Permission denied, não era o owner

# Exercício 9
cd dir2

# Comentário
# Permission denied, não era o owner
