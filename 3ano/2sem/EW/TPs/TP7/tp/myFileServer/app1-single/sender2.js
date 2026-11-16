const   axios = require('axios'),
        formData = require('form-data'),
        fs = require('fs')

const form = new formData()
var filepath = "../ficheiros-cartoons/coyotte.jpeg"
form.append("myFile", fs.createReadStream(filepath))
form.append("desc", "Uma imagem do coyotte!")

filepath = "../ficheiros-cartoons/duffy-duck.png"
form.append("myFile", fs.createReadStream(filepath))
form.append("desc", "Uma imagem do pato!")

axios.post('http://localhost:18001/ficheiro', form)
    .then(function(resposta) {
        console.log("Ficheiro enviado com sucesso.")
        console.log(JSON.stringify(resposta.data))
    })
    .catch(function(erro) {
        console.log(JSON.stringify(erro))
    })