{-# OPTIONS_GHC -Wno-unrecognised-pragmas #-}
{-# HLINT ignore "Eta reduce" #-}
module TestesAntigos where
import Data.List


-- Teste 22/23

unlines' :: [String] -> String
unlines' [] = []
unlines' (h:t) = h ++ "\n" ++ unlines' t

{-
>>> unlines' ["Prog", "Func"]
"Prog\nFunc"

>>> unlines' ["Recurso","Calculo"]
"Recurso\nCalculo"
-}

type Mat = [[Int]]

stringToMat :: String -> Mat
stringToMat s = map stringToVector (lines s)

{-    o map dá a lista das listas (mat); o stringToVetor a lista de inteiros; e o lines a string

>>> stringToMat "2,4,5,7\n3,4,8,0\n6,4,2,9\n7,0,1,2"
[[2,4,5,7],[3,4,8,0],[6,4,2,9],[7,0,1,2]]

>>> stringToMat "2,3,6,4\n12,3,12,4\n3,-4,5,7"
[[2,3,6,4],[12,3,12,4],[3,-4,5,7]]
-}

stringToVector :: String -> [Int]
stringToVector lista = read ("["++lista++"]") 

{-
>>> stringToVector "5404455454"
[5404455454]
-}

transposta :: String -> String
transposta = unlines . map (intercalate "," . map show) . transpose . stringToMat

data Lista a = Esq a (Lista a) | Dir (Lista a) a | Nula

lista1 = Esq 5 (Dir (Dir (Esq 2 Nula) 3) 10)

semUltimo :: Lista a -> Lista a
semUltimo (Esq x Nula) = Nula
semUltimo (Esq x l) = Esq x (semUltimo l)
semUltimo (Dir l x) = l

instance Show a => Show (Lista a) where
    show :: Lista a -> String
    show l = show (conv l)

conv :: Lista a -> [a]
conv Nula = []
conv (Esq x l) = x : conv l
conv (Dir l x) = conv l ++ [x]



data BTree a = Empty | Node a (BTree a) (BTree a)

inorder :: BTree a -> [a]
inorder Empty = []
inorder (Node r e d) = inorder e ++ r:inorder d


numera :: BTree a -> BTree (a,Int)
numera arv = snd (numeraAux 1 arv) 

numeraAux :: Int -> BTree a -> (Int,BTree (a,Int))
numeraAux n Empty = (0,Empty)
numeraAux n (Node x e d) = let (ne,e1) = numeraAux n e
                               (nd,d1) = numeraAux (n+ne+1) d
                           in (ne+nd+1,Node (x,n+ne) e1 d1)

unInorder :: [a] -> [BTree a]
unInorder [] = []
unInorder l = [Node x e d | (l1,x,l2) <- parte l, e <- unInorder l1, d <- unInorder l2] 
 

parte :: [a] -> [([a],a,[a])]
parte [] = []
parte (h:t) = ([],h,t) : [(h:a,b,c) | (a,b,c) <- parte t]


type MSet a = [(a,Int)]

converteMSet :: MSet a -> [a]
converteMSet [] = []
converteMSet ((a,1):t) = a : converteMSet t
converteMSet ((a,n):t) = a : converteMSet ((a,n-1):t)


removeMSet :: Eq a => a -> MSet a -> MSet a
removeMSet a [] = []
removeMSet a ((x,n):t)
    | a==x = if n>1 then (x,n-1):t else t
    | otherwise = (x,n) : removeMSet a t


uniaoMSet :: Eq a => MSet a -> MSet a -> MSet a
uniaoMSet l1 l2 = foldr insereMSet l1 l2

insereMSet :: Eq a => (a, Int) -> MSet a -> MSet a
insereMSet a [] = [a]
insereMSet (x,y) ((e,n):t)
    | x == e = (e,n+y) : t
    | otherwise = (e,n) : insereMSet (x,y) t


type Posicao = (Int,Int)
data Movimento = Norte | Sul | Este | Oeste
data Caminho = C Posicao [Movimento]

instance Eq Caminho where
    (==) :: Caminho -> Caminho -> Bool
    C pos lmov == C pos' lmov' = length lmov == length lmov' && pos == pos' && posFinal pos lmov == posFinal pos' lmov'
        
posFinal :: Posicao -> [Movimento] -> Posicao
posFinal posi lmov = foldl (\(x,y) lmov -> 
    case lmov of
        Norte -> (x,y+1)
        Sul -> (x,y-1)
        Este -> (x+1,y)
        Oeste -> (x-1,y)
    ) posi lmov


func :: [[Int]] -> [Int]
func l = concat (filter (\x -> sum x >10) l)

func' :: [[Int]] -> [Int]
func' [] = []
func' (h:t)
    | sum h > 10 = h ++ func' t
    | otherwise = func' t


data Prop = Var String | Not Prop | And Prop Prop | Or Prop Prop

p1 :: Prop
p1 = Not (Or (And (Not (Var "A")) (Var "B")) (Var "C"))


eval :: [(String,Bool)] -> Prop -> Bool
eval d (Var s) = myLookup s d
eval d (Not p) = not $ eval d p
eval d (And a b) = eval d a && eval d b
eval d (Or a b) = eval d a || eval d b

myLookup :: Eq a => a -> [(a,b)] -> b
myLookup _ [] = error "variavel nao encontrada"
myLookup x ((a,b):t) = if x==a then b else myLookup x t


nnf :: Prop -> Prop
nnf (Var s) = Var s
nnf (Not p) = case p of 
    Var s -> Not (Var s)
    Not pr -> nnf pr
    pr1 `And` pr2 -> nnf (Not pr1) `Or` nnf (Not pr2)
    pr1 `Or` pr2 -> nnf (Not pr1) `And` nnf (Not pr2)
nnf (And a b) = And (nnf a) (nnf b)
nnf (Or a b) = Or (nnf a) (nnf b)


data Contacto = Casa Integer
    | Trab Integer
    | Tlm Integer
    | Email String
    deriving (Show)
type Nome = String
type Agenda = [(Nome, [Contacto])]

consulta :: [Contacto] -> ([Integer],[String])
consulta = foldr (\c (tlfs,emails) ->
                case c of
                    Tlm n -> (n:tlfs,emails)
                    Casa n -> (n:tlfs,emails)
                    Trab n -> (n:tlfs,emails)
                    Email e -> (tlfs,e:emails)
    ) ([],[])
             