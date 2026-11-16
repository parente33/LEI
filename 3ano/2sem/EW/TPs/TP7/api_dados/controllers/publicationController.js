const Publication = require('../models/Publication')

const publicationController = {
    createPublication: async function(req, res) {
        try {
            const newPublication = new Publication(req.body)
            await newPublication.save()
            res.status(201).json(newPublication)
        }
        catch (erro) {
            res.status(400).json({ error: erro.message })
        }
    },

    getAllPublications: async function(req, res) {
        try {
            const publication = await Publication.find()
            res.json(publication)
        }
        catch (erro) {
            res.status(500).json({ error: erro.message })
        }
    },

    getPublicationById: async function(req, res) {
        try {
            const publication = await Publication.findById(req.params.id)
            if (!publication) {
                res.status(404).json({message: "Publication not found..."})
            }
            else {
                res.json(publication)
            }
        }
        catch (erro) {
            res.status(500).json({ error: erro.message })
        }
    },

    updatePublication: async function(req, res) {
        try {
            const publication = await Publication.findByIdAndUpdate(req.params.id, req.body, {new: true})
            if (!publication) {
                res.status(404).json({message: "Publication not found..."})
            }
            else {
                res.json(publication)
            }
        }
        catch (erro) {
            res.status(400).json({ error: erro.message })
        }
    },

    deletePublication: async function(req, res) {
        try {
            const publication = await Publication.findByIdAndDelete(req.params.id)
            if (!publication) {
                res.status(404).json({message: "Publication not found..."})
            }
            else {
                res.json({message: "Publication neutralized with success."})
            }
        }
        catch (erro) {
            res.status(400).json({ error: erro.message })
        }
    },

    getStats: async function(req, res) {
        try {
            const publications = await Publication.find()
            res.json({ count: publications.length })
        }
        catch (erro) {
            res.status(500).json({ error: erro.message })
        }
    }
}

module.exports = publicationController