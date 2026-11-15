module Q8a107363 where

type Equipa = String
type Golos = Int
type Jogo = ((Equipa,Golos), (Equipa,Golos))
type Jornada = [Jogo]


{-|
--^ Devolve o total de golos marcados pelas equipas que perdem

>>> golosDosQuePerdem [(("Benfica",2),("Sporting",1)),(("Guimaraes",0),("Porto",1))]
1

>>> golosDosQuePerdem [(("Pinhalnovense",4),("Estrela da Amadora",2)),(("Anadia",2),("Ponte da Barca",2)),(("Sabugal FC",1),("Amigos do Tejo",0))]
4
-}

f :: Jornada -> [Golos]
f listajogos = map (\((_,g1), (_,g2)) -> min g1 g2) listajogos

golosDosQuePerdem :: Jornada -> Golos
golosDosQuePerdem jornada = sum (f jornada)