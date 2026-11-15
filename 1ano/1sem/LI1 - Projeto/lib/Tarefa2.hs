{-|
Module      : Tarefa2
Description : Valida jogo
Copyright   : Daniel Gonçalves Parente <a107363@alunos.uminho.pt>
              Tiago Silva Martins <a106938@alunos.uminho.pt>

Módulo para a realização da Tarefa 2 de LI1 em 2023/24.
-}
module Tarefa2 where

import LI12324
import Tarefa1

valida :: Jogo -> Bool --verifica que um dado jogo não viola nenhuma das restricoes impostas nas funcoes abaixo
valida (Jogo mapa inimigos colecionaveis jogador) =
    chaoPresente mapa &&
    todosRessaltam inimigos jogador &&
    vidaUnicaFantasmas inimigos &&
    numeroMinimoInimigos inimigos &&
    semColisoesIniciais jogador inimigos &&
    tamanhoMaximoJogador jogador &&
    colisaoComBlocos mapa jogador &&
    verificainimigos mapa inimigos &&
    verificarcolecionaveis mapa colecionaveis


tudoplataforma :: [Bloco] -> Bool -- verifica se todos os blocos de uma linha de blocos sao plataformas
tudoplataforma [] = True
tudoplataforma (x:xs)
      |x==Plataforma = tudoplataforma xs
      |otherwise = False

chaoPresente :: Mapa -> Bool --verifica se o chao é formado por plataformas
chaoPresente (Mapa _ _ lblocos)
        |tudoplataforma (last lblocos) = True
        |otherwise = False

todosRessaltam :: [Personagem] -> Personagem -> Bool --verifica se os inimigos têm a propriedade ressalta ativa e o jogador não
todosRessaltam inimigos jogador = all inimigoRessalta inimigos && jogadorRessalta jogador
    where
        inimigoRessalta p = ressalta p == True
        jogadorRessalta p = ressalta p == False

vidaUnicaFantasmas :: [Personagem] -> Bool --verifica se todos os fantasmas têm apenas uma unica vida
vidaUnicaFantasmas = all saoFantasmasComVidaUnica
    where
        saoFantasmasComVidaUnica p = eFantasma p && temVidaUnica p
        eFantasma p = tipo p == Fantasma
        temVidaUnica p = vida p == 1

numeroMinimoInimigos :: [Personagem] -> Bool --verifica se no mapa tem pelo menos 2 inimigos
numeroMinimoInimigos inimigos = length inimigos >= 2

semColisoesIniciais :: Personagem -> [Personagem] -> Bool --Verifica se nao existem colisoes inicias entro o Jogador e inimigos
semColisoesIniciais _ [] = True
semColisoesIniciais p1 (p:px)
       |not (colisoesPersonagens p1 p) && semColisoesIniciais p1 px= True
       |otherwise = False

validarescadas :: Mapa -> Bool --facilita o uso de indices para validar esacadas
validarescadas (Mapa _ _ []) = True
validarescadas x = escadasEalcapoesValidos (indicesBlocos x)

escadasEalcapoesValidos :: [((Int, Int), Bloco)] -> Bool --verifica se as esacadas nao terminam ou começam em alcapao e que numa das suas extremidades existe uma plataforma
escadasEalcapoesValidos [] = True
escadasEalcapoesValidos (((x, y), block) : xs)
    |block == Escada = verificaalcapaocima xs (x-1,y) && verificaalcapaobaixo xs (x+1,y) &&  escadasEalcapoesValidos xs
    |otherwise = escadasEalcapoesValidos xs

verificaalcapaocima :: [((Int, Int), Bloco)] -> (Int,Int) -> Bool --se andarmos para cima nao encontramos alcapao
verificaalcapaocima (((x, y), block):xs) (x1,y2)
      |block==Alcapao && x==x1 && y2==y = False
      |block==Vazio && naoplataformabaixo ((x+1, y), block) = False
      |otherwise = True

verificaalcapaobaixo :: [((Int, Int), Bloco)] -> (Int,Int) -> Bool --se andarmos para Baixo nao encontramos alcapao
verificaalcapaobaixo [((x, y), block)] (x1,y2)
      |block==Alcapao = False
      |block== Escada = verificaalcapaocima [((x+1 , y), block)] (x1,y2)
      |block==Vazio && naoplataformacima ((x-1, y), block) = False
      |otherwise = True

naoplataformacima :: ((Int, Int), Bloco) -> Bool --Verifica se nao estiste uma plataforma acima das escadas
naoplataformacima ((x, y), block)
         |block==Escada = naoplataformacima ((x-1 , y), block)
         |block==Vazio = True
         |block==Plataforma = False
         |otherwise = True

naoplataformabaixo :: ((Int, Int), Bloco) -> Bool --Verifica se nao existe uma plataforma a baixo das escadas
naoplataformabaixo ((x, y), block)
        |block==Escada = naoplataformabaixo ((x+1, y), block)
        |block==Vazio = True
        |block==Plataforma = False
        |otherwise = True


verificainimigos :: Mapa -> [Personagem] -> Bool --Verifica se os inimigos nao se encontram dentro de uma plataforma ou alcapao
verificainimigos (Mapa _ _ []) _ = True
verificainimigos m [] = True
verificainimigos m (x:xs) = colisaoComBlocos m x && verificainimigos m xs

verificarcolecionaveis :: Mapa -> [(Colecionavel, Posicao)] -> Bool --Verifica se todos os colecionaveis encontra-se num bloco vazio
verificarcolecionaveis m [] = True
verificarcolecionaveis m ((x, (a,b)) : xs) = verificarcolecionaveis' (indicesBlocos m) (x, (a,b)) && verificarcolecionaveis m xs

verificarcolecionaveis' :: [((Int, Int), Bloco)] -> (Colecionavel, Posicao) -> Bool --Verifica se algum colecionavel encontra-se num bloco vazio
verificarcolecionaveis' [] _ = True
verificarcolecionaveis' (((x, y), block) : xs) (c, (a,b))
       |a==fromIntegral x && b==fromIntegral y && block==Vazio = verificarcolecionaveis' xs (c, (a,b))
       |otherwise = False

tamanhoMaximoJogador :: Personagem -> Bool --Veirfica que o tamanha maximo do jogador seja igual ao do alcapao
tamanhoMaximoJogador e@Personagem {tipo= Jogador, tamanho= (x,y)}
      |x<=1 && y<=1 = True
      |otherwise = False



