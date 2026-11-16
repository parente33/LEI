#!/bin/bash
# Importa o JSON para a base de dados pubs2, coleção pubs
mongoimport --host localhost --db pubs2 --collection pubs --type json --file /docker-entrypoint-initdb.d/jcrpubs.json --jsonArray

# Cria o índice de texto necessário para o parâmetro ?q= funcionar
mongosh pubs2 --eval 'db.pubs.createIndex({title: "text", authors: "text", booktitle: "text", publisher: "text"})'