# Imagem base
FROM node:22

# Diretório de trabalho
WORKDIR /app

# Copiar ficheiros de dependências
COPY package*.json ./

# Instalar dependências
RUN npm install

# Copiar o resto do código
COPY . .

# Porta da Interface
EXPOSE 19020

# Ficheiro principal da Interface
CMD ["npm", "start"]
