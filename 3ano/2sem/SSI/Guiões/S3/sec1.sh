#!/bin/bash

# Exercício 1
echo "Ficheiro de Lisboa" > lisboa.txt
echo "Ficheiro do Porto" > porto.txt
echo "Ficheiro de Braga" > braga.txt

# Exercício 2
ls -l lisboa.txt

# Exercício 3
chmod u+rw,g+rw,o+rw lisboa.txt

# Exercício 4
chmod u+rx,u-w porto.txt

# Exercício 5
chmod a-r,u+r braga.txt

# Exercício 6
mkdir dir1
mkdir dir2
ls -ld dir1
ls -ld dir2

# Exercício 7
chmod a-x,u+x dir2
