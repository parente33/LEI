const Jogo = require('../models/jogo');

// GET /jogos
module.exports.list = () => {
    return Jogo.find({}, '_id name year category minPlayers').exec();
};

// GET /jogos/:id
module.exports.findById = id => {
    return Jogo.findById(id).exec();
};

// GET /jogos?editora=EEEE
module.exports.findByEditora = editora => {
    return Jogo.find({ "editoras.name": editora }, '_id name year').exec();
};

// GET /autores
module.exports.listAutores = () => {
    return Jogo.aggregate([
        { $unwind: "$autores" },
        {
            $group: {
                _id: "$autores.name",
                jogos: { $push: { id: "$_id", name: "$name" } }
            }
        },
        { $sort: { _id: 1 } },
        {
            $project: {
                _id: 0,
                autor: "$_id",
                jogos: "$jogos"
            }
        }
    ]).exec();
};

// GET /categorias
module.exports.listCategorias = () => {
    return Jogo.aggregate([
        { $group: {
                _id: "$category",
                jogos: { $push: { id: "$_id", name: "$name" } }
            }
        },
        { $sort: { _id: 1 } },
        {
            $project: {
                _id: 0,
                categoria: "$_id",
                jogos: "$jogos"
            }
        }
    ]).exec();
};

// POST /jogos
module.exports.insert = jogo => {
    // Se o objeto vier com 'id', mapeamos para '_id' por segurança
    if (jogo.id && !jogo._id) {
        jogo._id = jogo.id;
    }
    const novo = new Jogo(jogo);
    return novo.save();
};

// DELETE /jogos/:id
module.exports.remove = id => {
    return Jogo.findByIdAndDelete(id).exec();
};

// PUT /jogos/:id
module.exports.update = (id, jogo) => {
    return Jogo.findByIdAndUpdate(id, jogo, { new: true }).exec();
};