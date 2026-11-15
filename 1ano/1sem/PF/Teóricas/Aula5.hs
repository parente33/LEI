module Aula5 where

data BTree a = Empty
           | Node a (BTree a ) (BTree a)
    deriving (Show)


arv1 = Node 3 (Node 5 Empty Empty)
              (Node 7 (Node 6 Empty Empty) (Node 0 Empty Empty))

arv2 = Node 10 arv1 (Node 1 arv1 Empty)

altura :: BTree a -> Int
altura Empty = 0
altura (Node x e d) = 1 + max (altura e) (altura d)


procura :: Eq a => a -> BTree a -> Bool
procura x Empty = False
procura x (Node y e d) = x==y || procura x e || procura x d


elemAltura :: Eq a => a -> BTree a -> [Int]
elemAltura x arv = elemAlturas x arv 1

elemAlturas :: Eq a => a -> BTree a -> Int -> [Int]
elemAlturas x Empty n = []
elemAlturas x (Node y e d) n
    | x==y = n : elemAlturas x e (n+1) ++ elemAlturas x d (n+1)
    | otherwise = elemAlturas x e (n+1) ++ elemAlturas x d (n+1)


-- travessias

infixa :: BTree a -> [a]
infixa Empty = []
infixa (Node x e d) = infixa e ++ [x] ++ infixa d

prefixa :: BTree a -> [a]
preixa Empty = []
prefixa (Node x e d) = x: prefixa e ++ prefixa d

posfixa :: BTree a -> [a]
posixa Empty = []
posfixa (Node x e d) = prefixa e ++ prefixa d ++ [x]

-- procura

elemBT :: Ord a => a -> BTree a -> Bool
elemBT x Empty = False
elemBT x (Node y e d)
    | x<y = elemBT x e
    | x==y = True
    | otherwise = elemBT x d



-- lista para árvore

--listToBT :: Ord a => [a] -> BTree a
--listToBT


testaBalanceada :: BTree a -> Bool
testaBalanceada Empty = True
testaBalanceada (Node x e d) = abs (altura e - altura d) <= 1 && testaBalanceada e && testaBalanceada d

testeProcura :: Ord a => BTree a -> Bool
testeProcura Empty = True
testeProcura (Node x e d) = testeProcura e && testeProcura d && all (x>) (posfixa e) && all (>x) (posfixa d)

testProc :: Ord a => BTree a -> Bool
testProc Empty = True
testProc (Node x Empty Empty) = True
testProc (Node x a@(Node x1 e1 d1) b@(Node x2 e2 d2)) = x>x1 && x<x2 && testProc a && testProc b
testProc (Node x a@(Node x1 e1 d1) Empty) = x>x1 && testProc a
testProc (Node x Empty a@(Node x2 e2 d2)) = x<x2 && testProc a

