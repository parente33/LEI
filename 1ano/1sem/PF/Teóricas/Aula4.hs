module Aula4 where
import Data.Char


mymap :: (a->b) -> [a] -> [b]
mymap f [] = []
mymap f (x:xs) = f x : mymap f xs


triplos :: [Int] -> [Int]
triplos l = mymap (3*)l


maiusculas :: String -> String
maiusculas = mymap toUpper


somaPares :: [(Float,Float)] -> [Float]
somaPares l = mymap aux l
    where aux (x,y) = x+y

{-
(\(x,y) -> x + y)  ---> funções anónimas
    ^        ^
    |        |
    |        |
    |        |
argumento  corpo da
            função
-}


somaParesAnon :: [(Float,Float)] -> [Float]
somaParesAnon l = mymap (\(x,y)-> x+y) l


myfilter :: (a -> Bool) -> [a] -> [a]
myfilter c [] = []
myfilter c (x:xs) = if (c x)
                        then x:myfilter c xs
                        else myfilter c xs


pares :: Integral a => [a] -> [a]
pares l = myfilter even l


paresAnon :: Integral a => [a] -> [a]
paresAnon l = myfilter (\x->mod x 2 == 0) l
