#!/bin/bash
echo "A aguardar pelo MongoDB..."
until mongosh --eval "print(\"waited for connection\")" > /dev/null 2>&1; do
    sleep 1
done

mongoimport --db leituras --collection livros --file /docker-entrypoint-initdb.d/livros.json --jsonArray