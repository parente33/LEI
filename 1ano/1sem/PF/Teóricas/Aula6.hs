module Aula6 where

data BTree a = Empty
            | Node a (BTree a) (BTree a)
    deriving Show 

data RTree a = R a [RTree a]
    deriving Show


contaRT :: RTree a -> Int -- nodos da árvore
contaRT (R x l) = 1 + sum (map contaRT l)

alturaRT :: RTree a -> Int
alturaRT (R x []) = 1
alturaRT (R x l) = 1 + maximum (map alturaRT l)

converte :: BTree a -> RTree a
converte (Node x Empty Empty) = R x []
converte (Node x e Empty) = R x [converte e]
converte (Node x Empty d) = R x [converte d]
converte (Node x e d) = R x [converte e, converte d]


pertenceRT :: Eq a => a -> RTree a -> Bool
pertenceRT x (R y lista) = x==y || any (pertenceRT x) lista


preOrderRT :: RTree a -> [a]
preOrderRT (R x lista) = x : concat (map preOrderRT lista)

travNiveisBT :: BTree a -> [a]
travNiveisBT arv = aux [arv]

aux :: [BTree a] -> [a]
aux [] = []
aux (Empty:t) = aux t
aux ((Node x e d):t) = x : aux (t++[e,d])