module Main where

import Graphics.Gloss
import Graphics.Gloss.Interface.Pure.Game

data EstadoGloss = EstadoGloss                   
                { estado :: Estado
                , imagens :: Imagens
                , tempo :: Tempo
                , keys :: [Key]
                }

type Estado = (Float, Float)
type Tempo = Float
type Imagens = [Picture]



estadoInicial :: Estado
estadoInicial = (0,0)

estadoGlossInicial :: Picture -> Picture -> EstadoGloss
estadoGlossInicial p1 p2 = EstadoGloss estadoInicial [p1,p2] 0.0 []

-- altera imagem a cada 100 unidades de tempo
desenhaEstado :: EstadoGloss -> Picture
desenhaEstado e  = Pictures [Translate x y imagem, sTempo]
    where (x,y) = estado e
          temp = round $ n*1000
          [p1,p2] = imagens e
          n = tempo e
          imagem = if mod temp 200<100 then p1 else p2
          sTempo = Translate 120 180 $ Scale 0.1 0.1 $ Text (show n) 

reageEvento :: Event -> EstadoGloss -> EstadoGloss
eageEvento (EventKey (Char 's') Down _ _) _ = error "FIM"
reageEvento (EventKey k Down _ _) e = e {keys = [k]}
reageEvento (EventKey k Up _ _) e = e {keys=[]}
reageEvento _ s = s


reageEvento' :: Event -> EstadoGloss -> EstadoGloss
reageEvento' (EventKey (SpecialKey KeyUp) Down _ _) e = e {estado = (x,y+5)}
  where (x,y) = estado e
reageEvento' (EventKey (SpecialKey KeyDown) Down _ _) e = e {estado = (x,y-5)}
  where (x,y) = estado e
reageEvento' (EventKey (SpecialKey KeyLeft) Down _ _) e = e {estado = (x-5,y)}
  where (x,y) = estado e
reageEvento' (EventKey (SpecialKey KeyRight) Down _ _) e = e {estado = (x+5,y)}
  where (x,y) = estado e
reageEvento' (EventKey (Char 's') Down _ _) _ = error "FIM"
reageEvento' _ s = s  -- ignora qualquer outro evento


reageTempo :: Float -> EstadoGloss -> EstadoGloss
reageTempo n e = e {tempo = b+n, estado = alteraEstado (x,y) lk}
  where b = tempo e
        lk = keys e
        (x,y) = estado e
        alteraEstado (x,y) [] = (x,y)
        alteraEstado (x,y) [k] =
          case k of (SpecialKey KeyUp) -> (x,y+5)
                    (SpecialKey KeyDown) -> (x,y-5)
                    (SpecialKey KeyLeft) -> (x-5,y)
                    (SpecialKey KeyRight) -> (x+5,y)

fr:: Int
fr = 60

dm :: Display
dm = InWindow
       "Novo Jogo"  -- título da janela
       (400, 400)   -- dimensão da janela
       (0,0)        -- posição no ecran

main :: IO ()
main = do 
         p1 <- loadBMP "pac_open.bmp"
         p2 <- loadBMP "pac_closed.bmp"
         play  dm                          -- janela onde irá decorrer o jogo
               (greyN 0.5)                 -- cor do fundo da janela
               fr                          -- frame rate
               (estadoGlossInicial p1 p2)  -- define estado inicial do jogo
               desenhaEstado               -- desenha o estado do jogo
               reageEvento                 -- reage a um evento
               reageTempo                  -- reage ao passar do tempo