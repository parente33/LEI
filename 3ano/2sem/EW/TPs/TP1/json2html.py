import json
import os
import shutil

def open_json(filename):
    with open(filename, encoding="utf-8") as f:
        data = json.load(f)
    return data

def mk_dir(relative_path):
    if not os.path.exists(relative_path):
        os.mkdir(relative_path)
    else:
        shutil.rmtree(relative_path)
        os.mkdir(relative_path)

def new_file(filename, content):
    with open(filename, "w", encoding="utf-8") as f:
        f.write(content)

# ------------------------------ PÁGINA PRINCIPAL ------------------------------ #

mapa = open_json("dataset.json")
lista_links = ""
cidades = {}

cidades_ordenadas = mapa["cidades"]

cidades_ordenadas.sort(key = lambda c: c["nome"])

for cidade in cidades_ordenadas:
    cidades[cidade["id"]] = cidade["nome"]
    lista_links += f'''
                        <li><a href="{cidade["id"]}.html">{cidade["nome"]}</a></li>
                    '''

html = f'''
<html>
    <head>
        <title>Lista de Cidades Portuguesas</title>
        <meta charset="utf-8"/>
    </head>
    <body>
        <h3>Lista de cidades portuguesas</h3>
        <ul>
            {lista_links}
        </ul>
    </body>
</html>
'''

mk_dir("output")
new_file("./output/index.html", html)


# ------------------------------ PÁGINAS INDIVIDUAIS ------------------------------ #
ligacoes = mapa["ligações"]

for cidade in mapa["cidades"]:
    origens = list(filter(lambda l: l["origem"] == cidade["id"], ligacoes))
    destinos = list(filter(lambda l: l["destino"] == cidade["id"], ligacoes))
    links = ""
    for o in origens:
        links += f'''
                    <li><a href="{o["destino"]}.html">{cidades[o["destino"]]}</a></li>
                '''
    for d in destinos:
        links += f'''
                    <li><a href="{d["origem"]}.html">{cidades[d["origem"]]}</a></li>
                '''

    html = f'''
<html>
    <head>
        <title>{cidade["nome"]} </title>
        <meta charset="utf-8"/>
    </head>
    <body>
        <h3>{cidade["nome"]}</h3>
            <table border="1">
                <tr><td>id</td><td>{cidade["id"]}</td></tr>
                <tr><td>nome</td><td>{cidade["nome"]}</td></tr>
                <tr><td>população</td><td>{cidade["população"]}</td></tr>
                <tr><td>descrição</td><td>{cidade["descrição"]}</td></tr>
                <tr><td>distrito</td><td>{cidade["distrito"]}</td></tr>
            </table>
            <hr/>
            <h3>Liçações para outras cidades</h3>
            <ul>
                {links}
            </ul>
            <hr/>
            <address>
                <a href="index.html">Voltar ao índice</a>
            </address>
    </body>
</html>
'''

    new_file(f"./output/{cidade["id"]}.html", html)