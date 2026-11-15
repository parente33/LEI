{-|
Module      : Tarefa1
Description : Verifica colisões
Copyright   : Daniel Gonçalves Parente <a107363@alunos.uminho.pt>
              Tiago Silva Martins <a106938@alunos.uminho.pt>

Módulo para a realização da Tarefa 1 de LI1 em 2023/24.
-}
module Tarefa1 where

import LI12324


-- calcula colisões tanto com os limites do mapa, tanto com blocos do tipo Plataforma
colisoesParede :: Mapa -> Personagem -> Bool
colisoesParede mapa@(Mapa _ _ lblocos) jogador = colideComLimites mapa (calculaHitbox jogador) || colisaoComBlocos mapa jogador

-- verifica todos os casos em que a hitbox do jogador interceta, de alguma forma, os limites do mapa 
--(incluindo casos em que o jogador se encontra fora do mapa, para efeitos simplificadores)
colideComLimites :: Mapa -> Hitbox -> Bool 
colideComLimites (Mapa _ _ lblocos) hitbox@((x1,y1),(x2,y2)) = 
  y1>=0 && y1<=1 && x1>=(-1) && x1<=1 -- inclui o limite esquerdo/superior 
  || y1>=1 && y1<=fromIntegral (length lblocos) && x1>=(-1) && x1<=0  -- inclui o limite esquerdo
  || y1>=fromIntegral (length lblocos) && y1<=fromIntegral (length lblocos)+1 && x1>=fromIntegral (length (head lblocos))-1 && x1<=fromIntegral (length (head lblocos))+1 --inclui o limite infeiror/direito 
  || y1>=fromIntegral (length lblocos) && y1<=fromIntegral (length lblocos)+1 && x1>=(-1) && x1<=fromIntegral (length (head lblocos))-1  -- inclui o limite inferior  
  || y1>=(-1) && y1<=1 && x1>=1 && x1<=fromIntegral (length (head lblocos))+1 -- inclui o limite superior 
  || y1>=1 && y1<=fromIntegral (length lblocos) && x1>=fromIntegral (length (head lblocos))-1 && x1<=fromIntegral (length (head lblocos))+1 --inclui o limite direito

-- devolve a hitbox do personagem na posição em que se encontra, tendo também em conta o seu tamanho
calculaHitbox :: Personagem -> Hitbox
calculaHitbox jogador =
        let (x,y) = posicao jogador
            (c,l) = tamanho jogador
            x1 = x-c/2
            y1 = y+l/2
            x2 = x+c/2
            y2 = y-l/2
        in ((x1,y1),(x2,y2))

-- devolve uma lista de tuplos onde cada um é composto por um par de coordenadas, e o bloco que nessas coordenadas se encontra
indicesBlocos :: Mapa -> [((Int, Int), Bloco)]
indicesBlocos (Mapa _ _ matriz) = concataMapaIndices (\i row -> mapaIndices (\j bloco -> ((i, j), bloco)) row) matriz -- para cada linha da matriz, mapWIth
  where
  -- recebe uma função f e uma lista (cada "row" da matriz) e mapeia cada elemento da lista com o indice que lhe corresponde
        mapaIndices :: (Int -> a -> b) -> [a] -> [b] 
        mapaIndices f = zipWith f [0..]                         


  -- recebe o índice do bloco da matriz (Int), um valor do tipo Bloco, e associa a cada indice o bloco que lhe corresponde, concateado todas as listas singulares
        concataMapaIndices :: (Int -> a -> [b]) -> [a] -> [b] 
        concataMapaIndices f = concat . zipWith f [0..]                         


-- filtra apenas os blocos com os tipos Plataforma e Alcapao, com os indíces em que se apresentam no mapa
filtraPlataformaAlcapao :: [((Int,Int),Bloco)] -> [((Int,Int),Bloco)]
filtraPlataformaAlcapao = filter (\(_, bloco) -> bloco == Plataforma || bloco == Alcapao)

-- filtra apenas os blocos com os tipos Escada, com os indíces em que se apresentam no mapa
filtraEscada :: [((Int,Int),Bloco)] -> [((Int,Int),Bloco)]
filtraEscada = filter (\(_,bloco) -> bloco == Escada)

