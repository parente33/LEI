const express = require('express');
const router = express.Router();
const LivroController = require('../controllers/livro');

// GET /api/livros
router.get('/', function(req, res) {
    LivroController.list(req.query.search)
        .then(dados => res.json(dados))
        .catch(erro => res.status(500).json({ erro: erro.message }));
});

// POST /api/livros
router.post('/', function(req, res) {
    LivroController.insert(req.body)
        .then(dados => res.status(201).json(dados))
        .catch(erro => res.status(500).json({ erro: erro.message }));
});

// PUT /api/livros/:id
router.put('/:id', function(req, res) {
    LivroController.updateEstado(req.params.id, req.body.lido)
        .then(dados => res.json(dados))
        .catch(erro => res.status(500).json({ erro: erro.message }));
});

// DELETE /api/livros/:id
router.delete('/:id', function(req, res) {
    LivroController.remove(req.params.id)
        .then(dados => res.json(dados))
        .catch(erro => res.status(500).json({ erro: erro.message }));
});

module.exports = router;