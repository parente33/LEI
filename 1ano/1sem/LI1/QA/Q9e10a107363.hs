module Questao9e10 where

type Coordenada = (Int,Int)
type Comprimento = Int
type Largura = Int
data Movimento = E | W | N | S  deriving (Show,Eq)
type Mapa = (Comprimento,Largura)


movimentosPossiveis :: Coordenada -> Coordenada -> [Movimento]
movimentosPossiveis (xi,yi) (xf,yf)
    | xi==xf && yi==yf = []
    | xi<xf && yi<yf = fazLista E (xf-xi)++fazLista N (yf-yi)
    | xi<xf && yi>yf = fazLista E (xf-xi)++fazLista S (yi-yf)
    | xi>xf && yi<yf = fazLista W (xi-xf)++fazLista N (yf-yi)
    | xi>xf && yi>yf = fazLista W (xi-xf)++fazLista S (yi-yf)
    | xi<xf && yi==yf = fazLista E (xf-xi)
    | xi>xf && yi==yf = fazLista W (xi-xf)
    | xi==xf && yi<yf = fazLista N (yf-yi)
    | xi==xf && yi>yf = fazLista S (yi-yf)

fazLista :: Movimento -> Int -> [Movimento]
fazLista _ 0 = []
fazLista N n = N:fazLista N (n-1)
fazLista S n = S:fazLista S (n-1)
fazLista W n = W:fazLista W (n-1)
fazLista E n = E:fazLista E (n-1)


dentroMapa :: Mapa -> Coordenada -> [Movimento] -> Bool
dentroMapa mapa coord [] = True
dentroMapa (c,l) (x,y) lista
    | posAposMovim (x,y) lista > (c,l) = False
    | otherwise = True

posAposMovim :: Coordenada -> [Movimento] -> Coordenada -- calcula a posicao de um personagem apos um movimento unitario
posAposMovim (x,y) (N:t) = (x,y+1)
posAposMovim (x,y) (S:t) = (x,y-1)
posAposMovim (x,y) (W:t) = (x-1,y)
posAposMovim (x,y) (E:t) = (x+1,y)