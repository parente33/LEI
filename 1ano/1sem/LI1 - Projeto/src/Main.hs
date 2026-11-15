module Main where
import LI12324
import Tarefa1 
import Tarefa2
import Tarefa3
import Tarefa4
import Tarefa5
import Graphics.Gloss.Interface.IO.Game
import Graphics.Gloss

carregarImagens :: IO Imagens --Carrega todos os BMP 
carregarImagens = do
  alcapao <-loadBMP "alcapao.bmp"
  coracaopartido <- loadBMP "coracaopartido.bmp"
  coracao <- loadBMP "coracao.bmp"
  escadas <- loadBMP "escadas.bmp"
  estrela <- loadBMP "estrela.bmp"
  hammer <- loadBMP "hammer.bmp"
  mariohammer2 <- loadBMP "mario_bonk_standing.bmp"
  mariohammer2direita <- loadBMP "mario_bonk_standing_direita.bmp"
  mariodead <- loadBMP "mario_dead.bmp"
  marioescada <- loadBMP "mario_escada.bmp"
  marioescadadireita <- loadBMP "mario_escada_direita.bmp"
  mariohammerunning <- loadBMP "mario_hammer_running.bmp"
  mariohammerrunningdireita <- loadBMP "mario_hammer_running_direita.bmp"
  mariohammer <- loadBMP "mario_hammer_standing.bmp"
  mariohammerdireita <- loadBMP "mario_hammer_standing_direita.bmp"
  mariojumping <- loadBMP "mario_jumping.bmp"
  mariojumpingdireita <- loadBMP "mario_jumping_direita.bmp"
  mariorunning <- loadBMP "mario_running.bmp"
  mariorunningdireita <- loadBMP "mario_running_direita.bmp"
  mario <- loadBMP "mario_standing.bmp"
  mariodireita <- loadBMP "mario_standing_direita.bmp"
  macaco <- loadBMP "monke.bmp"
  plataforma <- loadBMP "plataforma.bmp"
  princesa <- loadBMP "princesa.bmp"
  playButton <- loadBMP "play_button.bmp"
  playButton2 <- loadBMP "amarelo.bmp"
  quitButton <- loadBMP "quit.bmp"
  quitButton2 <- loadBMP "amarelo2.bmp"
  menuBackground <- loadBMP "Background_direito.bmp"
  gameBackground <- loadBMP "game_background.bmp"
  empty <- loadBMP "empty.bmp"
  moeda <- loadBMP "moeda.bmp"
  fantasma <- loadBMP "fantasma.bmp"
  fantasmaDireita <- loadBMP "fantasmaDireita.bmp"
  vitoria <- loadBMP "vitoria.bmp"
  derrota <- loadBMP "derrota.bmp"
  let imgs = [(Derrota,derrota),(Vitoria,vitoria),(FantasmaDireitaBMP,fantasmaDireita),(FantasmaBMP,fantasma),(MoedaBMP,moeda),(EmptyBMP,empty),(Princesa,princesa),(PlataformaBMP,plataforma),(Macaco,macaco),(Mariodireita,mariodireita),(Mario,mario),(Mariorunningdireita,mariorunningdireita),(Mariorunning,mariorunning),(Mariojumpingdireita,mariojumpingdireita),(Mariojumping,mariojumping),(Mariohammerdireita,mariohammerdireita),(Mariohammer,mariohammer),(Mariohammerrunningdireita,mariohammerrunningdireita),(Mariohammerunning,mariohammerunning),(Marioescadadireita,marioescadadireita),(Marioescada,marioescada),(Mariodead,mariodead),(Mariohammer2direita,mariohammer2direita),(Mariohammer2,mariohammer2),(MarteloBMP,hammer),(EstrelaBMP,estrela),(EscadasBMP,escadas),(Coracao,coracao),(Coracaopartido,coracaopartido),(AlcapaoBMP, alcapao),(PlayButton, playButton), (MenuBackgroundImage, menuBackground), (GameBackgroundImage, gameBackground), (PlayButton2, playButton2), (Quit, quitButton), (Quit2, quitButton2)]
  return imgs

main :: IO ()
main = do
  imgs <- carregarImagens
  playIO
    janela
    corFundo
    frameRate
    (Estado {imagens = imgs, modo = MenuInicial Jogar, acao = Parar, jogo= jogo1})
    desenha
    reage
    tempo