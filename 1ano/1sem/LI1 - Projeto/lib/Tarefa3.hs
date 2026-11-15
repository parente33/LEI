{-# OPTIONS_GHC -Wno-unrecognised-pragmas #-}
{-# HLINT ignore "Avoid lambda" #-}
{-# HLINT ignore "Avoid lambda using `infix`" #-}
{-# HLINT ignore "Eta reduce" #-}
{-# HLINT ignore "Use zipWith" #-}

{-|
Module      : Tarefa3
Description : Movimenta personagens no jogo
Copyright   : Daniel Gonçalves Parente <a107363@alunos.uminho.pt>
              Tiago Silva Martins <a106938@alunos.uminho.pt>

Módulo para a realização da Tarefa 3 de LI1 em 2023/24.
-}
module Tarefa3 where

import LI12324
import Tarefa1


-- função geral da tarefa
movimenta :: Semente -> Tempo -> Jogo -> Jogo
movimenta s t jogo@(Jogo mapa inimigos colecionaveis jogador) = Jogo (colisaoComAlcapao jogo) inimigos colecionaveis jogador


atualizaPosicaoMario :: Float -> Jogo -> Jogo
atualizaPosicaoMario dt e@Jogo {mapa = mapaAtual, jogador = mario} =
  let marioCaido = mario
      (vx, vy) = velocidade marioCaido
      (x, y) = posicao mario
      dir = direcao mario
      novaPosicao = (x + vx * realToFrac dt, y + vy * realToFrac dt)
      novaPosicao1 = (x + vx * realToFrac dt, 0.5 + realToFrac (truncate (y + vy * realToFrac dt)))
      novoMario = personagemCai dir mapaAtual (mario {posicao = novaPosicao, emEscada = False})
      novoMario1 = personagemCai dir mapaAtual (mario {posicao = novaPosicao1, emEscada = False})
      novoMarioEscada = personagemCai dir mapaAtual (mario {posicao = novaPosicao, emEscada = True})
      novoMarioEscada1 = personagemCai dir mapaAtual (mario {posicao = novaPosicao, emEscada = True})
      indices = devolveIndices $ filtraEscada $ indicesBlocos mapaAtual
      
  in
         if colisaoComBlocos mapaAtual mario && vy == 0 then
             if colisaoComBlocos' (blocosSobreEscada mapaAtual ++ indices) mario then
                e {jogador = novoMarioEscada1}
          else
             e {jogador = novoMario1}
         else
            if colisaoComBlocos' (blocosSobreEscada mapaAtual ++ indices) mario then
                e {jogador = novoMarioEscada}
          else
             e {jogador = novoMario}
 

gravidadeAtua :: Personagem -> Mapa -> Bool
gravidadeAtua jogador mapa = not (colisaoComBlocos mapa jogador)

gravidadeAtuaE :: Personagem -> Mapa -> Bool
gravidadeAtuaE jogador mapa = not (colisaoComBlocos' (blocosSobreEscada mapa++indices) jogador)
       where indices = devolveIndices $ filtraEscada $ indicesBlocos mapa


personagemCai:: Direcao -> Mapa -> Personagem -> Personagem
personagemCai dir mapa jogador
       |gravidadeAtua jogador mapa && gravidadeAtuaE jogador mapa = jogador  {velocidade = novaVelocidade}
       |otherwise = jogador  {velocidade =(vx, 0)}
      where
    (vx,vy) = velocidade jogador
    (x,y) = posicao jogador
    novaVelocidade = (vx,vy+0.1)
    novajogador1 = jogador  {velocidade =(0, vy)}
    novojogador = jogador  {velocidade =(vx, 0)}
-- termina aqui as funções da gravidade

novosColecionaveis :: Jogo -> [(Colecionavel, Posicao)]
novosColecionaveis jogo@(Jogo _ _ lcolecionaveis _) =
  let jogoComMartelo = colideComMartelo jogo
      jogoComMoeda = colideComMoeda jogoComMartelo
  in filter (naoColidiuComJogador jogoComMoeda) lcolecionaveis
  where
    naoColidiuComJogador :: Jogo -> (Colecionavel, Posicao) -> Bool
    naoColidiuComJogador (Jogo _ _ _ jogador) (_, posicaoColecionavel) =
      not $ interHitboxes (calculaHitbox jogador) (calculaHitboxColecionavel posicaoColecionavel)

    calculaHitboxColecionavel :: Posicao -> Hitbox
    calculaHitboxColecionavel (x, y) = ((x - 1/2, y + 1/2), (x + 1/2, y - 1/2))

novosInimigos :: Semente -> [Personagem] -> [Personagem]
novosInimigos _ [] = []
novosInimigos s inimigosAtuais = zipWith (atualizarInimigo s) aleatorios inimigosAtuais
  where
    aleatorios = geraAleatorios s (length inimigosAtuais)

-- começa aqui a animação dos inimigos
animarInimigos :: Semente -> Int -> Jogo -> Jogo
animarInimigos s numInimigos jogo = jogo {inimigos = novosInimigos}
  where
    aleatorios = geraAleatorios s numInimigos
    inimigosAtuais = inimigos jogo
    novosInimigos = zipWith (atualizarInimigo s) aleatorios inimigosAtuais

atualizarInimigo :: Semente -> Int -> Personagem -> Personagem
atualizarInimigo s numAleatorio inimigo
    | even numAleatorio = moverEsquerda inimigo
    | otherwise = moverDireita inimigo

moverEsquerda :: Personagem -> Personagem
moverEsquerda inimigo = inimigo {posicao = moveX (-1) (posicao inimigo)}

moverDireita :: Personagem -> Personagem
moverDireita inimigo = inimigo {posicao = moveX 1 (posicao inimigo)}

moveX :: Double -> Posicao -> Posicao
moveX delta (x,y) = (x+delta,y)

detetaEscada :: Semente -> Mapa -> Personagem -> Personagem
detetaEscada s mapa inimigo = case blocoEmFrente of
                                  Just Escada -> decidirMovimentoEscada s inimigo
                                  _ -> inimigo
  where
      (x,y) = posicao inimigo
      direcaoAtual = direcao inimigo
      blocoEmFrente = blocoNaFrente mapa (x,y) direcaoAtual

blocoNaFrente :: Mapa -> Posicao -> Direcao -> Maybe Bloco
blocoNaFrente (Mapa _ _ blocos) (x,y) direcaoAtual = case direcaoAtual of
                                                        Norte -> getBloco (x,y-1)
                                                        Sul -> getBloco (x,y+1)
                                                        Este -> getBloco (x+1,y)
                                                        Oeste -> getBloco (x-1,y)
  where
      getBloco (x',y') = if dentroLimites (x',y') blocos
                            then Just (blocos !! round y' !! round x')
                            else Nothing

dentroLimites :: Posicao -> [[a]] -> Bool
dentroLimites (x,y) mapa = let numLinhas = length mapa
                               numColunas = if numLinhas > 0
                                              then length (head mapa)
                                              else 0
                           in round x >= 0 && round x < numColunas && round y >= 0 && round y < numLinhas

decidirMovimentoEscada :: Semente -> Personagem -> Personagem
decidirMovimentoEscada s inimigo = case numAleatorio `mod` 3 of
                                        0 -> subirEscada inimigo
                                        1 -> descerEscada inimigo
                                        _ -> inimigo
    where
        numAleatorio = head $ geraAleatorios s 1

subirEscada :: Personagem -> Personagem
subirEscada inimigo = inimigo {posicao = moveY (-1) (posicao inimigo)}

descerEscada :: Personagem -> Personagem
descerEscada inimigo = inimigo {posicao = moveY 1 (posicao inimigo)}

moveY :: Double -> Posicao -> Posicao
moveY delta (x,y) = (x,y+delta)
-- termina aqui a animação dos inimigos


-- começa aqui as colisões/atualizações com os colecionaveis
colideComMartelo :: Jogo -> Jogo
colideComMartelo jogo@(Jogo mapa inimigos lcolecionaveis jogador) =
    case lcolecionaveis of
      [] -> jogo
      ((Martelo,(x,y)):t) ->
        if interHitboxes (calculaHitbox jogador) (calculaHitboxMartelo (x,y))
          then colideComMartelo (Jogo mapa inimigos t jogador {aplicaDano = (True,10)})
          else colideComMartelo (Jogo mapa inimigos lcolecionaveis jogador)
      _ -> jogo
  where
        calculaHitboxMartelo :: Posicao -> Hitbox
        calculaHitboxMartelo (x,y) = ((x-1/2,y+1/2),(x+1/2,y-1/2))

colideComMoeda :: Jogo -> Jogo
colideComMoeda jogo@(Jogo mapa inimigos lcolecionaveis jogador) =
      case lcolecionaveis of
        [] -> Jogo mapa inimigos lcolecionaveis jogador
        ((Moeda,(x,y)):t) ->
            if interHitboxes (calculaHitbox jogador) (calculaHitboxMoeda (x,y))
              then colideComMoeda (Jogo mapa inimigos t jogador {pontos = pontos jogador +10})
              else colideComMoeda (Jogo mapa inimigos lcolecionaveis jogador)
  where
      calculaHitboxMoeda :: Posicao -> Hitbox
      calculaHitboxMoeda (x,y) = ((x-1/2,y+1/2),(x+1/2,y-1/2))

verificaColisoes :: Jogo -> Jogo
verificaColisoes jogo@(Jogo _ inimigos _ jogador) =
      case aplicaDano jogador of
          (True, _) -> verificaColisoesComMartelo jogo
          _ -> verificaColisoesSemMartelo jogo

verificaColisoesComMartelo :: Jogo -> Jogo
verificaColisoesComMartelo jogo@(Jogo _ inimigos _ jogador) = foldr colideComInimigoComMartelo jogo inimigos

colideComInimigoComMartelo :: Personagem -> Jogo -> Jogo
colideComInimigoComMartelo inimigo jogo@(Jogo mapa inimigos colecionaveis jogador) =
  if interHitboxes (calculaHitboxDano jogador) (calculaHitbox inimigo)
    then Jogo mapa (atualizaInimigoComMartelo inimigo : inimigos) colecionaveis jogador
    else jogo

atualizaInimigoComMartelo :: Personagem -> Personagem
atualizaInimigoComMartelo inimigo = 
  if vida inimigo <= 0
    then inimigo {posicao = (200, 200) }
    else inimigo {vida = vida inimigo -1}

verificaColisoesSemMartelo :: Jogo -> Jogo
verificaColisoesSemMartelo jogo@(Jogo _ inimigos colecionaveis jogador) =
  foldr (colideComInimigoSemMartelo jogador) jogo inimigos

colideComInimigoSemMartelo :: Personagem -> Personagem -> Jogo -> Jogo
colideComInimigoSemMartelo jogador inimigo jogo@(Jogo mapa inimigos colecionaveis _) =
  if interHitboxes (calculaHitbox jogador) (calculaHitbox inimigo)
    then Jogo mapa (atualizaJogadorSemMartelo jogador inimigo : filter (/= inimigo) inimigos) colecionaveis jogador
    else jogo

atualizaJogadorSemMartelo :: Personagem -> Personagem -> Personagem
atualizaJogadorSemMartelo jogador inimigo =
  if vida inimigo <= 0
    then jogador { vida = vida jogador - 1, aplicaDano = (False, 0) }
    else jogador

-- calcula a hitbox de dano do jogador
calculaHitboxDano :: Personagem -> Hitbox
calculaHitboxDano jogador = if aplicaOuNaoDano && tempoRestante > 0
                              then case direcao jogador of
                                      Este -> ((x1+1,y1),(x2+1,y2))
                                      Oeste -> ((x1-1,y1),(x2-1,y2))
                                      Sul -> ((x1,y1+1),(x2,y2+1))
                                      Norte -> ((x1,y1-1),(x2,y2-1))
                              else ((x1,y1),(x2,y2))
  where
      (x,y) = posicao jogador
      (c,l) = tamanho jogador
      (aplicaOuNaoDano,tempoRestante) = aplicaDano jogador
      ((x1,y1),(x2,y2)) = calculaHitbox jogador

colisaoComAlcapao :: Jogo -> Mapa
colisaoComAlcapao jogo@(Jogo mapa@(Mapa pos jogadorDir matriz) i c jogador)
  | colisaoComBlocos mapa jogador && any (verificaColisaoAlcapao (calculaHitbox jogador)) indicesBlocosAlcapao =
    Mapa pos jogadorDir (atualizaMatrizAlcapao matriz indiceAlcapao)
  | otherwise = Mapa pos jogadorDir matriz
  where
    indicesBlocosAlcapao = devolveIndices $ filtraAlcapao $ indicesBlocos mapa

    -- filtra apenas os blocos com alçapões
    filtraAlcapao :: [((Int,Int),Bloco)] -> [((Int,Int),Bloco)]
    filtraAlcapao = filter (\(_,bloco) -> bloco == Alcapao)

    -- deteta colisões dos "pés" do jogador com os blocos (equivalente à função verificaColisaoS)
    verificaColisaoAlcapao :: Hitbox -> (Int,Int) -> Bool
    verificaColisaoAlcapao hitbox@((x1,y1),(x2,y2)) (i,j) =
      y1>=y3 && y1<(y3+0.5) && x1>(x3-1) && x1<(x3+1)
      where
        (x3,y3) = converteIndiceParaPosicao (i,j)

    -- retorna o par do índice do bloco do alçapão que foi pisado
    indiceAlcapao :: (Int,Int)
    indiceAlcapao = head $ filter (\indice -> verificaColisaoAlcapao (calculaHitbox jogador) indice) indicesBlocosAlcapao

    -- atualiza o índice do bloco atualizado na matriz dos blocos do mapa
    atualizaMatrizAlcapao :: [[Bloco]] -> (Int,Int) -> [[Bloco]]
    atualizaMatrizAlcapao mat (i, j) = map (atualizaLinhaAlcapao (i, j)) $ zip [0..] mat

    -- atualiza o índice do bloco na linha da matriz dos blocos do mapa
    atualizaLinhaAlcapao :: (Int,Int) -> (Int,[Bloco]) -> [Bloco]
    atualizaLinhaAlcapao (i, j) (k, linha) = map (atualizaBlocoAlcapao (i, j) k) $ zip [0..] linha

    -- recebe o índice do bloco que precisa de ser atualizado (e fá-lo, se necessário)
    atualizaBlocoAlcapao :: (Int,Int) -> Int -> (Int,Bloco) -> Bloco
    atualizaBlocoAlcapao (i,j) k (l,bloco) =
                if (k,l) == (i,j)
                  then Vazio
                  else bloco

colideComEstrela :: Jogo -> Bool -- é preciso alterar isto e por a funcionar
colideComEstrela jogo@(Jogo mapa@(Mapa(pos,dir) (x,y) matriz) inimigos colecionaveis jogador) = interHitboxes (calculaHitbox jogador) (calculaHitboxEstrela (x,y))
 where
     calculaHitboxEstrela :: Posicao -> Hitbox
     calculaHitboxEstrela (x,y) = ((x-1/2,y+1/2),(x+1/2,y-1/2))

ficasemvidas :: Jogo -> Bool
ficasemvidas e@Jogo {mapa = mapaAtual, jogador = mario}
         |vida mario == 0 = True
         |otherwise = False

-- termina aqui a função do pisar o alçapão

{-comentário para função que define o tempo restante do martelo

A variável tempo é dada pelo play do gloss que dirá quanto tempo passou desde a última vez que a função foi chamada. 
Ou seja, no caso do martelo o que tens de fazer é tirar ao tempo que ainda tens no martelo esse valor que recebes. 
Começas com 10 segundos, e se o tempo for, por exemplo, 0.5, então passas a ter 9.5s.
Ou seja, na função movimenta, a variável tempo vai ser sempre "t" (por exemplo) e essa variavel apenas vai ser definida quando utilizarmos o play do gloss.
Ou melhor, , se a funçao for chamada 60 vezes por segundo, iremos ter que tirar 1/60 s ao tempo do martelo. É inversamente proporcional ao framwrate. 
Quanto maior o frame rate mais fluido é o jogo e mais pesado para o computador. 
Idealmente queremos a maior fluidez sem tornar pesado (ou seja, não fique lento o computador). 
Provavelmente uma framerate de 60 parece-me bem.  Se for menos um pouco também deverá ser boa.
-}

adolf :: Personagem
adolf = Personagem (0,0) Jogador (11.5,9.5) Este (1,1) False False 5 0 (False,0)


stalin :: Personagem
stalin = Personagem (0,0) Fantasma (10.5,9.5) Este (1,1) False True 1 0 (False,0)


mapa3 :: Mapa
mapa3 = Mapa ((2.5,2.5),Oeste) (0.5,2.5) [[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                          ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                          ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                          ,[Vazio,Vazio,Vazio,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Vazio,Vazio,Vazio,Vazio,Vazio]
                                          ,[Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                          ,[Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                          ,[Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                          ,[Vazio,Plataforma,Alcapao,Plataforma,Alcapao,Plataforma,Plataforma,Plataforma,Vazio,Plataforma,Vazio,Vazio,Vazio]
                                          ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio]
                                          ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio]
                                          ,[Vazio,Plataforma,Plataforma,Plataforma,Alcapao,Alcapao,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma]
                                          ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                          ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                          ,[Vazio,Vazio,Vazio,Vazio,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Vazio,Vazio,Vazio,Vazio]
                                          ,[Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                          ,[Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                          ,[Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma]]