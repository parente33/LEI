const axios = require('axios')
const http = require('http')

/* ----------------------------------- UTILS ---------------------------------------------- */

// --- FunÃ§Ãµes utilitÃ¡rias
// ----- para o servidor aplicacional da EurovisÃ£o

function pagina(titulo, corpo){
    return `
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="utf-8"/>
        <title>${titulo}</title>
        <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css"/>
    </head>
    <body class="w3-light-grey">

        <div class="w3-container w3-teal">
            <h1>${titulo}</h1>
        </div>

        <div class="w3-container w3-margin-top">
            ${corpo}
        </div>

    </body>
    </html>
    `
}

function link(href, texto){
    return `<a href="${href}">${texto}</a>`
}

function card(titulo, conteudo){
    return `
    <div class="w3-card-4 w3-white w3-margin-bottom">
        <header class="w3-container w3-teal">
            <h3>${titulo}</h3>
        </header>
        <div class="w3-container w3-padding">
            ${conteudo}
        </div>
    </div>
    `
}

function lista(items){
    if(items.length === 0)
        return `<p><i>Sem registos.</i></p>`

    return `
      <ul class="w3-ul w3-hoverable">
        ${items.map(i => `<li>${i}</li>`).join("")}
      </ul>
    `
}

function botaoVoltar(){
    return `<a class="w3-button w3-teal w3-margin-top" href="/">Voltar</a>`
}

async function getEdicoes(){
    const resp = await axios.get("http://localhost:25000/edicoes")
    return resp.data
}

async function getEdicao(id){
    const resp = await axios.get("http://localhost:25000/edicoes/" + id)
    return resp.data
}

async function getInterp(i){
    const resp = await axios.get("http://localhost:25000/interpretes/" + i)
    return resp.data
}

async function getPais(p){
    const resp = await axios.get("http://localhost:25000/paises/" + p)
    return resp.data
}

/* ----------------------------------------------------------------------------------- */

var myServer = http.createServer(async function (req, res) {
    var d = new Date().toISOString().substring(0, 16)
    console.log(req.method + " " + req.url + " " + d)

    switch (req.method) {
        case "GET":
            if (req.url === "/") {
                try {
                    var edicoes = await getEdicoes()
                    var linhas = edicoes.map(e => `
                            <tr>
                                <td>${e.anoEdição}</td>
                                <td>${link("/paises/" + encodeURIComponent(e.organizacao), e.organizacao)}</td>
                                <td>${link("/paises/" + encodeURIComponent(e.vencedor), e.vencedor)}</td>
                                <td>${link("/edicoes/" + e.id, "Ver edição")}</td>
                            </tr>
                        `).join("")

                    var corpo = card("Lista de edições", `
                            <table class="w3-table w3-striped w3-bordered w3-hoverable">
                                <tr class="w3-light-grey">
                                    <th>Ano</th>
                                    <th>Organizador</th>
                                    <th>Vencedor</th>
                                    <th>Página da edição</th>
                                </tr>
                                ${linhas}
                            </table>
                        `)

                    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' })
                    res.end(pagina("Edições da Eurovision", corpo))
                }
                catch (erro){
                    res.writeHead(500, { 'Content-Type': 'text/html; charset=utf-8' })
                    res.end(`<p>Erro ao carregar edições: ${erro}</p>`)
                }
            } // Página da edição
            else if (req.url.startsWith('/edicoes/ed')) {
                var id = req.url.split('/')[2]
                try {
                    var edicao = await getEdicao(id)
                    var musicas = edicao.musicas.map(m => `
                            ${m.título} -
                            ${link("/interpretes/" + encodeURIComponent(m.intérprete), m.intérprete)}
                            (${link("/paises/" + encodeURIComponent(m.país), m.país)})
                        `)

                    var corpo = `
                        ${card("Informação geral", `
                                <p><b>Ano: </b> ${edicao.anoEdição}</p>
                                <p><b>Organização: </b> ${link("/paises/" + encodeURIComponent(edicao.organizacao), edicao.organizacao)}</p>
                                <p><b>Vencedor: </b> ${edicao.vencedor}</p>
                            `)}

                        ${card("Músicas a concurso", lista(musicas))}

                        ${botaoVoltar}
                    `

                    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' })
                    res.end(pagina("Edição" + edicao.anoEdição, corpo))
                }
                catch (erro) {
                    res.writeHead(500, { 'Content-Type': 'text/html; charset=utf-8' })
                    res.end(`<p>Erro ao carregar a edição ${id}: ${erro}</p>`)
                }
            } // Página do país
            else if (req.url.startsWith(/paises/)) {
                var pais = decodeURIComponent(req.url.split('/')[2])
                try {
                    var historicoPais = await getPais(pais)
                    var participacoes = historicoPais.participacoes.map(p => `
                            ${p.ano}: "${p.tit}" -
                            ${link("/interpretes/" + encodeURIComponent(p.inter), p.inter)}
                        `)

                    var organizacoes = historicoPais.organizacoes.map(p => `
                            ${p.ano}: "${p.tit}" -
                            ${link("/interpretes/" + encodeURIComponent(p.inter), p.inter)}
                        `)

                    var vitorias = historicoPais.vitorias.map(p => `
                            ${p.ano}: "${p.tit}" -
                            ${link("/interpretes/" + encodeURIComponent(p.inter), p.inter)}
                        `)

                    var corpo = `
                        ${card("Participações", lista(participacoes))}
                        ${card("Organizações", lista(organizacoes))}
                        ${card("Vitórias", lista(vitorias))}
                        ${botaoVoltar}
                    `

                    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' })
                    res.end(pagina("País " + pais, corpo))
                }
                catch (erro) {
                    res.writeHead(500, { 'Content-Type': 'text/html; charset=utf-8' })
                    res.end(`<p>Erro ao carregar o país ${pais}: ${erro}</p>`)
                }
            }
            else {
                res.writeHead(405, { 'Content-Type': 'text/html; charset=utf-8' })
                res.end(`<p>Método não suportado: ${req.method}</p>`)
            }
            break

        default:
            res.writeHead(405, { 'Content-Type': 'text/html; charset=utf-8' })
            res.end(`<p>Rota não suportada: ${req.url}</p>`)
    }
})

myServer.listen(25001)
console.log("Servidor à escuta na porta 25001...")