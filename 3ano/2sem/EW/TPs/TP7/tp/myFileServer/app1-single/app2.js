const express = require('express');
const multer = require('multer')
const fs = require('fs');

const app = express();
const PORT = 18001;

// 1. Configuração do Armazenamento do Multer
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        const dir = './uploads';
        // Cria a pasta uploads caso não exista
        if (!fs.existsSync(dir)) {
            fs.mkdirSync(dir);
        }
        cb(null, dir);
    },
    filename: function (req, file, cb) {
        // Guarda o ficheiro com o nome original + timestamp para evitar sobreposições
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        cb(null, uniqueSuffix + '-' + file.originalname);
    }
});

const upload = multer({ storage: storage });

// 2. Servir ficheiros estáticos (HTML, JS, CSS)
app.use(express.static('public'));

// 4. Rota POST para receber os ficheiros
// O nome 'myFile' deve coincidir com o atributo 'name' do teu input HTML
app.post('/ficheiro', upload.array('myFile'), function (req, res) {
    console.log("Informação sobre o ficheiro:")
    console.log(JSON.stringify(req.files))
    console.log("Informação sobre os campos textuais:")
    console.log(JSON.stringify(req.body))
    res.send(`<p>Recebi 1 ficheiro: ${JSON.stringify(req.file)}</p>`)
})

app.listen(PORT, () => {
    console.log(`Servidor a correr em http://localhost:${PORT}`);
});