#!/bin/bash
# Importa o JSON para a base de dados pubs2, coleção pubs
mongoimport --host localhost --db ewEMDs --collection ewEMDs --type json --file /docker-entrypoint-initdb.d/emd.json --jsonArray

# Cria o índice de texto necessário para o parâmetro ?q= funcionar
mongosh ewEMDs --eval 'db.pubs.createIndex({dataEMD: "text", nome: "text", idade: "text", género: "text", morada: "text", modalidade: "text", clube: "text"})'