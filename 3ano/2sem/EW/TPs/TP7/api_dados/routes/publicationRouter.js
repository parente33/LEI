const express = require('express')
const router = express.Router()
const publicationController = require('../controllers/publicationController')

// Nova publicação
router.post('/publications', publicationController.createPublication)

// Listar publicaçóes
router.get('/publications', publicationController.getAllPublications)

// Estatísticas
router.get('/publications/stats', publicationController.getStats)

// Consultar uma publicação
router.get('/publications/:id', publicationController.getPublicationById)

// Alterar uma publicação
router.put('/publications/:id', publicationController.updatePublication)

// Apagar uma publicação
router.delete('/publications/:id', publicationController.deletePublication)

module.exports = router