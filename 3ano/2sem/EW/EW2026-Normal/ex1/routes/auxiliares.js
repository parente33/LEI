const express = require('express');
const router = express.Router();
const JogoController = require('../controllers/jogo');

/* GET /autores */
router.get('/autores', function(req, res, next) {
    JogoController.listAutores()
        .then(dados => res.json(dados))
        .catch(erro => res.status(500).json({ erro: erro.message }));
});

/* GET /categorias */
router.get('/categorias', function(req, res, next) {
    JogoController.listCategorias()
        .then(dados => res.json(dados))
        .catch(erro => res.status(500).json({ erro: erro.message }));
});

module.exports = router;