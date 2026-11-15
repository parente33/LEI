module Tarefa5 where

import Data.Maybe (fromJust)
import Graphics.Gloss
import Graphics.Gloss.Interface.IO.Game
import System.Exit (exitFailure)
import LI12324
import Tarefa1
import Tarefa3
import Tarefa4
import LI12324 (Imagem(MoedaBMP))
import GHC.Base (TrName(TrNameD))
import GHC.Conc (ThreadStatus(ThreadRunning))


getImagem :: Imagem -> Imagens -> Picture --Recolha a Picture que esta acossiada a uma imagem
getImagem k d = fromJust $ lookup k d

desenha :: Estado -> IO Picture --Desenha o jogo no ecra
desenha e@Estado {modo = MenuInicial Jogar, imagens = imgs} = do
  return $ Pictures [getImagem MenuBackgroundImage imgs, Translate 470 (-250) $ getImagem PlayButton2 imgs, Translate 470 (-400) $ getImagem Quit imgs]
desenha e@Estado {modo = MenuInicial Sair, imagens = imgs} = do
  return $ Pictures [getImagem MenuBackgroundImage imgs, Translate 470 (-250) $ getImagem PlayButton imgs, Translate 470 (-400) $ getImagem Quit2 imgs]
desenha e@Estado {modo = EmJogo, imagens = imgs, jogo = jogo} = do
  return $ Pictures (getImagem GameBackgroundImage imgs : mapaImagem mapaFinal imgs 1 ++ posicaoEstrela imgs jogo  ++ posicaoColecionaveis imgs jogo ++ desenhaVidas imgs jogo ++ desenhaPontuacao imgs jogo ++ posicaoFantasmas imgs jogo ++ [posicaoMacaco imgs macaco] ++ [posicaoMario imgs jogo]++ desenhaVitoriaOuDerrota imgs jogo)

desenhaVidas :: Imagens -> Jogo -> [Picture]
desenhaVidas imgs Jogo {jogador = mario}
           |n==3 = Translate (-320) 460 (getImagem Coracao imgs) : Translate (-270) 460 (getImagem Coracao imgs) : [Translate (-220) 460 (getImagem Coracao imgs)]
           |n==2 = Translate (-320) 460 (getImagem Coracao imgs) : Translate (-270) 460 (getImagem Coracao imgs) : [Translate (-220) 460 (getImagem Coracaopartido imgs)]
           |n==1 = Translate (-320) 460 (getImagem Coracao imgs) : Translate (-270) 460 (getImagem Coracaopartido imgs) : [Translate (-220) 460 (getImagem Coracaopartido imgs)]
           |n==0 = Translate (-320) 460 (getImagem Coracaopartido imgs) : Translate (-270) 460 (getImagem Coracaopartido imgs) : [Translate (-220) 460 (getImagem Coracaopartido imgs)]
              where n = vida mario

desenhaPontuacao :: Imagens -> Jogo -> [Picture]
desenhaPontuacao imgs Jogo {jogador = mario} =
    [ Color red (Translate 230 450 $ Scale 0.20 0.20 $ Text ("Score:"++show n)) ]
    where n = pontos mario


posicaoEstrela :: Imagens -> Jogo -> [Picture] --Calcula a posicao da estrela no ecra
posicaoEstrela imgs Jogo {mapa = (Mapa _ (x,y) _)} = [Translate (realToFrac (14.84-x) * (-24)) (realToFrac (y - 20) * (-24)) (getImagem EstrelaBMP imgs)]

desenhaVitoriaOuDerrota :: Imagens -> Jogo -> [Picture]
desenhaVitoriaOuDerrota imgs j
           |colideComEstrela j = [getImagem Vitoria imgs]
           |ficasemvidas j = [getImagem Derrota imgs]
           |otherwise = []


posicaoColecionaveis :: Imagens -> Jogo -> [Picture] --Calcula a pocicao dos colecionaveis no ecra
posicaoColecionaveis imgs Jogo {colecionaveis = col} = posicaoColecionaveis' imgs col

posicaoColecionaveis' :: Imagens -> [(Colecionavel, Posicao)] -> [Picture] --funcao auxiliar à anterior
posicaoColecionaveis' _ [] = []
posicaoColecionaveis' imgs ((c,(x,y)):xs)
           |c==Moeda = Translate (realToFrac (14.84-x) * (-24)) (realToFrac (y - 20) * (-24)) (getImagem MoedaBMP imgs) : posicaoColecionaveis' imgs xs
           |c==Martelo = Translate (realToFrac (14.84-x) * (-24)) (realToFrac (y - 20) * (-24)) (getImagem MarteloBMP imgs) : posicaoColecionaveis' imgs xs
           |otherwise = []

posicaoMario :: Imagens -> Jogo -> Picture --Calcula a posicao do Mario no ecra
posicaoMario imgs Jogo {jogador = mario} =
        Translate (realToFrac (14.84-x) * (-24)) (realToFrac (y - 20) * (-24)) $ getImagem (direcaodaimagem z) imgs
                     where (x, y) = posicao mario
                           z = direcao mario

posicaoFantasmas :: Imagens -> Jogo -> [Picture]
posicaoFantasmas imgs Jogo {inimigos = inimigos} = posicaoFantasmas' imgs inimigos

posicaoFantasmas' :: Imagens -> [Personagem] -> [Picture]
posicaoFantasmas' _ [] = []
posicaoFantasmas' imgs (x:xs) = posicaoFantasmas'' imgs x : posicaoFantasmas' imgs xs

posicaoFantasmas'' :: Imagens -> Personagem -> Picture
posicaoFantasmas'' imgs inimigo =
        Translate (realToFrac (14.84-x) * (-24)) (realToFrac (y - 20) * (-24)) $ getImagem (direcaodaimagemInimigo z) imgs
                     where (x, y) = posicao inimigo
                           z = direcao inimigo

