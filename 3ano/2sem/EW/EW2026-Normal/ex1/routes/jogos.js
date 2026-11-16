const express = require('express');
const router = express.Router();
const JogoController = require('../controllers/jogo');

/* GET listagem de jogos (ou filtro por editora) */
router.get('/', function(req, res, next) {
    if (req.query.editora) {
        JogoController.findByEditora(req.query.editora)
            .then(dados => res.json(dados))
            .catch(erro => res.status(500).json({ erro: erro.message }));
    } else {
        JogoController.list()
            .then(dados => res.json(dados))
            .catch(erro => res.status(500).json({ erro: erro.message }));
    }
});

/* GET consulta individual de jogo */
router.get('/:id', function(req, res, next) {
    JogoController.findById(req.params.id)
        .then(dados => {
            if (dados) res.json(dados);
            else res.status(404).json({ mensagem: "Jogo não encontrado" });
        })
        .catch(erro => res.status(500).json({ erro: erro.message }));
});

/* POST criação de jogo */
router.post('/', function(req, res, next) {
    JogoController.insert(req.body)
        .then(dados => res.status(201).json(dados))
        .catch(erro => res.status(500).json({ erro: erro.message }));
});

/* PUT atualização de jogo */
router.put('/:id', function(req, res, next) {
    JogoController.update(req.params.id, req.body)
        .then(dados => res.json(dados))
        .catch(erro => res.status(500).json({ erro: erro.message }));
});

/* DELETE remoção de jogo */
router.delete('/:id', function(req, res, next) {
    JogoController.remove(req.params.id)
        .then(dados => res.json({ mensagem: "Removido com sucesso", dados: dados }))
        .catch(erro => res.status(500).json({ erro: erro.message }));
});

module.exports = router;