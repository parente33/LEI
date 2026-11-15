module Aula where

areaQ :: Int -> Int
areaQ lado = lado*lado

perimetro :: Float -> Float -> Float
perimetro comp larg = 2*(comp + larg)

exp1 :: [Int] -> (Int, [Int])
exp1 l = (head l, tail l)

pertenceS :: Char -> String -> Bool
pertenceS x lista = elem x lista

removeLista :: [a] -> [a]
removeLista l =  if mod (length l) 2 == 0
                    then tail l
                    else init l

devolveLista :: [a] -> (a,a)
devolveLista l = (head l, last l)

parListas :: ([a],[a]) -> (a,[a])
parListas (xs, ys) = (head xs, ys)

parListas' :: ([a],[a]) -> (a,[a])
parListas' par = (head (fst par), snd par)

somapar :: (Int, Int) -> (Int, Int) -> (Int, Int)
somapar (x,y) (w,z) = (x+w, y+z)

somapar' :: (Int, Int) -> (Int, Int) -> (Int, Int)
somapar' (x,y) (w,z) = if mod (x+w) 2 == 0 
                            then (0, y+z)
                            else (x+w, y+z)
                    