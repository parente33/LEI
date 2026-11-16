#!/bin/bash
echo "A aguardar que o MongoDB inicialize..."
until mongosh --eval "print(\"waited for connection\")" > /dev/null 2>&1; do
    sleep 1
done

mongoimport --db jogostabuleiro --collection jogos --file /docker-entrypoint-initdb.d/jogos_ready.json --jsonArray