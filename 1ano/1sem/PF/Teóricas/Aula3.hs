module Aula where
import Data.Char (digitToInt)

myunzip :: [(a,b)] -> ([a],[b])
myunzip [] = ([],[])
myunzip ((x,y):t) = (x: fst (myunzip t), y: snd (myunzip t))

myunzip1 :: [(a,b)] -> ([a],[b])
myunzip1 [] = ([],[])
myunzip1 ((x,y):t) = (x:xs, y:ys)
    where (xs,ys) = myunzip t


first :: (a,b,c) -> a
first (x,_,_) = x


mySplitAt :: Int -> [a] -> ([a],[a])
mySplitAt n l | n<=0 = ([],l)
mySplitAt n [] = ([],[])
mySplitAt n (x:xs) = (x:l1, l2)
    where (l1,l2) = mySplitAt (n-1) xs


insere :: Int -> [Int] -> [Int] -- insere um argumento numa lista ordenada
insere n [] = [n]
insere n (x:xs) 
    | n <= x = n:x:xs
    | otherwise = x: insere n xs

ordena :: [Int] -> [Int]
ordena [] = []
ordena (x:xs) = insere x (ordena xs)


parte :: Int -> [Int] -> ([Int],[Int])
parte n [] = ([],[])
parte n (x:xs) 
    | x<n = (x:l1, l2)
    | otherwise = (l1 ,x:l2)
   where (l1,l2) = parte n xs

qsort :: [Int] -> [Int]
qsort [] = []
qsort (x:xs) = let (la,lb) = parte x xs
               in (qsort la ++ [x] ++ qsort lb) 


merge :: [Int] -> [Int] -> [Int]
merge [] l = l
merge l [] = l
merge (x:xs) (y:ys) 
    | x <= y = x: merge xs (y:ys)
    | otherwise = y: merge (x:xs) ys

msort :: [Int] -> [Int]
msort [] = []
msort [x] = [x]
msort l = let n = length l `div` 2 
              (l1,l2) = mySplitAt n l
          in merge (msort l1) (msort l2)


maxim :: [Int] -> Int
maxim [x] = x
maxim (x:xs) = max x (maxim xs)


maximo :: [Int] -> Int
maximo [x] = x
maximo (x:xs) = maxAux xs x
    where maxAux (h:t) m = maxAux t (max h m)
          maxAux [] m = m

invert :: [a] -> [a]
invert l = invertAc l []
    where invertAc [] ac = ac
          invertAc (x:xs) ac = invertAc xs (x:ac)


stringToInt :: String -> Int
stringToInt (x:xs) = aux xs (digitToInt x)
    where aux (x:xs) n = aux xs (n*10 + digitToInt x)
          aux [] n = n

divisores :: Integer -> [Integer]
divisores n = [ x | x <- [1..n], mod n x ==0 ]

primo :: Integer -> Bool
primo n = divisores n == [1,n]

primosate :: Integer -> [Integer]
primosate x = [ y | y <- [1..x], primo y ]

primate :: Integer -> [Integer]
primate n = crivo [2..n]

crivo :: [Integer] -> [Integer]
crivo (x:xs) = x : crivo [ n | n <- xs, mod n x /= 0]
crivo [] = []
