const Livro = require('../models/livro');

// GET /api/livros com suporte a pesquisa por título ou autor
module.exports.list = (search) => {
    if (search) {
        return Livro.find({
            $or: [
                { titulo: { $regex: search, $options: 'i' } },
                { autor: { $regex: search, $options: 'i' } }
            ]
        }).exec();
    }
    return Livro.find().exec();
};

// POST /api/livros
module.exports.insert = (livro) => {
    const novo = new Livro(livro);
    return novo.save();
};

// PUT /api/livros/:id
module.exports.updateEstado = (id, estado) => {
    return Livro.findByIdAndUpdate(id, { lido: estado }, { new: true }).exec();
};

// DELETE /api/livros/:id
module.exports.remove = (id) => {
    return Livro.findByIdAndDelete(id).exec();
};