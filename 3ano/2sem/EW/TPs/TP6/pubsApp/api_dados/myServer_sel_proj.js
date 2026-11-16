const express = require('express');
const mongoose = require('mongoose');
const app = express();

app.use(express.json());

// O meu logger
app.use(function(req, res, next){
    var d = new Date().toISOString().substring(0, 16)
    console.log(req.method + " " + req.url + " " + d)
    next()
})

// 1. Conexão ao MongoDB
const nomeBD = "pubs2"
const mongoHost = process.env.MONGO_URL || `mongodb://127.0.0.1:27017/${nomeBD}`
mongoose.connect(mongoHost)
    .then(() => console.log(`MongoDB: liguei-me à base de dados ${nomeBD}.`))
    .catch(err => console.error('Erro:', err));

// 2. Esquema flexível (strict: false permite campos variados do dataset jcrpubs.json)
//      - Mas assume alguns pressupostos... como o tipo do _id
//      - versionKey: false, faz com que o atributo _v não seja adicionado ao documento
const pubSchema = new mongoose.Schema({}, { strict: false, collection: 'pubs', versionKey: false });
const Pub = mongoose.model('Pub', pubSchema);

// 3. Rotas CRUD focadas em _id

// GET /pubs - Listar com FTS, Ordenação e Projeção de Campos
app.get('/pubs', async (req, res) => {
    try {
        let queryObj = { ...req.query };
        
        // 1. Extração de parâmetros especiais
        const searchTerm = queryObj.q;
        const fields = queryObj._select; // Ex: "title,authors,year"
        const sortField = queryObj._sort;
        const order = queryObj._order === 'desc' ? -1 : 1;

        // Limpeza do objeto de query para não filtrar por parâmetros de controlo
        delete queryObj.q;
        delete queryObj._select;
        delete queryObj._sort;
        delete queryObj._order;

        let mongoQuery = {};
        let projection = {};
        let mongoSort = {};

        // 2. Configuração da Pesquisa de Texto
        if (searchTerm) {
            mongoQuery = { $text: { $search: searchTerm } };
            // Score de relevância
            projection.score = { $meta: "textScore" };
            mongoSort = { score: { $meta: "textScore" } };
        } else {
            mongoQuery = queryObj;
        }

        // 3. Configuração da Projeção (_select)
        if (fields) {
            // Converte "title,year" em { title: 1, year: 1 }
            fields.split(',').forEach(f => {
                projection[f.trim()] = 1;
            });
        }

        // 4. Execução da Query
        let execQuery = Pub.find(mongoQuery, projection);

        // Prioridade de ordenação: _sort manual ou textScore se houver pesquisa
        if (sortField) {
            execQuery = execQuery.sort({ [sortField]: order });
        } else if (searchTerm) {
            execQuery = execQuery.sort(mongoSort);
        }

        const pubs = await execQuery.exec();
        res.json(pubs);

    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// GET /pubs/:id - Procurar apenas por _id
app.get('/pubs/:id', async (req, res) => {
    try {
        const pub = await Pub.findById(req.params.id);
        if (!pub) return res.status(404).json({ error: "Não encontrado" });
        res.json(pub);
    } catch (err) {
        res.status(400).json({ error: "ID inválido ou erro de sistema" });
    }
});

// POST /pubs - Inserir publicação
app.post('/pubs', async (req, res) => {
    try {
        const newPub = new Pub(req.body);
        const saved = await newPub.save();
        res.status(201).json(saved);
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
});

// PUT /pubs/:id - Atualizar apenas por _id
app.put('/pubs/:id', async (req, res) => {
    try {
        const updated = await Pub.findByIdAndUpdate(req.params.id, req.body, { new: true });
        if (!updated) return res.status(404).json({ error: "Não encontrado" });
        res.json(updated);
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
});

// DELETE /pubs/:id - Remover apenas por _id
app.delete('/pubs/:id', async (req, res) => {
    try {
        const deleted = await Pub.findByIdAndDelete(req.params.id);
        if (!deleted) return res.status(404).json({ error: "Não encontrado" });
        res.json({ message: "Eliminado com sucesso", id: req.params.id });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.listen(7789, () => console.log('API minimalista em http://localhost:7789/pubs'));