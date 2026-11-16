const express = require('express')
const mongoose = require('mongoose')
const app = express()
const publicationRouter = require('./routes/publicationRouter')

app.use(express.json())

const nomeDB = "pubs"
mongoose.connect(`mongodb://127.0.0.1:27017/${nomeDB}`)
    .then(() => console.log(`MongoDB: liguei-me à base de dados ${nomeDB}.`))
    .catch(err => console.error('Erro:', err));

app.use('/api', publicationRouter)

const PORT = process.env.PORT || 16000
app.listen(PORT, function () {
    console.log(`Servidor ACORDOU à escuta na porta ${PORT}...`)
})