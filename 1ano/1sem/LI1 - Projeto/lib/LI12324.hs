{-|
Module      : LI12324
Description : Definições base do jogo
Copyright   : Nelson Estevão <d12733@di.uminho.pt>
              Olga Pacheco   <omp@di.uminho.pt>
              Rui Carvalho   <d13696@di.uminho.pt>
              Xavier Pinho   <d12736@di.uminho.pt>

Tipos de dados e funções auxiliares para a realização do projeto de LI1 em 2023/24.
-}
module LI12324 where

import System.Random (mkStdGen, randoms)
import Graphics.Gloss
import Graphics.Gloss.Interface.IO.Game

data Bloco
  = Escada
  | Plataforma
  | Alcapao
  | Vazio
  deriving (Ord, Eq, Read, Show)

data Mapa =
  Mapa (Posicao, Direcao) Posicao [[Bloco]]
  deriving (Eq, Read, Show)

type Hitbox = (Posicao, Posicao)

type Velocidade = (Double, Double)

type Posicao = (Double, Double)

type Tempo = Double

data Direcao
  = Norte
  | Sul
  | Este
  | Oeste
  deriving (Ord, Eq, Read, Show)

data Entidade
  = MacacoMalvado
  | Fantasma
  | Jogador
  deriving (Ord, Eq, Read, Show)

data Colecionavel
  = Moeda
  | Martelo
  deriving (Ord, Eq, Read, Show)

data Personagem =
  Personagem
    {velocidade :: Velocidade
    ,tipo       :: Entidade
    ,posicao    :: Posicao
    ,direcao    :: Direcao
    ,tamanho    :: (Double, Double)
    ,emEscada   :: Bool
    ,ressalta   :: Bool
    ,vida       :: Int
    ,pontos     :: Int
    ,aplicaDano :: (Bool, Double)
    }
  deriving (Eq, Read, Show)

data Acao
  = Subir
  | Descer
  | AndarDireita
  | AndarEsquerda
  | Saltar
  | Parar
  deriving (Eq, Read, Show)

{- | Vetor velocidade da gravidade.
prop> gravidade == (0, 10)
-}

gravidade :: Velocidade
gravidade = (0, 10)

data Jogo =
  Jogo
    { mapa          :: Mapa
    , inimigos      :: [Personagem]
    , colecionaveis :: [(Colecionavel, Posicao)]
    , jogador       :: Personagem
    }
  deriving (Eq, Show)

data Estado = Estado
  { jogo :: Jogo,
    imagens :: Imagens,
    modo :: Modo,
    acao :: Acao
  }

data MenuInicialOpcoes = Jogar | Sair deriving (Show, Eq)

data Modo = MenuInicial MenuInicialOpcoes | EmJogo deriving (Show, Eq)

type Semente = Int

{-| Função que gera uma lista de números aleatórios a partir de uma 'Semente'.

== Exemplos

>>> geraAleatorios 2324 3
[-4152215250714882843,5190394115856197582,1807065739108315696]

>>> geraAleatorios 10 1
[3575835729477015470]
-}

geraAleatorios :: Semente -> Int -> [Int]
geraAleatorios s c = take c $ randoms (mkStdGen s)

data Imagem
  = MenuBackgroundImage
  | GameBackgroundImage
  | PlayButton
  | PlayButton2
  | Quit
  | Quit2
  | AlcapaoBMP
  | Coracaopartido
  | Coracao
  | EscadasBMP
  | EstrelaBMP
  | MarteloBMP
  | Mariohammer2
  | Mariohammer2direita
  | Mariodead
  | Marioescada
  | Marioescadadireita
  | Mariohammerunning
  | Mariohammerrunningdireita
  | Mariohammer
  | Mariohammerdireita
  | Mariojumping
  | Mariojumpingdireita
  | Mariorunning
  | Mariorunningdireita
  | Mario
  | Mariodireita
  | Macaco
  | PlataformaBMP
  | Princesa
  | EmptyBMP
  | MoedaBMP
  | FantasmaBMP
  | FantasmaDireitaBMP
  | Derrota
  | Vitoria
  deriving (Show, Eq)

data BackgroundImage
  = MenuBackground
  | GameBackground
  deriving (Show, Eq)

type Imagens = [(Imagem, Picture)]


jogo1 :: Jogo
jogo1 = Jogo mapaFinal listaInimigosNossoMapa colecionaveis1 mario1

mario1 :: Personagem
mario1 = Personagem (0,0) Jogador (3.5,38.5) Oeste (1,1) False False 3 0 (False,0)

colecionaveis1 :: [(Colecionavel, Posicao)]
colecionaveis1 = [(Moeda,(3.5,6.5)),(Moeda,(18.5,10.5)),(Moeda,(27.5,18.5)),(Moeda,(21.5,22.5)),(Moeda,(7.5,30.5)),(Moeda,(27.5,34.5)),(Moeda,(6.5,38.5)),(Martelo,(13.5,10.5)),(Martelo,(3.5,18.5)),(Martelo,(7.5,26.5)),(Martelo,(23.5,34.5))]

listaInimigosNossoMapa :: [Personagem]
listaInimigosNossoMapa = [Personagem (0,0) Fantasma (21.5,6.5) Este (1,1) False True 1 0 (False,0)
                        ,Personagem (0,0) Fantasma (18.5,10.5) Oeste (1,1) False True 1 0 (False,0)
                        ,Personagem (0,0) Fantasma (7.5,10.5) Este (1,1) False True 1 0 (False,0)
                        ,Personagem (0,0) Fantasma (11.5,14.5) Oeste (1,1) False True 1 0 (False,0)
                        ,Personagem (0,0) Fantasma (5.5,18.5) Este (1,1) False True 1 0 (False,0)
                        ,Personagem (0,0) Fantasma (16.5,18.5) Oeste (1,1) False True 1 0 (False,0)]

macaco :: Personagem
macaco = Personagem (0,0) MacacoMalvado (24.5,6) Este (1,1) False True 1000 0 (False,0)

mapaFinal :: Mapa
mapaFinal =  Mapa ((5.5,33),Oeste) (6.5,2.5) blocosMapaFinal

blocosMapaFinal :: [[Bloco]]
blocosMapaFinal =                             [[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Plataforma,Plataforma,Plataforma,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Alcapao,Alcapao,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Alcapao,Alcapao,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Alcapao,Alcapao,Vazio,Plataforma,Plataforma,Plataforma,Vazio,Vazio,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Plataforma,Alcapao,Alcapao,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Alcapao,Alcapao,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio]
                                              ,[Vazio,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Alcapao,Alcapao,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Alcapao,Alcapao,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio]
                                              ,[Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Vazio,Escada,Vazio,Vazio,Vazio]
                                              ,[Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma,Plataforma]]