-- filtra apenas blocos do tipo Plataforma, com os respetivos índices
filtraPlataforma :: [((Int,Int),Bloco)] -> [((Int,Int),Bloco)]
filtraPlataforma = filter (\(_,bloco) -> bloco == Plataforma)

-- devolve apenas os índices dos blocos que compõem o mapa
devolveIndices :: [((Int,Int),Bloco)] -> [(Int,Int)]
devolveIndices resultados = map (\((a,b),_) -> (a,b)) resultados

-- converte os índices obtidos em "devolveIndices" para a posição do canto superior esquerdo do bloco
converteIndiceParaPosicao :: (Int,Int) -> Posicao
converteIndiceParaPosicao (i,j) = (fromIntegral j,fromIntegral i)

-- converte posições do mapa para o bloco que lhes corresponde na matriz dos blocos
convertePosicaoParaIndice :: Posicao -> (Int,Int)
convertePosicaoParaIndice (x,y) = (floor y,floor x)

-- função geral, que verifica todas as possíveis colisões com blocos
colisaoComBlocos :: Mapa -> Personagem -> Bool
colisaoComBlocos mapa jogador =
  any (verificaColisao (calculaHitbox jogador)) indicesBlocosFiltrados || any (verificaColisao' (calculaHitbox jogador)) indicesBlocosFiltrados ||
  verificaColisaoN' (calculaHitbox jogador) indicesBlocosFiltrados ||  any (verificaColisaoN (calculaHitbox jogador)) indicesBlocosFiltrados || 
  verificaColisaoS' (calculaHitbox jogador) indicesBlocosFiltrados ||  any (verificaColisaoS (calculaHitbox jogador)) indicesBlocosFiltrados ||
  verificaColisaoE' (calculaHitbox jogador) indicesBlocosFiltrados ||  any (verificaColisaoE (calculaHitbox jogador)) indicesBlocosFiltrados || 
  verificaColisaoW' (calculaHitbox jogador) indicesBlocosFiltrados ||  any (verificaColisaoW (calculaHitbox jogador)) indicesBlocosFiltrados
 where
    indicesBlocosFiltrados = devolveIndices $ filtraPlataformaAlcapao $ indicesBlocos mapa

-- idêntica à de cima, mas recebe uma lista de índices
colisaoComBlocos' :: [(Int,Int)]  -> Personagem -> Bool
colisaoComBlocos' mapa jogador =
  any (verificaColisao (calculaHitbox jogador)) mapa || any (verificaColisao' (calculaHitbox jogador)) mapa ||
  verificaColisaoN' (calculaHitbox jogador) mapa ||  any (verificaColisaoN (calculaHitbox jogador)) mapa || 
  verificaColisaoS' (calculaHitbox jogador) mapa ||  any (verificaColisaoS (calculaHitbox jogador)) mapa ||
  verificaColisaoE' (calculaHitbox jogador) mapa ||  any (verificaColisaoE (calculaHitbox jogador)) mapa || 
  verificaColisaoW' (calculaHitbox jogador) mapa ||  any (verificaColisaoW (calculaHitbox jogador)) mapa

-- verica colisões com blocos do tipo Escada
colisaoComEscadas :: Mapa -> Personagem -> Bool
colisaoComEscadas mapa jogador =
  any (verificaColisao (calculaHitbox jogador)) indicesBlocosFiltrados || any (verificaColisao' (calculaHitbox jogador)) indicesBlocosFiltrados  
  || any (verificaColisaoN (calculaHitbox jogador)) indicesBlocosFiltrados || 
   any (verificaColisaoS (calculaHitbox jogador)) indicesBlocosFiltrados ||
   any (verificaColisaoE (calculaHitbox jogador)) indicesBlocosFiltrados || 
    any (verificaColisaoW (calculaHitbox jogador)) indicesBlocosFiltrados
 where
    indicesBlocosFiltrados = devolveIndices $ filtraEscada $ indicesBlocos mapa

-- função "mãe" das colisões pela esquerda da personagem
colisaoComW :: Mapa -> Personagem -> Bool
colisaoComW mapa jogador =
  verificaColisaoW' (calculaHitbox jogador) indicesBlocosFiltrados ||  any (verificaColisaoW (calculaHitbox jogador)) indicesBlocosFiltrados
 where
    indicesBlocosFiltrados = devolveIndices $ filtraPlataformaAlcapao $ indicesBlocos mapa

-- função "mãe" das colisões pela direita da personagem
colisaoComE :: Mapa -> Personagem -> Bool
colisaoComE mapa jogador =
  verificaColisaoE' (calculaHitbox jogador) indicesBlocosFiltrados ||  any (verificaColisaoE (calculaHitbox jogador)) indicesBlocosFiltrados
 where
    indicesBlocosFiltrados = devolveIndices $ filtraPlataformaAlcapao $ indicesBlocos mapa

-- função "mãe" das colisões por cima da personagem
colisaoComN :: Mapa -> Personagem -> Bool
colisaoComN mapa jogador =
  verificaColisaoN' (calculaHitbox jogador) indicesBlocosFiltrados ||  any (verificaColisaoN (calculaHitbox jogador)) indicesBlocosFiltrados
 where
    indicesBlocosFiltrados = devolveIndices $ filtraPlataformaAlcapao $ indicesBlocos mapa

-- função "mãe" das colisões pelos pés da personagem
colisaoComS :: Mapa -> Personagem -> Bool
colisaoComS mapa jogador =
  verificaColisaoS' (calculaHitbox jogador) indicesBlocosFiltrados ||  any (verificaColisaoS (calculaHitbox jogador)) indicesBlocosFiltrados
 where
    indicesBlocosFiltrados = devolveIndices $ filtraPlataformaAlcapao $ indicesBlocos mapa

-- calcula a hitbox da parte de cima da personagem
calculaHitboxCabeca :: Personagem -> Hitbox
calculaHitboxCabeca jogador =
        let (x,y) = posicao jogador
            (c,l) = tamanho jogador
            x1 = x-c/2
            y1 = y+l/2
            x2 = x+c/2
            y2 = (y+l/2) - 0.1
        in ((x1,y1),(x2,y2))

-- calcula a hitbox da parte de baixo da personagem
calculaHitboxPes :: Personagem -> Hitbox
calculaHitboxPes jogador =
        let (x,y) = posicao jogador
            (c,l) = tamanho jogador
            x1 = x-c/2
            y1 = (y-l/2) + 0.01
            x2 = x+c/2
            y2 = y-l/2
        in ((x1,y1),(x2,y2))

-- verifica todos os casos possíveis de colisão com um bloco
verificaColisao :: Hitbox -> (Int,Int) -> Bool                                                                                      
verificaColisao hitbox@((x1,y1),(x2,y2)) (i,j) =
    y1==y3 && x1>=(x3-1) && x1<=(x3+1) -- colisões "por baixo"
    || y2==(y3+1) && x2>=x3 && x2<=(x3+1) -- colisões "por cima"
    || y1>=y3 && y1<=(y3+1) && x1>=(x3-0.9) && x1<=x3 -- colisões "pela direita"
    || y1<=(y3+1.5) && y1>=y3 && x1<=(x3+1) && x1>=x3 -- colisões "pela esquerda"
  where
    (x3,y3) = converteIndiceParaPosicao (i,j)

-- função auxiliar (igual à verificaColisão) para separar por casos as colisões
verificaColisao' :: Hitbox -> (Int,Int) -> Bool
verificaColisao' hitbox@((x1,y1),(x2,y2)) (i,j) =
  verificaColisaoN || verificaColisaoS || verificaColisaoE || verificaColisaoW
  where
    verificaColisaoN = y2==(y3+1) && x1>(x3-1) && x1<(x3+2)
    verificaColisaoS = y1==y3 && x1>(x3-1) && x1<(x3+1)
    verificaColisaoE = x1==(x3+1) && y1>y3 && y1<(y3+2)
    verificaColisaoW = x1==x3 && y1>y3 && y1<(y3+2)
    (x3,y3) = converteIndiceParaPosicao (i,j)

-- verifica colisões "por cima" da personagem
verificaColisaoN :: Hitbox -> (Int,Int) -> Bool
verificaColisaoN hitbox@((x1,y1),(x2,y2)) (i,j) = y2==(y3+1) && x1>(x3-1) && x1<(x3+2)
  where (x3,y3) = converteIndiceParaPosicao (i,j)

-- igual à de cima, mas recebe uma lista de índices
verificaColisaoN' :: Hitbox -> [(Int,Int)] -> Bool
verificaColisaoN' hitbox coordenadas = any (\c -> verificaColisaoN hitbox c) coordenadas

-- verifica colisões "por baixo" da personagem
verificaColisaoS :: Hitbox -> (Int,Int) -> Bool
verificaColisaoS hitbox@((x1,y1),(x2,y2)) (i,j) = y1==y3 && x1>(x3-0.75) && x1<(x3+1)
  where (x3,y3) = converteIndiceParaPosicao (i,j)

-- igual à de cima, mas recebe uma lista de índices
verificaColisaoS' :: Hitbox -> [(Int,Int)] -> Bool
verificaColisaoS' hitbox coordenadas = any (\c -> verificaColisaoS hitbox c) coordenadas

-- verifica colisões "pela direita" da personagem
verificaColisaoE :: Hitbox -> (Int,Int) -> Bool
verificaColisaoE hitbox@((x1,y1),(x2,y2)) (i,j) = x1==(x3+1) && y1>y3 && y1<(y3+2)
  where (x3,y3) = converteIndiceParaPosicao (i,j)

-- igual à de cima, mas recebe uma lista de índices
verificaColisaoE' :: Hitbox -> [(Int,Int)] -> Bool
verificaColisaoE' hitbox coordenadas = any (\c -> verificaColisaoE hitbox c) coordenadas

-- verifica colisões "pela esquerda" da personagem
verificaColisaoW :: Hitbox -> (Int,Int) -> Bool
verificaColisaoW hitbox@((x1,y1),(x2,y2)) (i,j) = x1==(x3+1) && y1>y3 && y1<(y3+2)
  where (x3,y3) = converteIndiceParaPosicao (i,j)

-- igual à de cima, mas recebe uma lista de índices
verificaColisaoW' :: Hitbox -> [(Int,Int)] -> Bool
verificaColisaoW' hitbox coordenadas = any (\c -> verificaColisaoW hitbox c) coordenadas

-- função geral que vê colisões entre a personagem e a lista de inimigos
colisoesPersonagens :: Personagem -> Personagem -> Bool
colisoesPersonagens jogador inimigo = interHitboxes hitboxJogador hitboxInimigo
  where hitboxJogador = calculaHitbox jogador
        hitboxInimigo = calculaHitbox inimigo

-- verifica se duas hitboxes colidem, isto é, se em algum momento se sorepõem
interHitboxes :: Hitbox -> Hitbox -> Bool
interHitboxes hitbox1 hitbox2 = x3>=(x1-1) && x3<=x2 && y4>=(y2-1) && y4<=(y1+1)
  where
    ((x1,y1),(x2,y2)) = hitbox1
    ((x3,y3),(x4,y4)) = hitbox2

-- verifica se uma personagem está dentro de um bloco, para ser usada na Tarefa2
dentroDoBloco :: Hitbox -> (Int,Int) -> Bool
dentroDoBloco hitbox@((x1,y1),(x2,y2)) (i,j) = y2>y3 && y2<(y3+1) && x1>(x3-1) && x1<(x3+1)
  where (x3,y3) = converteIndiceParaPosicao (i,j)


-- verifica se uma posição está dentro de uma hitbox
dentroDaHitbox ((x1,y1),(x2,y2)) (x,y) = x>=x1 && x<=x2 && y>=y1 && y<=y2

-- vê quais são as plataformas que se encontram acima de uma escada
blocosSobreEscada :: Mapa -> [(Int,Int)]
blocosSobreEscada map = blocosSobreEscada'  indicesBlocosFiltrados indicesBlocosFiltrados1
  where  indicesBlocosFiltrados = devolveIndices $ filtraPlataformaAlcapao $ indicesBlocos map
         indicesBlocosFiltrados1 = devolveIndices $ filtraEscada $ indicesBlocos map

-- função auxiliar
blocosSobreEscada' :: [(Int,Int)] -> [(Int,Int)] -> [(Int,Int)]
blocosSobreEscada' _ [] = []
blocosSobreEscada' [] _ = []
blocosSobreEscada' ((p,r):ps) e = blocosSobreEscada'' (p,r) e ++ blocosSobreEscada' ps e

-- função auxiliar
blocosSobreEscada'' :: (Int,Int) -> [(Int,Int)] -> [(Int,Int)]
blocosSobreEscada'' _ [] = []
blocosSobreEscada'' (p,r) e
               |(p+1,r) `elem` e = [(p,r)]
               |otherwise = []