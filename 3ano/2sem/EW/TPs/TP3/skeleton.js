/* --- skeleton.js: estrutura base de um servidor de uma API de dados
   --- ------------ a usar o json-server para suportar o modelo de dados
   --- 2026-02-16 by jcr
   --------------------------------------------------------------------- */
const axios = require('axios')
const http = require('http')
const mymod = require('./aux.js')

http.createServer(async function (req, res) {
    console.log(req.method + " " + req.url + " " + mymod.myDateTime())

    if (req.method === "GET") {

        if (req.url) {

        }
        else if(req.url){

        }
        else if(req.url){

        }
        else {
            res.writeHead(404, { 'Content-Type': 'application/json' })
            res.end(JSON.stringify({
                erro: "Rota nÃ£o suportada",
                metodo: req.method,
                caminho: req.url
            }))
        }

    } else {
        res.writeHead(405, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({
            erro: "MÃ©todo nÃ£o permitido",
            metodo: req.method
        }))
    }

}).listen(25000)

console.log("Servidor Ã  escuta na porta 25000...")
