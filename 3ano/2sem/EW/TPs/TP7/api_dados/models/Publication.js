const mongoose = require('mongoose')

const publicationSchema = new mongoose.Schema({
    type: { type: String, required: true },
    id: { type: String, required: true, unique: true },
    authors: [{ type: String, required: true }],
    title: { type: String, required: true },
    booktitle: String,
    address: String,
    year: { type: String, required: true },
    month: String,
    doi: String
})

const Publication = mongoose.model('Publication', publicationSchema, 'pubs')

module.exports = Publication