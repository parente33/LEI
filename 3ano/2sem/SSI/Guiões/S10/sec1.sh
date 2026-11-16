#!/bin/bash

# Secção das Tarefas Base
# Exercício 1 (Q1)
# Essas linhas extraem parâmetros de segurança gerados pela aplicação:
# __elgg_ts (timestamp); __elgg_token (token CSRF)
# São necessários para que o pedido forjado seja válido aos olhos do servidor. O Elgg usa estes valores para prevenir ataques de CSRF (Cross-Site Request Forgery); sem eles, o servidor rejeita o pedido, mesmo que venha de uma sessão autenticada.

# Exercício 2 (Q2)
# Sim, é possível. O modo de editor afeta apenas a forma como o input é apresentado; o ataque não depende da troca para o modo de input bruto, se a aplicação falhar em "higienizar" corretamente html/js perigoso, o XSS pode ainda ser explorado.

# Exercício 3 (Q3)
# Enquadra-se em Stored XSS. O payload js é injetado e persistido no servidor; quando outro utilizador visita o perfil, o servidor devolve esse conteúdo armazenado sem sanitização adequada; o script executa automaticamente no browser da vítima (aqui o importante é ele estar guardado na aplicação).
