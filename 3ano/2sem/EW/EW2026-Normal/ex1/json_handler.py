import json

# 1. Carregar o dataset original
try:
    with open('jogos.json', 'r', encoding='utf-8') as f:
        data = json.load(f)
except FileNotFoundError:
    print("Erro: O ficheiro 'jogos.json' não foi encontrado.")
    exit(1)

# 2. Tratar e converter os dados para o padrão do MongoDB
for registo in data:
    if 'id' in registo:
        # Define o _id do MongoDB com o valor do id original
        registo['_id'] = registo['id']
        # Remove o campo 'id' antigo para não duplicar informação
        del registo['id']

# 3. Guardar o novo dataset pronto para importação
with open('jogos_ready.json', 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=4)

print("Conversão concluída com sucesso!")