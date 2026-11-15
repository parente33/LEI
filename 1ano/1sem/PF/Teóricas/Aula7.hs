module Aula7 where
import Ficha1

-- classes

data Nat = Zero
        | Suc Nat
    -- deriving (Show)

dois = Suc (Suc Zero)
quatro = Suc (Suc dois)
cinco = Suc quatro

instance Eq Nat where 
    Zero == Zero = True
    (Suc x) == (Suc y) = x == y
    _ == _ = False

instance Ord Nat where
    (<=) :: Nat -> Nat -> Bool
    Zero <= _ = True
    (Suc x) <= Zero = False
    (Suc x) <= (Suc y) = x<=y


natToInt :: Nat -> Int
natToInt Zero = 0
natToInt (Suc x) = 1 + natToInt x

instance Show Nat where
    show :: Nat -> String
    show n = show (natToInt n)

{-
data Time = AM Int Int
          | PM Int Int

instance Eq Time where
    t1 == t2 = convHoras4 t1 == convHoras4 t2

instance Ord Time where
    t1 <= t2 = convHoras4 t1 <= convHoras4 t2
-}

instance Num Nat where
    (+) :: Nat -> Nat -> Nat
    Zero + n = n
    (Suc x) + n = Suc (x+n) -- == x+n+1

    (*) :: Nat -> Nat -> Nat
    Zero * n = Zero
    (Suc x) * n = n + x*n

    (-) :: Nat -> Nat -> Nat
    n - Zero = n
    Zero - (Suc _) = error "Nao esta definido"
    (Suc x) - (Suc y) = x-y

    abs :: Nat -> Nat
    abs n = n

    signum :: Nat -> Nat
    signum Zero = Zero
    signum (Suc x) = Suc Zero

    fromInteger :: Integer -> Nat
    fromInteger x
        | x == 0 = Zero
        | x>0 = Suc (fromInteger (x-1))
        | x<0 = error "Nao esta definido"


instance Enum Nat where
    toEnum :: Int -> Nat
    toEnum 0 = Zero
    toEnum x | x>0 = Suc (toEnum (x-1))
             | x<0 = error "Nao esta definido"
            
    fromEnum :: Nat -> Int
    fromEnum = natToInt

    
