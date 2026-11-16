const express = require('express');
const axios = require('axios');
const path = require('path');
const app = express();

// Configurações do Express
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug');
app.use(express.static('public'));

// URL da API (Se estiveres a correr fora do Docker, usa localhost)
const API_URL = process.env.API_URL || "http://localhost:7789/pubs";

app.get('/publicacoes', (req, res) => {
    const d = new Date().toISOString().substring(0, 16);
    
    // Faz o pedido à API de dados
    axios.get(API_URL + '?_select=id,type,title,year')
        .then(response => {
            res.render('index', { 
                pubs: response.data, 
                date: d 
            });
        })
        .catch(err => {
            res.render('error', { 
                error: err, 
                message: "Erro ao obter dados da API" 
            });
        });
});

app.get('/publicacoes/stats', (req, res) => {
    const d = new Date().toISOString().substring(0, 16);
    
    // Faz o pedido à API de dados
    axios.get(API_URL)
        .then(response => {
            let pubs = response.data;
            let stats = {
                ano: {}, tipo: {}, autor: {}
            };

            pubs.forEach(p => {
                stats.ano[p.year] = (stats.ano[p.year] || 0) + 1;
                stats.tipo[p.type] = (stats.tipo[p.type] || 0) + 1;
                p.authors.forEach(a => {
                    stats.autor[a] = (stats.autor[a] || 0) + 1;
                })
            });
            res.render('stats', { stats: stats, date: d})
        })
        .catch(err => {
            res.render('error', { 
                error: err, 
                message: "Erro ao obter dados da API" 
            });
        });
});

const PORT = 7790;
app.listen(PORT, () => {
    console.log(`Servidor de Interface em http://localhost:${PORT}/publicacoes`);
});