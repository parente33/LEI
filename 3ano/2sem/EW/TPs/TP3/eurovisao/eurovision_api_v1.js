/* --- eurovision_api_v1.js: servidor de uma API de dados
   --- ------------ a usar o json-server para suportar o modelo de dados
   --- 2026-02-16 by jcr
   ---
   --- Rotas implementadas:
        GET /edicoes : [id, anoEdiÃ§Ã£o, organizador, vencedor]
        GET /edicoes/:id : { todos os campos de um ediÃ§Ã£o }
        GET /edicoes?organizador=EEEE : [anoEdiÃ§Ã£o, organizador, vencedor]
        GET /edicoes?vencedor=EEEE : [anoEdiÃ§Ã£o, organizador, vencedor]
        GET /edicoes/musicas : [id, anoEdiÃ§Ã£o, nmusicas]
        GET /paises?papel=org : [(paÃ­s, [anos])]
        GET /paises?papel=venc : [(paÃ­s, [anos])]
        GET /paises/:paÃ­s : {participaÃ§Ãµes: [ano, tÃ­tulo da canÃ§Ã£o, intÃ©rprete], vitÃ³rias: [ano, tÃ­tulo da canÃ§Ã£o, intÃ©rprete], organizaÃ§Ãµes: [ano, tÃ­tulo da canÃ§Ã£o, intÃ©rprete]}
        GET /interpretes : [(paÃ­s, interp)]
        GET /interpretes/:interp : [(ano, tÃ­tulo, venceu)]
   --------------------------------------------------------------------- */
const axios = require('axios')
const http = require('http')

/* --- FunÃ§Ãµes auxiliares --- */

function getParticipacao(lista, ano, país){
    var res = {}, encontrado = false, i = 0
    while(i < lista.length && !encontrado){
        if(lista[i].país == país){
            res = {
                    ano: ano,
                    tit: lista[i].título,
                    interp: lista[i].intérprete
            }
            encontrado = true
        }
        i = i+1
    }
    return res
}

function getParticipacaoByInterp(lista, ano, interp){
    var res = {}, encontrado = false, i = 0
    while(i < lista.length && !encontrado){
        if(lista[i].intérprete == interp){
            res = {
                    ano: ano,
                    tit: lista[i].título,
                    país: lista[i].país
            }
            encontrado = true
        }
        i = i+1
    }
    return res
}

// --------------------------------------------------------------------

