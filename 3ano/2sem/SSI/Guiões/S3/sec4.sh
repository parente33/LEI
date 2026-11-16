#!/bin/bash

sudo apt install acl

# Exercício 1
getfacl porto.txt

# Exercício 2
sudo setfacl -m g:grupo-ssi:w porto.txt

# Exercício 3
getfacl porto.txt

# Comentários
#

# Exercício 4
su - andre
echo "Conteúdo alterado" > porto.txt
cat porto.txt

# Comentários
#
