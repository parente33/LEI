module Aula2 where

data Movimento = Norte | Sul | Este | Oeste deriving (Show,Eq)

type Ponto = (Double, Double)

move :: Ponto -> Movimento -> Ponto
move (x,y) Norte = (x,y+1)
move (x,y) Sul = (x,y-1)
move (x,y) Oeste = (x-1,y)
move (x,y) Este = (x+1,y)

move' :: Ponto -> Movimento -> Ponto
move' (x1,y1) m = if m == Norte then a
                 else if m == Sul then b
                 else if m == Oeste then c
                 else d
    where a = (x1,y1+1)
          b = (x1,y1-1)
          c = (x1-1,y1)
          d = (x1+1,y1)

move'' :: Ponto -> Movimento -> Ponto
move'' (x,y) m
    | m==Norte = (x,y+1)
    | m==Oeste = (x-1,y)
    | m==Sul = (x,y-1)
    | otherwise = (x+1,y)

dist :: Ponto -> Ponto -> Double
dist (x1,y1) (x2,y2) = sqrt ((x1-x2)^2 + (y1-y2)^2)

novOrigemCantEsq :: Ponto -> Double -> Ponto
novOrigemCantEsq (x1,y1) l = (x1,-(l-y1))

novOrigemCentr :: Ponto -> Double -> Ponto
novOrigemCentr (x1,y1) l = (x1-l/2,y1-l/2)

type Velocidade = Double
type Tempo = Double

movex :: Ponto -> Velocidade -> Tempo -> Ponto
movex (x1,y1) v t = (x1+d,y1)
    where d = v/t

movey :: Ponto -> Velocidade -> Tempo -> Ponto
movey (x1,y1) v t = (x1,y1+d)
    where d = v/t

type Velocidadexy = (Double,Double)

movexy :: Ponto -> Velocidadexy -> Tempo -> Ponto
movexy (x1,y1) (vx,vy) t = (x1+dx,y1+dy)
        where dx = vx/t
              dy = vy/t

data Figura = Circulo Ponto Double | Rectangulo Ponto Ponto
            deriving (Show,Eq)


dentroFig :: Figura -> Ponto -> Bool
dentroFig (Circulo (x,y) r) (x1,y1) = if (dist (x,y) (x1,y1)) <= r then True
                                      else False

dentroFig' :: Figura -> Ponto -> Bool
dentroFig' (Rectangulo (x,y) (x1,y1)) (x2,y2) = if x2<= abs (x-x1) && y2 <= abs (y-y1) then True else False

dentroFig'' :: Figura -> (Double, Double) -> Bool
dentroFig'' (Rectangulo (x,y) (x1,y1)) (x2,y2) = 
        let maxx= max x x1
            maxy= max y y1
            minx= min x x1
            miny= min y y1
        in x2>=minx && x2<=maxx && y2>=miny && y2<=maxy

data Time = AM Int Int | PM Int Int deriving (Show,Eq)

f :: Time -> Bool
f (AM h m) = if h >= 0 && h < 12 && m >= 0 && m < 60 then True
                else False
f (PM h m) = if h >= 0 && h < 12 && m >= 0 && m < 60 then True
                else False

f' :: Time -> Bool
f' (AM h m) = h >= 0 && h < 12 && m >= 0 && m < 60
f' (PM h m) = h >= 0 && h < 12 && m >= 0 && m < 60

data Ponto2 = Cartesiano Double Double | Polar Double Double
           deriving (Show,Eq)

posx :: Ponto2 -> Double
posx (Cartesiano x1 y1) = x1
posx (Polar x rad) = abs (x*cos rad)

posy :: Ponto2 -> Double
posy (Cartesiano x1 y1) = y1
posy (Polar x rad) = abs (x*sin rad)

raio :: Ponto2 -> Double
raio (Cartesiano x y) = dist (x,y) (0,0)
raio (Polar r rad) = r

angulo :: Ponto2 -> Double
angulo (Polar r rad) = rad
angulo (Cartesiano x y) = atan y/x

dist7 :: Ponto2 -> Ponto2 -> Double
dist7 p1 p2 = dist a b
    where a = (posx p1, posy p1)
          b = (posx p2, posy p2)

type Nome = String
type Coordenada = (Double, Double)
data Movimento2 = N | S | E | W deriving (Show,Eq) 
type Movimentos = [Movimento]
data PosicaoPessoa = Pos Nome Coordenada deriving (Show,Eq)
