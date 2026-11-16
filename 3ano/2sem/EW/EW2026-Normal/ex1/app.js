const express = require('express');
const logger = require('morgan');
const mongoose = require('mongoose');
const swaggerUi = require('swagger-ui-express');
const swaggerDocument = require('./swagger.json');
const cors = require('cors');

// Configuração da ligação ao MongoDB com fallback local
const mongoURI = process.env.MONGO_URL || 'mongodb://127.0.0.1:27017/jogostabuleiro';
mongoose.connect(mongoURI)
  .then(() => console.log('Conectado ao MongoDB com sucesso em: ' + mongoURI))
  .catch(err => console.error('Erro na conexão ao MongoDB:', err));

const jogosRouter = require('./routes/jogos');
const auxiliaresRouter = require('./routes/auxiliares');

const app = express();
app.use(cors());

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));

// Interface Swagger injetada na rota /api-docs
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument));

// Configuração dos roteadores
app.use('/', auxiliaresRouter); // Trata /autores e /categorias
app.use('/jogos', jogosRouter);   // Trata /jogos e /jogos/:id

// Catch 404 e erro global simples (padrão API JSON)
app.use(function(req, res) {
  res.status(404).json({ erro: "Rota não encontrada" });
});

app.use(function(err, req, res, next) {
  res.status(err.status || 500).json({ erro: err.message });
});

module.exports = app;