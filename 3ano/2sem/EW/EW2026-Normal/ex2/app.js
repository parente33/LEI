const express = require('express');
const logger = require('morgan');
const cors = require('cors');
const mongoose = require('mongoose');

// Configuração da BD interna do Docker (sem expor para fora)
const mongoURI = process.env.MONGO_URL || 'mongodb://mongo_leitura:27017/leituras';
mongoose.connect(mongoURI)
  .then(() => console.log('API conectada ao MongoDB em: ' + mongoURI))
  .catch(err => console.error('Erro de ligação ao MongoDB:', err));

const livrosRouter = require('./routes/livros');

const app = express();

app.use(cors()); // Permite que o Vue comunique com a API sem bloqueios de cors
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));

// Monta o roteador estritamente no prefixo /api/livros exigido pelo index.html
app.use('/api/livros', livrosRouter);

app.use(function(req, res) {
  res.status(404).json({ erro: "Endpoint não encontrado" });
});

module.exports = app;