posicaoMacaco :: Imagens -> Personagem -> Picture --Calcula a posicao do Mario no ecra
posicaoMacaco imgs macaco =
        Translate (realToFrac (14.84-x) * (-24)) (realToFrac (y - 20) * (-24)) $ getImagem Macaco imgs
                     where (x, y) = posicao macaco

direcaodaimagem :: Direcao -> Imagem --Escolhe o BMP de acordo com a direcao
direcaodaimagem x
          |x==Este = Mariodireita
          |x==Oeste = Mario
          |x==Norte = Marioescada
          |x==Sul = Marioescadadireita

direcaodaimagemInimigo :: Direcao -> Imagem --Escolhe o BMP de acordo com a direcao
direcaodaimagemInimigo x
          |x==Este = FantasmaDireitaBMP
          |x==Oeste = FantasmaBMP


blocotobmp :: [Bloco] -> Imagem --Tranforma um bloco no seu respetivo BMP
blocotobmp [] = EstrelaBMP
blocotobmp (x:xs)
          |x==Escada = EscadasBMP
          |x==Plataforma = PlataformaBMP
          |x==Alcapao = AlcapaoBMP
          |otherwise = EmptyBMP

blocotoimagem :: Bloco -> Imagens -> Float -> Float -> Picture --Calcula a posicao de um bloco no ecra
blocotoimagem x imgs n l = Translate (-320 + 24 * (l - 2)) (468 - 24 * (n - 1)) $ getImagem (blocotobmp [x]) imgs

mapaImagem :: Mapa -> Imagens -> Float -> [Picture] --Calcula a posicao de todos os blocos do mapa no ecra
mapaImagem (Mapa z y []) _ _ = []
mapaImagem (Mapa z y (x:xs)) imgs n = linhatoimagem x imgs n 1 ++ mapaImagem (Mapa z y xs) imgs (n+1)

linhatoimagem :: [Bloco] -> Imagens -> Float -> Float -> [Picture] --Calcula a posicao de todos os blocos de uma linha do mapa no ecra
linhatoimagem [] _ _ _ = []
linhatoimagem (x:xs) imgs n z = blocotoimagem x imgs n z : linhatoimagem xs imgs n (z+1)


reage :: Event -> Estado -> IO Estado --Inputs das teclas e os seus respetivos Outputs

reage (EventKey (SpecialKey KeyDown) Down _ _) e@Estado {modo = MenuInicial Jogar} =
  return e {modo = MenuInicial Sair}

reage (EventKey (SpecialKey KeyUp) Down _ _) e@Estado {modo = MenuInicial Sair} =
  return e {modo = MenuInicial Jogar}

reage (EventKey (SpecialKey KeyEnter) Down _ _) e@Estado {modo = MenuInicial Jogar} =
  return e {modo = EmJogo}

reage (EventKey (SpecialKey KeyEnter) Down _ _) e@Estado {modo = MenuInicial Sair} =
  exitFailure

reage (EventKey (SpecialKey KeyLeft) Down _ _) e@Estado {modo = EmJogo, jogo = jogo1} =
  return $ atualizaestado (Just AndarEsquerda) e

reage (EventKey (SpecialKey KeyLeft) Up _ _) e@Estado {modo= EmJogo,jogo = jogo1} = do
  return $ atualizaestado (Just Parar) e

reage (EventKey (SpecialKey KeyRight) Down _ _) e@Estado {modo = EmJogo, jogo = jogo1} =
  return $ atualizaestado (Just AndarDireita) e

reage (EventKey (SpecialKey KeyRight) Up _ _) e@Estado {modo= EmJogo,jogo = jogo1} = do
  return $ atualizaestado (Just Parar) e

reage (EventKey (SpecialKey KeySpace) Down _ _) e@Estado {modo = EmJogo, jogo = jogo1} =
  return $ atualizaestado (Just Saltar) e

reage (EventKey (SpecialKey KeyUp) Down _ _) e@Estado {modo= EmJogo,jogo = jogo1} = do
  return $ atualizaestado (Just Subir) e

reage (EventKey (SpecialKey KeyUp) Up _ _) e@Estado {modo= EmJogo,jogo = jogo1} = do
  return $ atualizaestado (Just Parar) e

reage (EventKey (SpecialKey KeyDown) Down _ _) e@Estado {modo= EmJogo,jogo = jogo1} = do
  return $ atualizaestado (Just Descer) e

reage (EventKey (SpecialKey KeyDown) Up _ _) e@Estado {modo= EmJogo,jogo = jogo1} = do
  return $ atualizaestado (Just Parar) e

reage (EventKey (SpecialKey KeyEsc) Down _ _) e@Estado {modo = EmJogo} =
  return e {modo = MenuInicial Jogar}

reage _ e = return e

janela :: Display
janela = FullScreen

corFundo :: Color
corFundo = greyN 0.0

frameRate :: Int
frameRate = 500

atualizaEstado :: Float -> Estado -> Estado --Atualiza o Estado com a nova posicao do Mario
atualizaEstado dt  e =
  let jogoAtualizado = atualizaPosicaoMario dt (jogo e)
  in e { jogo = jogoAtualizado }

tempo :: Float -> Estado -> IO Estado
tempo dt estadoAtual = do
  putStrLn $ "Before Update: " ++ show (posicao (jogador (jogo estadoAtual)))

  let estadoAtualizado = atualizaEstado dt estadoAtual
      jogoMovimentado = movimenta 2024 (realToFrac dt) (jogo estadoAtualizado)

  return estadoAtualizado { jogo = jogoMovimentado }

