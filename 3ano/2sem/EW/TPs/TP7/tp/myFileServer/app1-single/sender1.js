const   axios = require('axios'),
        formData = require('form-data'),
        fs = require('fs')

const form = new formData()
const filepath = "../ficheiros-cartoons/coyotte.jpeg"
form.append("myFile", fs.createReadStream(filepath))
form.append("desc", "Uma imagem do coyotte!")

axios.post('http://localhost:18000/ficheiro', form)
    .then(function(resposta) {
        console.log("Ficheiro enviado com sucesso.")
        console.log(JSON.stringify(resposta.data))
    })
    .catch(function(erro) {
        console.log(JSON.stringify(erro))
    })