http.createServer(async function (req, res) {
    var d = new Date().toISOString().substring(0, 16)
    console.log(req.method + " " + req.url + " " + d)

    if (req.method === "GET") {

        if (req.url == "/edicoes") {
            try {
                const resp = await axios.get('http://localhost:3000/edicoes')
                let dados = resp.data
                // Filtragem
                const resultado = dados.map(e => ({
                                id: e.id,
                                anoEdição: e.anoEdição,
                                organizacao: e.organizacao,
                                vencedor: e.vencedor
                            }))
                            res.writeHead(200, { 'Content-Type': 'application/json' })
                            res.end(JSON.stringify(resultado))

            } catch (error) {
                res.writeHead(502, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify({
                    erro: "Erro ao contactar o servidor de dados",
                    detalhe: error.message
                }))
            }
        }
        else if (req.url == "/edicoes/musicas") {
            try {
                const resp = await axios.get('http://localhost:3000/edicoes')
                let dados = resp.data
                // Filtragem
                const resultado = dados.map(e => ({
                    id: e.id,
                    anoEdição: e.anoEdição,
                    nmusicas: e.musicas.length
                }))
                res.writeHead(200, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify(resultado))

            } catch (error) {
                res.writeHead(502, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify({
                    erro: "Erro ao contactar o servidor de dados",
                    detalhe: error.message
                }))
            }
        }
        else if(req.url.startsWith('/edicoes/ed')){
            let partes = req.url.split('/')
            try {
                const resp = await axios.get('http://localhost:3000/edicoes/' + partes[2])
                let dados = resp.data
                res.writeHead(200, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify(dados))

            } catch (error) {
                res.writeHead(502, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify({
                    erro: "Erro ao contactar o servidor de dados",
                    detalhe: error.message
                }))
            }
        }
        else if(req.url.startsWith('/edicoes?v')){
            let partes = req.url.split('=')
            try {
                const resp = await axios.get('http://localhost:3000/edicoes?vencedor=' + partes[1])
                let dados = resp.data
                // Filtragem
                const resultado = dados.map(e => ({
                    id: e.id,
                    anoEdição: e.anoEdição,
                    vencedor: e.vencedor,
                    organizacao: e.organizacao
                }))
                res.writeHead(200, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify(resultado))

            } catch (error) {
                res.writeHead(502, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify({
                    erro: "Erro ao contactar o servidor de dados",
                    detalhe: error.message
                }))
            }
        }
        else if(req.url.startsWith('/edicoes?o')){
            let partes = req.url.split('=')
            try {
                const resp = await axios.get('http://localhost:3000/edicoes?organizacao=' + partes[1])
                let dados = resp.data
                // Filtragem
                const resultado = dados.map(e => ({
                    id: e.id,
                    anoEdição: e.anoEdição,
                    vencedor: e.vencedor,
                    organizacao: e.organizacao
                }))
                res.writeHead(200, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify(resultado))

            } catch (error) {
                res.writeHead(502, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify({
                    erro: "Erro ao contactar o servidor de dados",
                    detalhe: error.message
                }))
            }
        }
        else if(req.url.startsWith('/interpretes/')){
            let myInterp = decodeURIComponent(req.url.split('/')[2])
            try {
                const resp = await axios.get('http://localhost:3000/edicoes')
                let dados = resp.data
                let participacoes = [], vitorias = []
                for (const ed of dados) {
                    var particip = getParticipacaoByInterp(ed.musicas, ed.anoEdição, myInterp)
                    if(Object.keys(particip).length != 0){
                        participacoes.push(particip)
                        if ("vencedor" in ed && ed.vencedor == particip.país){
                            vitorias.push(particip)
                        }
                    }
                }
                var resultado = {
                    participacoes: participacoes,
                    vitorias: vitorias
                }
                res.writeHead(200, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify(resultado))

            } catch (error) {
                res.writeHead(502, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify({
                    erro: "Erro ao contactar o servidor de dados",
                    detalhe: error.message
                }))
            }
        }
        else if(req.url == '/interpretes'){
            try {
                const resp = await axios.get('http://localhost:3000/edicoes')
                let dados = resp.data
                let lista = []
                for (const ed of dados) {
                    for (const m of ed.musicas) {
                        if ("intÃ©rprete" in m) {
                            if (!lista.includes(m["intÃ©rprete"])) {
                                lista.push(m["intÃ©rprete"]);
                            }
                        }
                    }
                }
                lista.sort()
                res.writeHead(200, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify(lista))

            } catch (error) {
                res.writeHead(502, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify({
                    erro: "Erro ao contactar o servidor de dados",
                    detalhe: error.message
                }))
            }
        }
        else if(req.url.startsWith('/paises?papel=o')){
            try {
                const resp = await axios.get('http://localhost:3000/edicoes')
                let dados = resp.data
                let dicOrg = {}
                for (const ed of dados) {
                        if ("organizacao" in ed) {
                            if (dicOrg.hasOwnProperty(ed.organizacao)) {
                                dicOrg[ed.organizacao].push(ed.anoEdição)
                            }
                            else{
                                dicOrg[ed.organizacao] = [ed.anoEdição]
                            }
                        }
                }
                // Sorting: Ã© uma distribuiÃ§Ã£o, tem a forma de um dicionÃ¡rio
                var keys = Object.keys(dicOrg),
                    i,
                    len = keys.length;

                keys.sort();
                resultado = {}
                for (i = 0; i < len; i++) {
                    k = keys[i]
                    resultado[keys[i]] = dicOrg[keys[i]]
                }
                res.writeHead(200, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify(resultado))

            } catch (error) {
                res.writeHead(502, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify({
                    erro: "Erro ao contactar o servidor de dados",
                    detalhe: error.message
                }))
            }
        }
        else if(req.url.startsWith('/paises?papel=v')){
            try {
                const resp = await axios.get('http://localhost:3000/edicoes')
                let dados = resp.data
                let dicOrg = {}
                for (const ed of dados) {
                        if ("vencedor" in ed) {
                            if (dicOrg.hasOwnProperty(ed.vencedor)) {
                                dicOrg[ed.vencedor].push(ed.anoEdição)
                            }
                            else{
                                dicOrg[ed.vencedor] = [ed.anoEdição]
                            }
                        }
                }
                // Sorting: Ã© uma distribuiÃ§Ã£o, tem a forma de um dicionÃ¡rio
                var keys = Object.keys(dicOrg),
                    i,
                    len = keys.length;

                keys.sort();
                resultado = {}
                for (i = 0; i < len; i++) {
                    k = keys[i]
                    resultado[keys[i]] = dicOrg[keys[i]]
                }
                res.writeHead(200, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify(resultado))

            } catch (error) {
                res.writeHead(502, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify({
                    erro: "Erro ao contactar o servidor de dados",
                    detalhe: error.message
                }))
            }
        }
        else if(req.url.startsWith('/paises/')){
            let myPaís = decodeURIComponent(req.url.split('/')[2])
            try {
                const resp = await axios.get('http://localhost:3000/edicoes')
                let dados = resp.data
                let participacoes = [], vitorias = [], orgs = []
                for (const ed of dados) {
                    var particip = getParticipacao(ed.musicas, ed.anoEdição, myPaís)
                    if(Object.keys(particip).length != 0){
                        participacoes.push(particip)
                        if ("organizacao" in ed && ed.organizacao == myPaís){
                            orgs.push(particip)
                        }
                        if ("vencedor" in ed && ed.vencedor == myPaís){
                            vitorias.push(particip)
                        }
                    }
                }
                var resultado = {
                    participacoes: participacoes,
                    vitorias: vitorias,
                    organizacoes: orgs
                }
                res.writeHead(200, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify(resultado))

            } catch (error) {
                res.writeHead(502, { 'Content-Type': 'application/json' })
                res.end(JSON.stringify({
                    erro: "Erro ao contactar o servidor de dados",
                    detalhe: error.message
                }))
            }
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

