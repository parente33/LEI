# Relatório de Teste: Engenharia Web (2025/2026)

**Data:** 25 de Maio de 2026

**UC:** Engenharia Web (3º ano - LEI)

**Autor:** Daniel Parente, a107363

---

## 1. Persistência de Dados

A persistência de dados foi inteiramente implementada utilizando o ecossistema **MongoDB**. Foram estruturadas duas bases de dados distintas e isoladas, correndo em serviços independentes no Docker, para garantir a total separação de contextos entre os exercícios:
1. **`jogostabuleiro`**: Destinada ao Exercício 1, contendo a coleção `jogos`.
2. **`leituras`**: Destinada ao Exercício 2, contendo a coleção `livros`.

---

## 2. Setup das Bases de Dados e Modificações nos Datasets

### 2.1 Exercício 1: Jogos de Tabuleiro

Ao analisar o dataset original `jogos.json` (localizado na pasta `ex1`), foram detetadas oportunidades de otimização estrutural. Foi criado e executado o script `json_handler.py` que aplicou as seguintes alterações:
1. O campo de identificação textual `id` original de cada jogo foi mapeado diretamente para o campo de chave primária nativo do MongoDB (`_id`), eliminando a redundância textual.
2. O ficheiro processado e sanitizado foi exportado para `ex1/jogos_ready.json` (este foi o ficheiro efetivamente persistido no MongoDB).

### 2.2 Exercício 2: Lista de Leituras (Engenharia Reversa)

Com base na análise do frontend em Vue.js fornecido no ficheiro `index.html`, derivou-se o modelo de dados Mongoose adequado para a interface. Foi criado um dataset inicial em JSON denominado `ex2/livros.json` contendo 6 registos exemplificativos com os campos: `titulo`, `autor`, `paginas`, `genero` e o estado booleano `lido` (com valor por omissão igual a `false`).

---

## 3. Respostas Textuais Pedidas (Queries MongoDB - Exercício 1.2)

As queries foram testadas e encontram-se registadas no ficheiro [ex1/queries.txt](ex1/queries.txt). Abaixo apresenta-se a listagem das instruções em MongoDB utilizadas:

1. **Quantos jogos estão registados na base de dados?**
   ```javascript
   db.jogos.countDocuments()

2. **Quantos jogos pertencem à categoria "Family"?**
   ```javascript
   db.jogos.countDocuments({ category: "Family" })

3. **Qual a lista de autores (ordenada alfabeticamente e sem repetições)?**
    ```javascript
    db.jogos.aggregate([
        { $unwind: "$autores" },
        { $group: { _id: "$autores.name" } },
         $sort: { _id: 1 } },
        { $project: { _id: 0, autor: "$_id" } }
    ])

4. **Qual a distribuição de jogos por ano de lançamento (quantos jogos foram lançados em cada ano)?**
    ```javascript
    db.jogos.aggregate([
        { $group: { _id: "$year", totalJogos: { $sum: 1 } } },
        { $sort: { _id: -1 } }
    ])

5. **Qual a distribuição de jogos por editora (quantos jogos cada editora tem registados)?**
    ```javascript
    db.jogos.aggregate([
        { $unwind: "$editoras" },
        { $group: { _id: "$editoras.name", totalJogos: { $sum: 1 } } },
        { $sort: { totalJogos: -1, _id: 1 } }
    ])

## 4. Instruções de Execução (Orquestração com Docker Compose)

Toda a arquitetura do exame foi automatizada através de um único ficheiro docker-compose.yml localizado na raiz do projeto. Não é necessária qualquer intervenção manual ou importação de ficheiros por terminal, uma vez que scripts de inicialização (import.sh) populam as bases de dados no momento de arranque dos contentores.

### Passo 1: Clonar o Repositório

Clone o projeto para o seu ambiente local através de uma das seguintes vias:

- Via SSH:
```console
    git@github.com:parente33/ENGWEB2026-Normal.git
```

- Via HTTPS:
```console
    git clone https://github.com/parente33/ENGWEB2026-Normal.git
```

Entre na diretoria principal do projeto:

```console
    cd ENGWEB2025-Normal
```

### Passo 2: Levantar o Ecossistema do Exame

Certifique-se de que possui o motor do Docker ativo e execute o comando abaixo na raiz do repositório. Este comando irá construir os ambientes, expor as portas adequadas e semear as bases de dados de forma totalmente transparente:

```console
docker compose up --build
```

## 5. Aplicações Desenvolvidas e Endpoints Disponíveis

Após o arranque bem-sucedido dos serviços do Docker Compose, as aplicações encontram-se prontas a ser utilizadas e testadas através dos seguintes pontos de acesso:

### Exercício 1: API de Dados - Jogos de Tabuleiro

- Porta da API: A API de dados Express responde na porta especificada no enunciado: 17000.

- Explorar Endpoints (JSON): * Listagem Geral de Jogos: [http://localhost:17000/jogos](http://localhost:17000/jogos)

    - Agregação de Autores: [http://localhost:17000/autores](http://localhost:17000/autores)

    - Agregação de Categorias: [http://localhost:17000/categorias](http://localhost:17000/categorias)

- Interface Swagger: Foi adicionada e configurada uma documentação interativa baseada em Swagger UI, acessível em [http://localhost:17000/api-docs](http://localhost:17000/api-docs).

### Exercício 2: Engenharia Reversa - Lista de Leituras

- Porta da API: A API de suporte Express responde e processa pedidos JSON na porta 19020 [http://localhost:19020/api/livros](http://localhost:19020/api/livros). O servidor MongoDB deste exercício encontra-se devidamente isolado na rede interna do Docker, não expondo portas para o exterior.

- Porta do Frontend (Nginx): A interface Web estática em Vue.js (index.html) é servida autonomamente por uma imagem Nginx Alpine, exposta publicamente na porta pedida: 19021.

- Explorar Aplicação: Abra o seu navegador e aceda diretamente a [http://localhost:19021](http://localhost:19021) para interagir com o gestor de leituras.
