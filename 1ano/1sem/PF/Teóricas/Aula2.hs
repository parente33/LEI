module Aula where

soPos :: [Int] -> [Int]
soPos [] = []
soPos (x:xs) = if x>0
                then x : soPos xs
                else soPos xs

mytake :: Int -> [a] -> [a]
mytake n l | n<=0 = []
mytake n [] = []
mytake n (x:xs) = x : mytake (n-1)xs

mydrop :: Int -> [a] -> [a]
mydrop n l | n<=0 = l
mydrop n [] = []
mydrop n (x:xs) = mydrop (n-1)xs

cola :: [a] -> [a] -> [a]
cola [] l2 = l2
cola l1 [] = l1
cola (x:xs) l2 = x : cola xs l2

inverte :: [a] -> [a]
inverte [] = []
inverte (x:xs) = inverte xs ++ [x]

fun :: [(a,b)] -> [b]
fun [] = []
fun (x:xs) = fun xs ++ [snd x]

myzip :: [a] -> [b] -> [(a,b)]
myzip (x:xs) (y:ys) = (x,y) : myzip xs ys
myzip _ _ = []

(!!!) :: [a] -> Int -> a
(!!!) (x:xs) 0 = x
(!!!) (x:xs) n | n > 0 = xs !!! (n-1)

somaPares :: [(Int,Int)] -> [Int]
somaPares ((x,y):t) = x+y : somaPares t
somaPares [] = []

somaCompPares :: [(Int,Int)] -> [(Int,Int)]
somaCompPares [] = [(0,0)]
somaCompPares ((x,y):t) = (x+ fst (somaCompPares t), y+ snd (somaCompPares t))

somaCompPares' :: [(Int,Int)] -> [(Int,Int)]
somaCompPares' [] = [(0,0)]
somaCompPares' ((x,y):t) = (x+a,y+b)
    where (a,b) = somaCompPares' t
    