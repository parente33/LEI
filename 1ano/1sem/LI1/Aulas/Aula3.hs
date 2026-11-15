{-|
Module      :  Exemplo
Description :  Módulo Haskell contendo exemplos de funções recursivas
Copyright   :  (c) Olga Pacheco <omp@di.uminho.pt>  
                              
Este módulo contém definições Haskell para o cálculo de funções
recursivas simples.
-}

module Aula3 where

{-|
Module       : Data.Char
Description  : Módulo Haskell contendo funções
Copyright    : (c) 
-}

import Data.Char

-- | adiciona um valor dado a uma lista
adicionalista :: Int -> [Int] -> [Int]
adicionalista _ [] = []
adicionalista a (y:ys) = y+a : adicionalista a ys

-- | retira strings de uma lista começados por um dado caracter
retiraString :: Char -> [String] -> [String]
retiraString _ [] = []
retiraString c (h:t)
    | h == [] = retiraString c t
    | c == head h = retiraString c t
    | otherwise = h : retiraString c t

-- | adiciona um valor dado apenas à primeira componente do par (pode ser lista vaiza)
addpar :: Int -> [(Int,Int)] -> [(Int,Int)]
addpar _ [] = []
addpar a ((x,y):t) = (a+x,y) : addpar a t

-- | lista é não vazia
maiorvalor :: [(Int,Int)] -> Int
maiorvalor [(x,y)] = y
maiorvalor ((x,y):(w,z):t)
    | y>z = maiorvalor ((x,y):t)
    |otherwise = maiorvalor ((w,z):t)

-- | chr e ord são funções inversas uma da outra, ord transforma o dígito dado no valor correspondente em ASCII, soma-se uma unidade e usa-se a função chr para inverter tal valor para dígito
nextdigit :: Char -> Char
nextdigit '9' = '0'
nextdigit c = chr (ord c+1) 

-- | devolve a vogal a seguir
nextvogal' :: Char -> Char
nextvogal' c = aux c "aeioua"
    where aux :: Char -> String -> Char
          aux c s
            | c== head s = head (tail s)
            | otherwise = aux c (tail s)

type Nome = String 
type Coordenada = (Int, Int) 
data Movimento = N | S | E | W deriving (Show,Eq) 
type Movimentos = [Movimento]
data PosicaoPessoa = Pos Nome Coordenada deriving (Show,Eq)

{-}
>>> posicao (Pos "Abilio" (5,5)) [N,E,N,E]
Pos "Abilio (7,7)"
-}

posicao :: PosicaoPessoa -> Movimentos -> PosicaoPessoa -- ^ uma pessoa faz apenas um movimento
posicao p [] = p
posicao (Pos nome (x,y)) (m:lm)
    | m == N = posicao (Pos nome (x,y+1)) lm 
    | m == E = posicao (Pos nome (x+1,y)) lm 
    | m == W = posicao (Pos nome (x-1,y)) lm 
    | m == S = posicao (Pos nome (x,y-1)) lm 

posicoesM :: [PosicaoPessoa] -> Movimento -> [PosicaoPessoa] -- ^ várias pessoas fazem apenas um movimento
posicoesM [] _ = []
posicoesM (p:lp) m = posicao p [m] : posicoesM lp m

posicoesMs :: [PosicaoPessoa] -> Movimentos -> [PosicaoPessoa] -- ^ várias pessoas fazem vários movimentos
posicoesMs [] _ = []
posicoesMs lposicoes [] = lposicoes
posicoesMs lposicoes (m:lm) = posicoesMs (posicoesM lposicoes m) lm

{-
>>> coordenadaNorte [Pos "Abilio" (2,3), Pos "Marega" (1,4), Pos "Carlinhos" (1,2)]
(1,4)
-}

coordenadaNorte :: [PosicaoPessoa] -> Coordenada -- ^ indica a coordenada da pessoa mais a norte (presume-se que não é lista vazia)
coordenadaNorte [Pos n (x,y)] = (x,y)
coordenadaNorte (Pos nome (x,y):lp) = 
    let (xn,yn) = coordenadaNorte lp
    in if y>yn then (x,y)
               else (xn,yn)

pessoaNorte :: [PosicaoPessoa] -> [Nome]
pessoaNorte [] = []
pessoaNorte l =
    let (xn,yn) = coordenadaNorte l
    in getPessoas l yn

getPessoas :: [PosicaoPessoa] -> Int -> [Nome]
getPessoas [] _ = []
getPessoas ((Pos nome (x,y):t)) yn 
    | y==yn = nome : getPessoas t yn
    | otherwise = getPessoas t yn



pessoasNorte2 :: [PosicaoPessoa] -> [Nome]
pessoasNorte2 [] = []
pessoasNorte2 ((Pos nome (x,y)):t) = pessoasNAux t y [nome]

pessoasNAux :: [PosicaoPessoa] -> Int -> [Nome] -> [Nome]
pessoasNAux [] yn ln = ln
pessoasNAux ((Pos nome1 (x1,y1)):t) yn ln
    | y1<yn = pessoasNAux t yn ln
    | y1==yn = pessoasNAux t yn (nome1:ln)
    | y1>yn = pessoasNAux t y1 [nome1]



desloca1Dir :: [a] -> [a]
desloca1Dir [] = [] 
desloca1Dir (x:xs) = last (x:xs) : init (x:xs)

{-}
>>> desloca1Dir [1,2,3,4]
[4,1,2,3]
-}

deslocaDir :: [a] -> Int -> [a]
deslocaDir [] _ = []
deslocaDir (x:xs) 0 = x:xs
deslocaDir (x:xs) n = deslocaDir (desloca1Dir (x:xs)) (n-1)

desloca1Esq :: [a] -> [a]
desloca1Esq [] = []
desloca1Esq (x:xs) = xs ++ [x]

{-
>>> desloca1Esq [1,2,3,4]
[2,3,4,1]
-}

deslocaEsq :: [a] -> Int -> [a]
deslocaEsq [] _ = []
deslocaEsq (x:xs) 0 = x:xs
deslocaEsq (x:xs) n = deslocaEsq (desloca1Esq (x:xs)) (n-1)


deslocaDirV2 :: [a] -> Int -> [a]
deslocaDirV2 l n =
    let t = length l
        lf = take (t-n) l
        li = drop (t-n) l
    in li++lf


zip55 :: [a] -> [b] -> [(a,b)]
zip55 [] _ = []
zip55 _ [] = []
zip55 (x:xs) (y:ys) = (x,y) : zip55 xs ys

--unzip55 :: [(a,b)] -> ([a],[b])
--unzip55

mydiv :: Int -> Int -> Maybe Int
mydiv x y
    | x>0 && y>0 = Just (div x y)
    | otherwise = Nothing

