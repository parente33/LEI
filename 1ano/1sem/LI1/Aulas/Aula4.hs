module Aula4 where

trocaPrimUltLi :: [a] -> [a]
trocaPrimUltLi [] = []
trocaPrimUltLi [x] = [x]
trocaPrimUltLi (x:xs) = last xs : init xs ++ [x]

trocaPrimUltMat :: [[a]] -> [[a]]
trocaPrimUltMat = trocaPrimUltLi


trocaPrimUltCol :: [[a]] -> [[a]]
trocaPrimUltCol [] = []
trocaPrimUltCol (l:m) = trocaPrimUltLi l : trocaPrimUltCol m

somaLista :: Num a => [a] -> [a] -> [a]
somaLista [] _ = []
somaLista _ [] = []
somaLista (x:xs) (y:ys) = x+y : somaLista xs ys

posicaoElem :: Eq a => [a] -> a -> Int
posicaoElem [] _ = -1
posicaoElem (x:xs) a = posAux (x:xs) a 0

posAux :: Eq a => [a] -> a -> Int -> Int 
posAux [] _ _ = -1
posAux (x:xs) a p
    | x==a = p
    | otherwise = posAux xs a (p+1)

substituiElem :: String -> Int -> Char -> String
substituiElem [] _ _ = []
substituiElem (x:xs) n char
    | n<length (x:xs) = take n (x:xs) ++ [char] ++ drop (n+1) (x:xs)
    | n>length (x:xs) = x:xs    

