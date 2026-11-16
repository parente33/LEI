"""
Estratégia (ao consumir os tokens um a um):
    Se o token for INT, retornar 1
    Se o token for x:
        Consumir x
        Calcular recursivamente esquerda
        Calcular recursivamente direita
        Retornar 1 + max(esq, dir)
"""

tokens = open("tpc6_5.lst").read().split()
pos = 0

def parseTree():
    global pos

    if pos >= len(tokens):
        raise Exception("Unexpected end of input")

    token = tokens[pos]
    pos += 1

    if token.isdigit():
        return 1
    elif token == 'x':
        left = parseTree()
        right = parseTree()
        return 1 + max(left, right)
 
depth = parseTree()
print(depth)
