\documentclass[11pt, a4paper, fleqn]{article}
\usepackage{cp2526t}
\makeindex

%================= lhs2tex=====================================================%
%include polycode.fmt
%%format (bin (n) (k)) = "\Big(\vcenter{\xymatrix@R=1pt{" n "\\" k "}}\Big)"
%format -|- = "+"
%format . = "\comp "
%format .* = "\star "
%format .&&&. = "\wedge"
%format .<=. = "\leq"
%format .<==>. = "\Leftrightarrow"
%format .=?=. = "\mathbin{\stackrel{\mathrm{?}}{=}}"
%format .==. = "\equiv"
%format .==>. = "\Rightarrow"
%format (ana (g)) = "\ana{" g "}"
%format (ana' (f) (g)) = "\lanabracket\;\!" f "\:" g "\:\!\ranabracket"
%format (anaForest (f) (g)) = "\lanabracket\;\!" f "\:" g "\:\!\ranabracket_{\textit{\tiny F}}"
%format (anaList (g)) = "\anaList{" g "}"
%format (anaLTree (g)) = "\lanabracket\," g "\,\ranabracket"
%format (anaStream (g)) = "\lanabracket\," g "\,\ranabracket"
%format (anaRose (g)) = "\lanabracket\," g "\,\ranabracket_\textit{\tiny R}"
%format (anaTree (f) (g)) = "\lanabracket\;\!" f "\:" g "\:\!\ranabracket_{\textit{\tiny T}}"
%format (cata (f)) = "\llparenthesis\, " f "\,\rrparenthesis"
%format (cata' (f) (g)) = "\llparenthesis\, " f "\:" g "\,\rrparenthesis"
%format (cataBTree (x)) = "\llparenthesis\, " x "\,\rrparenthesis"
%format (cataForest (f) (g)) = "\llparenthesis\, " f "\:" g "\,\rrparenthesis_{\textit{\tiny F}}"
%format (cataFTree (x)) = "\llparenthesis\, " x "\,\rrparenthesis"
%format (cataList (g)) = "\llparenthesis\, " g "\,\rrparenthesis"
%format (cataNat (g)) = "\cataNat{" g "}"
%format (cataRose (x)) = "\llparenthesis\, " x "\,\rrparenthesis_\textit{\tiny R}"
%format (cataTree (f) (g)) = "\llparenthesis\, " f "\:" g "\,\rrparenthesis_{\textit{\tiny T}}"
%format (const (f)) = "\underline{" f "}"
%format (Cp.cond (p) (f) (g)) = "\mcond{" p "}{" f "}{" g "}"
%format (curry (f)) = "\overline{" f "}"
%format (div (x)(y)) = x "\div " y
%format (either (a) (b)) = "\alt{" a "}{" b "}"
%format (fac (n)) = "{" n "!}"
%format (for (f) (i)) = "\for{" f "}\ {" i "}"
%format (frac (a) (b)) = "\frac{" a "}{" b "}"
%format (frac (n)(m)) = "\frac{" n "}{" m "}"
%format (hylo (g) (h)) = "\llbracket\, " g ",\," h "\,\rrbracket"
%format (hylo' (ft) (ff) (gt) (gf)) = "\llbracket\, " ft "\:" ff ",\," gt "\:" gf "\,\rrbracket"
%format (hyloForest (ft) (ff) (gt) (gf)) = "\llbracket\, " ft "\:" ff ",\," gt "\:" gf "\,\rrbracket_{\textit{\tiny F}}"
%format (hyloRose (g) (h)) = "\llbracket\, " g ",\," h "\,\rrbracket_\textit{\tiny R}"
%format (hyloTree (ft) (ff) (gt) (gf)) = "\llbracket\, " ft "\:" ff ",\," gt "\:" gf "\,\rrbracket_{\textit{\tiny T}}"
%format (kcomp (f)(g)) = f "\kcomp " g
%format (lcbr (x)(y)) = "\begin{lcbr}" x "\\" y "\end{lcbr}"
%format (lcbr3 (x)(y)(z)) = "\begin{lcbr}" x "\\" y "\\" z "\end{lcbr}"
%format (plus (f)(g)) = "{" f "}\plus{" g "}"
%format (Prod (a) (b)) = a >< b
%format (Seq (a)) = "{" a "}^{*}"
%format (split (x) (y)) = "\conj{" x "}{" y "}"
%format (square (x)) = x "^2"
%format (uncurry f) = "\uncurry{" f "}"
%format (underbrace (t) (p)) = "\underbrace{" t "}_{" p "}"
%format % = "\mathbin{/}"
%format `minusNat`= "\mathbin{-}"
%format `ominus` = "\mathbin{\ominus}"
%format ++ = "\mathbin{+\!\!+}"
%format <-> = "{\,\leftrightarrow\,}"
%format <|> = "{\,\updownarrow\,}"
%format <$> = "\mathbin{\mathopen{\langle}\$\mathclose{\rangle}}"
%format ==> = "\Longrightarrow "
%format ==> = "\Rightarrow"
%format >< = "\times"
%format >|<  = "\bowtie "
%format |-> = "\mapsto"
%format B_tree = "\mathsf{B}\mbox{-}\mathsf{tree} "
%format cdots = "\cdots "
%format conc = "\mathsf{conc}"
%format delta = "\Delta "
%format Dist = "\fun{Dist}"
%format Either a b = a "+" b
%format fF = "\fun F "
%format fmap = "\mathsf{fmap}"
%format fromRational = " from_\Q "
%format fst = "\p1"
%format FTree = "{\FTree}"
%format i1 = "i_1"
%format i2 = "i_2"
%format inForest = "\mathsf{in}_{Forest}"
%format inFTree = "\mathsf{in}"
%format inLTree = "\mathsf{in}"
%format inNat = "\mathsf{in}"
%format inT = "\mathsf{in}"
%format Integer  = "\mathbb{Z}"
%format inTree = "\mathsf{in}_{Tree}"
%format IO = "\fun{IO}"
%format l2 = "l_2 "
%format Left = "i_1"
%format length = "\length "
%format LTree = "{\LTree}"
%format map = "\map "
%format matrix = "matrix"
%format muB = "\mu "
%format NA   = "\textsc{na}"
%format Nat0 = "\N_0"
%format NB   = "\textbf{NB}"
%format Null = "1"
%format outForest = "\mathsf{out}_{Forest}"
%format outFTree = "\mathsf{out}"
%format outLTree = "\mathsf{out}"
%format outStream = "\mathsf{out}"
%format outT = "\mathsf{out}"
%format outTree = "\mathsf{out}_{Tree}"
%format p1  = "\p1"
%format p2  = "\p2"
%format pi = "\pi "
%format Rational = "\Q "
%format Right = "i_2"
%format snd = "\p2"
%format succ = "\succ "
%format summation = "{\sum}"
%format TLTree = "\mathsf{TLTree}"
%format toRational = " to_\Q "
%format t1 = "t_1 "
%format t2 = "t_2 "
%format t3 = "t_3 "
%format t4 = "t_4 "
%format t5 = "t_5 "
%------------------------------------------------------------------------------%


%====== DEFINIR GRUPO E ELEMENTOS =============================================%

\group{G21}
\studentA{107363}{Daniel Gonçalves Parente}
\studentB{106938}{Tiago Silva Martins}

%==============================================================================%

\begin{document}
\sffamily
\setlength{\parindent}{0em}
\emergencystretch 3em
\renewcommand{\baselinestretch}{1.25}
\input{Cover}
\pagestyle{pagestyle}

\newgeometry{left=25mm,right=20mm,top=25mm,bottom=25mm}
\setlength{\parindent}{1em}

\section*{Preâmbulo}

Em \CP\ pretende-se ensinar a progra\-mação de computadores
como uma disciplina científica. Para isso parte-se de um repertório de \emph{combinadores}
que formam uma álgebra da programação % (conjunto de leis universais e seus corolários)
e usam-se esses combinadores para construir programas \emph{composicionalmente},
isto é, agregando programas já existentes.

Na sequência pedagógica dos planos de estudo dos cursos que têm
esta disciplina, opta-se pela aplicação deste método à programação
em \Haskell\ (sem prejuízo da sua aplicação a outras linguagens
funcionais). Assim, o presente trabalho prático coloca os
alunos perante problemas concretos que deverão ser implementados em
\Haskell. Há ainda um outro objectivo: o de ensinar a documentar
programas, a validá-los e a produzir textos técnico-científicos de
qualidade.

Antes de abordarem os problemas propostos no trabalho, os grupos devem ler
com atenção o anexo \ref{sec:documentacao} onde encontrarão as instruções
relativas ao \emph{software} a instalar, etc.

Valoriza-se a escrita de \emph{pouco} código que corresponda a soluções
simples e elegantes que utilizem os combinadores de ordem superior estudados
na disciplina.

\noindent \textbf{Avaliação}. Faz parte da avaliação do trabalho a sua defesa
por parte dos elementos de cada grupo. Estes devem estar preparados para
responder a perguntas sobre \emph{qualquer} dos problemas deste enunciado.
A prestação \emph{individual} de cada aluno nessa defesa oral será uma componente
importante e diferenciadora da avaliação.

%if False
\begin{code}
{-# OPTIONS_GHC -XNPlusKPatterns #-}
{-# LANGUAGE GeneralizedNewtypeDeriving, DeriveDataTypeable, FlexibleInstances #-}
module Main where
import Cp
import List hiding (fac)
import Nat hiding (aux)
import LTree hiding (merge)
import BTree
-- import Exp
import Probability
-- import Svg hiding (for,dup,fdiv)
import Data.Char
import Data.Ratio
import Data.List hiding (find)
import Control.Monad
-- import Control.Monad.State
import Control.Applicative hiding ((<|>),empty)
import System.Process
import Control.Concurrent

main = undefined
\end{code}
%endif

\Problema

Uma serialização (ou travessia) de uma árvore é uma sua representação sob a forma de uma lista.
Na biblioteca |BTree| encontram-se as funções de serialização |inordt|, |preordt| e |postordt|,
que fazem as travessias \emph{in-order}, \emph{ pre-order} e \emph{post-order}, respectivamente.
Todas essas travessias são catamorfismos que percorrem a árvore argumento em regime \emph{depth-first}.

Pretende-se agora uma função |bforder| que faça a travessia em regime \emph{breadth-first},
isto é, por níveis.
Por exemplo, para a árvore |t1| dada em anexo e mostrada na figura a seguir,

\begin{center}
	\figura
\end{center}

\noindent a função deverá dar a lista

\begin{spec}
	[5,3,7,1,4,6,8]
\end{spec}

\noindent em que se vê como os níveis |5|, depois |3,7| e finalmente |1,4,6,8| foram percorridos.

Pretendemos propor duas versões dessa função:

\begin{enumerate}
\item	Uma delas envolve um catamorfismo de |BTree|s:
\begin{code}
bfsLevels :: BTree a -> [a]
bfsLevels = concat . levels
\end{code}
Complete a definição desse catamorfismo:
\begin{code}
levels :: BTree a -> [[a]]
levels = cataBTree glevels
\end{code}
\item A segunda proposta,
\begin{code}
bft :: BTree a -> [a]
\end{code}
deverá basear-se num anamorfismo de listas.
\end{enumerate}
\textbf{Sugestão}: estudar o artigo \cite{Ok00} cujo PDF está incluído no material deste trabalho.
Quando fizer testes ao seu código pode, se desejar, usar funções disponíveis na biblioteca
|Exp| para visualizar as árvores em GraphViz (formato .dot).

Justifique devidamente a sua resolução, que deverá vir acompanhada de diagramas explicativos.
Como já se disse, valoriza-se a escrita de \emph{pouco} código que corresponda a soluções
simples e elegantes que utilizem os combinadores de ordem superior estudados
na disciplina.



\Problema

Considere a seguinte função em Haskell:
\begin{quote}
\begin{code}
f x = wrapper . worker where
         wrapper = head
         worker 0 = start x
         worker(n+1) = loop x (worker n)

loop x    [s,         h,       k,     j,     m     ] =
          [h / k + s, x^2 * h, k * j, j + m, m + 8 ]

start x = [x,         x^3,     6,     20,    22    ]
\end{code}
\end{quote}
Pode-se provar pela lei de recursividade mútua que |f x n| calcula o seno hiperbólico de |x|,
|sinh x|, para |n| aproximações da sua série de Taylor.
Faça a derivação da função dada a partir da referida série de Taylor, apresentando todos os cálculos justificativos, tal como se faz para outras funções no capítulo respectivo do texto base desta UC \cite{Ol98-24}.

\Problema

Quem em Braga observar, ao fim da tarde, o tráfego onde a Avenida Clairmont
Fernand se junta à N101, aproximadamente na coordenada \href{https://maps.app.goo.gl/uCbXLsdibYoochr36}{41°33'46.8"N
8°24'32.4"W} --- ver as setas da figura que se segue --- reparará nas sequências
imparáveis (infinitas!) de veículos provenientes dessas vias de circulação.

Mas também irá observar um comportamento interessante por parte dos condutores desses
veículos: por regra, \emph{cada carro numa via deixa passar, à sua frente, exactamente outro carro da outra via}.

\begin{center}
	\mapa
\end{center}

Este comportamento \emph{civilizado} chama-se \emph{fair-merge} (ou \emph{fair-interleaving})
de duas sequências infinitas, também designadas \emph{streams} em ciência
da computação. Seja dado o tipo dessas sequências em Haskell,
\begin{code}
data Stream a = Cons (a, Stream a) deriving Show
\end{code}
para o qual se define também:
\begin{code}
outStream (Cons (x,xs)) = (x,xs)
\end{code}
\noindent O referido comportamento civilizado pode definir-se, em Haskell,
da forma seguinte:\footnote{O facto das sequências serem infinitas não nos
deve preocupar, pois em Haskell isso é lidado de forma transparente por \lazy{lazy
evaluation}.}
\begin{code}
fair_merge :: Either (Stream a, Stream a) (Stream a, Stream a) -> Stream a
fair_merge = either h k where
   h (Cons(x,xs), y) = Cons(x , k(xs,y))
   k (x, Cons(y,ys)) = Cons(y , h(x,ys))
\end{code}

Defina |fair_merge| como um \textbf{anamorfismo} de |Stream|s, usando o combinador
\begin{code}
anaStream g = Cons . (id >< (anaStream g)) . g
\end{code}
e a seguinte estratégia:
\begin{itemize}
\item	Derivar a lei \textbf{dual} da recursividade mútua,
\begin{eqnarray}
	|either f g = ana(either h k)| & \equiv & |lcbr (out . f = fF (either f g) . h)(out . g = fF (either f g) . k)|
	\label{eq:fokkinga_dual}
\end{eqnarray}
	tal como se fez, nas aulas, para a que está no formulário.
\item
	Usar (\ref{eq:fokkinga_dual}) na resolução do problema proposto.
\end{itemize}
Justificar devidamente a resolução, que deverá vir acompanhada de diagramas explicativos.

\Problema

Como se sabe, é possível pensarmos em catamorfismos, anamorfismos etc \emph{probabilísticos},
quer dizer, programas recursivos que dão distribuições como resultados. Por
exemplo, podemos pensar num combinador
\begin{code}
pcataList :: (Either () (a, b) -> Dist b) -> [a] -> Dist b
\end{code}
que é muito parecido com
\begin{spec}
cataList :: (Either () (a, b) -> b) -> [a] -> b
\end{spec}
da biblioteca \List. A principal diferença é que o gene de |pcataList| é uma função probabilística.

Como exemplo de utilização, recorde-se que |cataList (either zero add)| soma todos
os elementos da lista argumento, por exemplo:
\begin{quote}
|cataList (either zero add) [20,10,5] = 35|.
\end{quote}
Considere-se agora a função |padd| (adição probabilística) que,
com probabilidade $90\%$ soma dois números e com probabilidade $10\%$ os subtrai:
\begin{code}
padd(a,b) = D [(a+b,0.9),(a-b,0.1)]
\end{code}
Se se correr
\begin{code}
d4 = pcataList (either pzero padd) [20,10,5] where pzero = return . zero
\end{code}
obter-se-á:
\begin{Verbatim}[fontsize=\small]
35  81.0%
25   9.0%
 5   9.0%
15   1.0%
\end{Verbatim}

Com base neste exemplo, resolva o seguinte
\begin{quote}\em
\textbf{Problema}: Uma unidade militar pretende enviar uma mensagem urgente
a outra, mas tem o aparelho de telegrafia meio avariado. Por experiência,
o telegrafista sabe que a probabilidade de uma palavra se perder (não ser
transmitida) é $5\%$; e que, no final de cada mensagem, o aparelho envia o código
|"stop"|, mas (por estar meio avariado), falha $10\%$ das vezes.

Qual a probabilidade de a palavra |"atacar"| da mensagem
\begin{quote}
|words "Vamos atacar hoje"|
\end{quote}
se perder, isto é, o resultado da transmissão ser |["Vamos","hoje","stop"]|?
E a de seguirem todas as palavras, mas faltar o |"stop"| no fim? E a da transmissão
ser perfeita?
\end{quote}

Responda a estas perguntas encontrando |gene| tal que
\begin{code}
transmitir = pcataList gene
\end{code}
descreve o comportamento do aparelho.
Justificar devidamente a resolução, que deverá vir acompanhada de diagramas explicativos.
%

\part*{Anexos}

\appendix

\section{Natureza do trabalho a realizar}
\label{sec:documentacao}
Este trabalho teórico-prático deve ser realizado por grupos de 3 alunos.
Os detalhes da avaliação (datas para submissão do relatório e sua defesa
oral) são os que forem publicados na \cp{página da disciplina} na \emph{internet}.

Recomenda-se uma abordagem participativa dos membros do grupo em \textbf{todos}
os exercícios do trabalho, para assim poderem responder a qualquer questão
colocada na \emph{defesa oral} do relatório.

Para cumprir de forma integrada os objectivos do trabalho vamos recorrer
a uma técnica de programa\-ção dita ``\litp{literária}'' \cite{Kn92}, cujo
princípio base é o seguinte:
%
\begin{quote}\em
        Um programa e a sua documentação devem coincidir.
\end{quote}
%
Por outras palavras, o \textbf{código fonte} e a \textbf{documentação} de um
programa deverão estar no mesmo ficheiro.

O ficheiro \texttt{cp2526t.pdf} que está a ler é já um exemplo de
\litp{programação literária}: foi gerado a partir do texto fonte
\texttt{cp2526t.lhs}\footnote{O sufixo `lhs' quer dizer
\emph{\lhaskell{literate Haskell}}.} que encontrará no \MaterialPedagogico\
desta disciplina des\-com\-pactando o ficheiro \texttt{cp2526t.zip}.

Como se mostra no esquema abaixo, de um único ficheiro (|lhs|)
gera-se um PDF ou faz-se a interpretação do código \Haskell\ que ele inclui:

        \esquema

Vê-se assim que, para além do \GHCi, serão necessários os executáveis \PdfLatex\ e
\LhsToTeX. Para facilitar a instalação e evitar problemas de versões e
conflitos com sistemas operativos, é recomendado o uso do \Docker\ tal como
a seguir se descreve.

\section{Docker} \label{sec:docker}

Recomenda-se o uso do \container\ cuja imagem é gerada pelo \Docker\ a partir do ficheiro
\texttt{Dockerfile} que se encontra na diretoria que resulta de descompactar
\texttt{cp2526t.zip}. Este \container\ deverá ser usado na execução
do \GHCi\ e dos comandos relativos ao \Latex. (Ver também a \texttt{Makefile}
que é disponibilizada.)

Após \href{https://docs.docker.com/engine/install/}{instalar o Docker} e
descarregar o referido zip com o código fonte do trabalho,
basta executar os seguintes comandos:
\begin{Verbatim}[fontsize=\small]
    $ docker build -t cp2526t .
    $ docker run -v ${PWD}:/cp2526t -it cp2526t
\end{Verbatim}
\textbf{NB}: O objetivo é que o container\ seja usado \emph{apenas}
para executar o \GHCi\ e os comandos relativos ao \Latex.
Deste modo, é criado um \textit{volume} (cf.\ a opção \texttt{-v \$\{PWD\}:/cp2526t})
que permite que a diretoria em que se encontra na sua máquina local
e a diretoria \texttt{/cp2526t} no \container\ sejam partilhadas.

Pretende-se então que visualize/edite os ficheiros na sua máquina local e que
os compile no \container, executando:
\begin{Verbatim}[fontsize=\small]
    $ lhs2TeX cp2526t.lhs > cp2526t.tex
    $ pdflatex cp2526t
\end{Verbatim}
\LhsToTeX\ é o pre-processador que faz ``pretty printing'' de código Haskell
em \Latex\ e que faz parte já do \container. Alternativamente, basta executar
\begin{Verbatim}[fontsize=\small]
    $ make
\end{Verbatim}
para obter o mesmo efeito que acima.

Por outro lado, o mesmo ficheiro \texttt{cp2526t.lhs} é executável e contém
o ``kit'' básico, escrito em \Haskell, para realizar o trabalho. Basta executar
\begin{Verbatim}[fontsize=\small]
    $ ghci cp2526t.lhs
\end{Verbatim}

\noindent Abra o ficheiro \texttt{cp2526t.lhs} no seu editor de texto preferido
e verifique que assim é: todo o texto que se encontra dentro do ambiente
\begin{quote}\small\tt
\verb!\begin{code}!
\\ ... \\
\verb!\end{code}!
\end{quote}
é seleccionado pelo \GHCi\ para ser executado.

\section{Em que consiste o TP}

Em que consiste, então, o \emph{relatório} a que se referiu acima?
É a edição do texto que está a ser lido, preenchendo o anexo \ref{sec:resolucao}
com as respostas. O relatório deverá conter ainda a identificação dos membros
do grupo de trabalho, no local respectivo da folha de rosto.

Para gerar o PDF integral do relatório deve-se ainda correr os comando seguintes,
que actualizam a bibliografia (com \Bibtex) e o índice remissivo (com \Makeindex),
\begin{Verbatim}[fontsize=\small]
    $ bibtex cp2526t.aux
    $ makeindex cp2526t.idx
\end{Verbatim}
e recompilar o texto como acima se indicou. (Como já se disse, pode fazê-lo
correndo simplesmente \texttt{make} no \container.)

No anexo \ref{sec:codigo} disponibiliza-se algum código \Haskell\ relativo
aos problemas que são colocados. Esse anexo deverá ser consultado e analisado
à medida que isso for necessário.

Deve ser feito uso da \litp{programação literária} para documentar bem o código que se
desenvolver, em particular fazendo diagramas explicativos do que foi feito e
tal como se explica no anexo \ref{sec:diagramas} que se segue.

\section{Como exprimir cálculos e diagramas em LaTeX/lhs2TeX} \label{sec:diagramas}

Como primeiro exemplo, estudar o texto fonte (\lhstotex{lhs}) do que está a ler\footnote{
Procure e.g.\ por \texttt{"sec:diagramas"}.} onde se obtém o efeito seguinte:\footnote{Exemplos
tirados de \cite{Ol98-24}.}
\begin{eqnarray*}
\start
|
        id = split f g
|
\just\equiv{ universal property }
|
     lcbr(
          p1 . id = f
     )(
          p2 . id = g
     )
|
\just\equiv{ identity }
|
     lcbr(
          p1 = f
     )(
          p2 = g
     )
|
\qed
\end{eqnarray*}

Os diagramas podem ser produzidos recorrendo à \emph{package} \Xymatrix, por exemplo:
\begin{eqnarray*}
\xymatrix@@C=2cm{
    |Nat0|
           \ar[d]_-{|cataNat g|}
&
    |1 + Nat0|
           \ar[d]^{|id + (cataNat g)|}
           \ar[l]_-{|inNat|}
\\
     |B|
&
     |1 + B|
           \ar[l]^-{|g|}
}
\end{eqnarray*}

\section{O mónade das distribuições probabilísticas} \label{sec:probabilities}
%format B = "\mathit B"
%format C = "\mathit C"
Mónades são functores com propriedades adicionais que nos permitem obter
efeitos especiais em progra\-mação. Por exemplo, a biblioteca \Probability\
oferece um mónade para abordar problemas de probabilidades. Nesta biblioteca,
o conceito de distribuição estatística é captado pelo tipo
\begin{eqnarray}
     |newtype Dist a = D {unD :: [(a, ProbRep)]}|
     \label{eq:Dist}
\end{eqnarray}
em que |ProbRep| é um real de |0| a |1|, equivalente a uma escala de $0$ a
$100 \%$.

Cada par |(a,p)| numa distribuição |d::Dist a| indica que a probabilidade
de |a| é |p|, devendo ser garantida a propriedade de  que todas as probabilidades
de |d| somam $100\%$.
Por exemplo, a seguinte distribuição de classificações por escalões de $A$ a $E$,
\[
\begin{array}{ll}
A & \rule{2mm}{3pt}\ 2\%\\
B & \rule{12mm}{3pt}\ 12\%\\
C & \rule{29mm}{3pt}\ 29\%\\
D & \rule{35mm}{3pt}\ 35\%\\
E & \rule{22mm}{3pt}\ 22\%\\
\end{array}
\]
será representada pela distribuição
\begin{code}
d1 :: Dist Char
d1 = D [('A',0.02),('B',0.12),('C',0.29),('D',0.35),('E',0.22)]
\end{code}
que o \GHCi\ mostrará assim:
\begin{Verbatim}[fontsize=\small]
'D'  35.0%
'C'  29.0%
'E'  22.0%
'B'  12.0%
'A'   2.0%
\end{Verbatim}
É possível definir geradores de distribuições, por exemplo distribuições \emph{uniformes},
\begin{code}
d2 = uniform (words "Uma frase de cinco palavras")
\end{code}
isto é
\begin{Verbatim}[fontsize=\small]
     "Uma"  20.0%
   "cinco"  20.0%
      "de"  20.0%
   "frase"  20.0%
"palavras"  20.0%
\end{Verbatim}
distribuição \emph{normais}, eg.\
\begin{code}
d3 = normal [10..20]
\end{code}
etc.\footnote{Para mais detalhes ver o código fonte de \Probability, que é uma adaptação da
biblioteca \PFP\ (``Probabilistic Functional Programming''). Para quem quiser saber mais
recomenda-se a leitura do artigo \cite{EK06}.}
|Dist| forma um \textbf{mónade} cuja unidade é |return a = D [(a,1)]| e cuja composição de Kleisli
é (simplificando a notação)
\begin{spec}
  ((kcomp f g)) a = [(y,q*p) | (x,p) <- g a, (y,q) <- f x]
\end{spec}
em que |g: A -> Dist B| e |f: B -> Dist C| são funções \textbf{monádicas} que representam
\emph{computações probabilísticas}.

Este mónade é adequado à resolução de problemas de \emph{probabilidades e estatística} usando programação funcional, de forma elegante e como caso particular da programação monádica.

\section{Código fornecido}\label{sec:codigo}

\subsection*{Problema 1}

Árvores exemplo:
\begin{code}
t1 :: BTree Int
t1 = Node (5,(Node (3,(Node (1,(Empty,Empty)),Node (4,(Empty,Empty)))),
           Node (7,(Node (6,(Empty,Empty)),Node (8,(Empty,Empty))))))

t2 :: BTree Int
t2 =
  node 1
    (node 2 (node 4 Empty Empty) (node 5 Empty Empty))
    (node 3 (node 6 Empty Empty) (node 7 Empty Empty))

t3 :: BTree Char
t3 =
  node 'A'
    (node 'B' (node 'C' (node 'D' Empty Empty) Empty) Empty)
    (node 'E' Empty Empty)

t4 :: BTree Char
t4 =
  node 'A'
    (node 'B' (node 'C' (node 'D' Empty Empty) Empty) Empty)
    Empty

t5 :: BTree Int
t5 =
  node 1
   (node 2 (node 4 Empty Empty) Empty)
   (node 3 Empty (node 5 (node 6 Empty Empty) Empty))

node a b c = Node (a,(b,c))
\end{code}

%----------------- Soluções dos alunos -----------------------------------------%

\section{Soluções dos alunos}\label{sec:resolucao}
Os alunos devem colocar neste anexo as suas soluções para os exercícios
propostos, de acordo com o ``layout'' que se fornece.
Não podem ser alterados os nomes ou tipos das funções dadas, mas pode ser
adicionado texto ao anexo, bem como diagramas e/ou outras funções auxiliares
que sejam necessárias.

\noindent
\textbf{Importante}: Não pode ser alterado o texto deste ficheiro fora deste anexo.

%----------------- Introducao ao Problema 1 -----------------------------------------%

\subsection*{Problema 1}

\subsection*{Problema 1: Travessia Breadth-First de uma Árvore}

\subsubsection*{Introdução ao Problema}

\noindent Quando temos uma estrutura de árvore em programação, podemos fazer a sua travessia, que consiste em representar essa estrutura de dados numa lista.
Existem as funções de travessia \textit{inordt}, \textit{preordt} e \textit{postordt} na biblioteca BTree que fazem as travessias \textit{in-order}, \textit{pre-order} e \textit{post-order} de uma árvore, respetivamente.
No entanto, todas essas travessias são catamorfismos que são feitos em regime \textit{depth-first}, isto é, percorrem a árvore em profundidade.

\noindent Pretende-se então desenvolver um função \textit{bforder} que faça esta travessia em regime \textit{breadth-first}, isto é, na horizontal.
Dada a árvore \textit{t1} acima representada, a função deverá ter como output a lista $[5, 3, 7, 1, 4, 6, 8]$.

%----------------- Catamorfismo -----------------------------------------%

\subsubsection*{Abordagem 1: Catamorfismo}

\noindent O grande obstáculo de tentar resolver este problema com um catamorfismo simples, do tipo $BTree \ A \to [A]$, é que essa abordagem perde a noção de profundidade.
Ao percorrermos a árvore, o catamorfismo não tem acesso a um contexto global que lhe diga em que nível está cada nó.

\noindent Para compreendermos a mecânica desta solução, vamos visualizar o processo em duas etapas.

\noindent Primeiramente, em vez da recursividade devolver logo uma lista plana com todos os níveis da árvore misturados, precisamos que ela devolva uma estrutura que os mantenha separados.
Usaremos então como resultado intermédio uma lista de listas, do tipo $[[A]]$.
Desta forma, organizamos os níveis da árvore por índice: a primeira lista contém a raiz, a segunda contém os filhos, a terceira contêm os netos, e assim sucessivamente, onde o índice da lista corresponde exatamente à profundidade na árvore.

\noindent Este processamento é feito pelo catamorfismo \textit{levels}, cuja missão será essa, converter a estrutura vertical da árvore numa lista de listas ($[[A]]$), onde cada sub-lista agrupa exclusivamente os nós que habitam a mesma profundidade.

\noindent Vejamos isto em prática com a árvore $t_1$. Em vez de misturar os níveis, a função \textit{levels} preserva-os:
$$\textit{levels}\ t_1 = [[5], [3, 7], [1, 4, 6, 8]]$$

\noindent A função principal \textit{bfsLevels} apenas recebe a estrutura organizada produzida pelo catamorfismo \textit{levels} e faz um \textit{concat}.
Isto faz com que a estrutura de níveis desapareça, deixando apenas a ordem em que os nós da árvore foram visitados.
Ao aplicar a função \textit{bfsLevels} ao exemplo anterior obtemos o seguinte resultado:
$$\textit{bfsLevels}\ t_1 = [5, 3, 7, 1, 4, 6, 8]$$

%----------------- diagrama comutativo -----------------------------------------%

\noindent O catamorfismo \textit{levels} é definido pelo diagrama comutativo seguinte, onde $g$ representa o seu gene, ou seja, o processo a ser feito em cada passo da sua recursão:

\begin{equation*}
\xymatrix{
    \mathsf{BTree}\ A \ar[d]_{\mathsf{levels}} \ar[r]^-{out} & 1 + A \times (\mathsf{BTree}\ A \times \mathsf{BTree}\ A) \ar[d]^{id + id \times (\mathsf{levels} \times \mathsf{levels})} \\
    [[A]] & 1 + A \times ([[A]] \times [[A]]) \ar[l]^-{g}
}
\end{equation*}

\noindent Podemos decompor o gene num co-produto de duas funções $g = [g_1, g_2]$ para cada caso possível: o caso base em que recebe um árvore vazia ou o caso recursivo em que recebe o nó onde se encontra e a lista de níveis das sub-árvores esquerda e da direita.

\begin{itemize}
    \item \textbf{Caso Base ($g_1$):} No caso de uma árvore vazia ((\textit{Empty})) não há níveis então o resultado é a lista vazia:
    $$ g_1 = \mathsf{nil} $$

    \item \textbf{Caso Recursivo ($g_2$):} Caso em que a árvore é um nó \textit{Node (a, (l, r))},
    \begin{enumerate}
        \item O nível atual (cabeça da lista do resultado) contém apenas o nó atual, $a$.
        \item Os níveis restantes são o resultado da fusão das subárvores $l$ e $r$ ao nível dos seus índices.
        Por exemplo se $l = [[1], [2, 3]]$ e $r = [[6], [7, 8]]$ então a fusão será igual a $[[1,6],[2,3,7,8]]$
    \end{enumerate}
\end{itemize}

\noindent Esta fusão exige uma função auxiliar, que designaremos por $\oplus$ (no código, corresponde à função \texttt{merge}), que combina duas listas de listas concatenando os seus elementos ao mesmo nível.

\noindent A definição formal do gene torna-se:
$$ g_2 (a, (l_S, r_S)) = [a] : (l_S \oplus r_S) $$


\subsubsection*{Implementação em Haskell}

A tradução direta da derivação acima resulta no seguinte código:

\begin{code}
-- Definição do gene g = [nil, g2]
glevels :: Either () (a, ([[a]], [[a]])) -> [[a]]
glevels = either (const []) step
    where
        -- step corresponde a g2
        step (a, (ls, rs)) = [a] : merge ls rs

-- Função auxiliar de fusão ponto-a-ponto (operador o-plus)
merge :: [[a]] -> [[a]] -> [[a]]
merge [] ys = ys
merge xs [] = xs
merge (x:xs) (y:ys) = (x ++ y) : merge xs ys
\end{code}

\noindent A função \texttt{merge} garante que os níveis da árvore se combinem corretamente.

\noindent Se uma sub-árvore for mais profunda que a outra, os níveis excedentes são simplesmente preservados (casos base da recursão em \texttt{merge}).

%----------------------------------------- Anamorfismo -----------------------------------------%

\subsubsection*{Anamorfismo}

\noindent Enquanto que a solução usando o catamorfismo dependia da estrutura intermédia ($[[A]]$) para separar os níveis e depois juntá-los na mesma lista, podemos,
com o anamorfismo, vamos gerar a lista de travessia ($[A]$) diretamente, recorrendo ao uso de uma fila de espera do tipo (\textit{queue}).
Esta abordagem vai-nos permitir otimizar o consumo de memória em árvores grandes.
O estado interno do anorfismo, isto é, a sua "semente", que vai corresponder à fila de espera será uma lista de árvores ($\mathsf{[BTree\ A]}$) que vai representar a fila de nós pendentes para visita.
Esta solução seguirá então uma lógica FIFO (\textit{First-In-First-Out}):

\begin{enumerate}
    \item \textbf{Consumo:} O anamofismo consome a cabeça da lista de estado, ou seja, o próximo elemento na fila.
    \item \textbf{Produção:} Se este for válido, o seu valor é adicionado à lista de resultados.
    \item \textbf{Agendamento:} Para garantir que são processados todos os nós do nível atual antes das sub-árvores do nó que estamos a tratar, estas são adicionadas ao final da fila, primeiro a da esquerda e depois a da direita.
\end{enumerate}

\noindent Vamos ver o que acontece se aplicarmos estas regras à árvore $t_1$.
Com a raiz da árvore na fila (o nó 5), o gene do anamorfismo consome o seu valor, e emite-o para a saída. Os seus filhos (nós 3 e 7) são adicionados à fila, que passa a ser a lista $[3, 7]$.
Na iteração seguinte, consome o nó 3, emite-o para a lista de resultados e adiciona os seus filhos (1 e 4) ao final da fila, passando esta a ser a lista $[7, 1, 4]$.
Assim, o nó 7, que está no mesmo nível que o nó 3, será processado antes do 1 e do 4 que estão no nível seguinte.

\noindent O anamorfismo é definido pelo diagrama dual ao do catamorfismo:
\begin{equation*}
\xymatrix {
    [A] & 1 + A \times [A] \ar[l]^-{in} \\
    [\mathsf{BTree}\ A] \ar[u]_{bft} \ar[r]^-{g_{bft}} & 1 + A \times [\mathsf{BTree}\ A] \ar[u]_{id + id \times bft}
}
\end{equation*}

\noindent Este diagrama é o dual do diagrama do catamorfismo, obtido pela inversão do sentido das setas e pela substituição do morfismo \textit{out} pelo morfismo \textit{in} associado ao functor base das BTrees, de acordo com a lei universal dos anamorfismos.

\noindent O gene definido por $g_{bft}$ implementa esta fila de espera e tem de lidar com 3 casos diferentes:

\begin{itemize}
    \item \textbf{Fila Vazia:} Se a fila de espera for vazia (`[]`), não há mais nós para visitar e o anamorfismo termina (`i1 ()`).

    \item \textbf{Nó Vazio na Fila:} Se a cabeça da fila for uma árvore `Empty`, esta não tem qualquer informação ou filhos, então deve ser descartada e o processo deve continuar sem gerar algum resultado nesta etapa.

    \item \textbf{Nó com Valor:} Se a cabeça da fila for `Node (a, (l, r))`, o gene coloca o valor `a` na lista de resultados e devolve a fila de espera concatenada com `[l, r]` como novo estado.
\end{itemize}

\noindent No caso geral temos a definição do gene seguinte:
$$g_{bft} (Node(a, (l, r)) : ts) = (a, ts \mathbin{+\!\!+} [l, r])$$

\noindent Assim o anamorfismo é inicializado com uma lista contendo a árvore raiz, seguida da definição do gene seguinte:

\begin{code}
bft t = anaList g_bft [t]

g_bft :: [BTree a] -> Either () (a, [BTree a])
g_bft [] = i1 ()
g_bft (Empty : ts) = g_bft ts
g_bft (Node (a, (l, r)) : ts) = i2 (a, ts ++ [l, r])
\end{code}

\noindent Note-se o uso de \texttt{++ [l,r]} na última cláusula.
É esta operação de colocar os filhos no fim da lista que altera a natureza da travessia de \textit{Stack} (pilha/profundidade) para \textit{Queue} (fila/largura).

%----------------- Fim do Problema 1 -----------------------------------------%

\subsection*{Problema 2}

\subsection*{Problema 2: Derivação do Seno Hiperbólico}

%----------------- Introducao ao Problema 2 -----------------------------------------%

\subsubsection*{Contextualização e Definição da Série}

\noindent Matematicamente, a função $\sinh x$ é definida pela soma infinita seguinte:

\begin{equation*}
\sinh x = \sum_{n=0}^{\infty} \frac{x^{2n+1}}{(2n+1)!} = x + \frac{x^3}{3!} + \frac{x^5}{5!} + \frac{x^7}{7!} + \dots
\end{equation*}

\noindent Tentar implementar esta fórmula diretamente, ou seja, para todos os termos calcular independentemente as potências e os fatoriais, seria muito ineficiente a nível computacional.
O custo das operações iria crescer exponencialmente com $n$.
No enunciado é-nos dada a função f x n que calcula o seno hiperbólico de x, ($\sinh x$), para n aproximações da sua série de Taylor.

\noindent Temos então como objetivo derivar essa função dada pela Lei da Recursividade Mútua.

\subsubsection*{Análise Algébrica e Relações de Recorrência}

\noindent Para podermos aplicar a Lei da Recursividade Mútua temos de expressar o termo $n+1$ apenas em função do termo $n$ e assim eliminámos a dependência do índice de iteração.

\noindent Sendo $s_n$ a soma dos primeiros $n$ termos, o próximo termo será definido como:
$$ s_{n+1} = s_n + t_{n+1} $$
onde $t_n = \frac{h_n}{k_n}$ será o termo a adicionar, composto por um numerador $h_n$ (potência de $x$) e um denominador $k_n$ (fatorial).


\noindent \textbf{1. O Numerador ($h$):}

\noindent O termo geral da série de Taylor tem como numerador para o seno hiperbólico a seguinte expressão:
\begin{equation}
    h_i = x^{2i+1}
\end{equation}

\noindent Procuramos então uma definição recursiva de $h_{i+1}$ em função de $h_i$.
Substituindo o $i$ por $i+1$ temos a seguinte expressão:

\begin{equation}
    h_{i+1} = x^{2(i+1)+1}
\end{equation}

\noindent Aplicamos a propriedade distributiva no expoente:
\begin{equation}
    h_{i+1} = x^{2i + 2 + 1}
\end{equation}

\noindent Se reagruparmos os termos $2i + 1$ temos a definição de $h_i$:
\begin{equation}
    h_{i+1} = x^{(2i + 1) + 2}
\end{equation}

\noindent Aplicamos a lei da soma das potências ($x^{a+b} = x^a \cdot x^b$) e obtemos:
\begin{equation}
    h_{i+1} = \underbrace{x^{2i+1}}_{h_i} \cdot x^2
\end{equation}

\noindent Portanto, temos que, para avançar um passo na série, basta multiplicar o numerador atual pelo quadrado de $x$:
\begin{equation} \label{eq:numerador_final}
    h_{i+1} = h_i \cdot x^2
\end{equation}

\noindent Provámos assim a linha de código \texttt{x\^{}2 * h} na função \texttt{loop}.

\noindent Para completar é necessário estabelecer o valor inicial, ou seja o valor de h no vetor \texttt{start}.
Relembramos que h representa o numerador do próximo termo a adicionar. Como o primeiro item já está na soma (o termo s armazena a soma acumulada, que em $h_0$ é x), temos em $h_1$:

\begin{equation}
    h_{start} = h_1 = x^{2(1)+1} = x^3
\end{equation}

\noindent Provámos então as duas primeiras posições no vetor \texttt{start}.

\noindent \textbf{2. O Denominador ($k$):}

\noindent O termo geral da série de Taylor tem como denominador para o seno hiperbólico a seguinte expressão:
\begin{equation}
    k_i = (2i+1)!
\end{equation}

\noindent Como no numerador, queremos definir $k_{i+1}$ recursivamente a partir de $k_i$. Substituindo então os termos obtemos a expressão seguinte:
\begin{equation}
    k_{i+1} = (2(i+1)+1)! = (2i+3)!
\end{equation}

\noindent Como $n! = n \times (n-1)!$ podemos decompor $(2i+3)!$ até encontrarmos o termo $(2i+1)!$:
\begin{equation}
    k_{i+1} = (2i+3) \cdot (2i+2) \cdot \underbrace{(2i+1)!}_{k_i}
\end{equation}

\noindent E chegamos à seguinte expressão:
\begin{equation} \label{eq:denominador_base}
    k_{i+1} = k_i \cdot q(i) \quad \text{onde} \quad q(i) = (2i+3)(2i+2)
\end{equation}

\noindent No código, a variável $q(i)$ é mapeada para $j$, justificando assim a linha de código \texttt{k * j}.

\noindent Para calcular $j_{start}$, usamos a fórmula de $q(i)$ com $i=1$:
\begin{equation}
    j_{start} = q(1) = (2(1)+3)(2(1)+2) = 5 \times 4 = 20
\end{equation}

\noindent Tal como no numerador, a função \texttt{start} prepara-se para o cálculo do segundo termo ($i=1$).
Dessa forma, a variável $k$ é inicializada com o valor de $k_1$, e não de $k_0$:
\begin{equation}
    k_{start} = k_1 = (2(1)+1)! = 3! = 6
\end{equation}

\noindent justificando assim o valor \texttt{6} e \texttt{20} no vetor \texttt{start}.

\subsubsection*{Eliminação da Dependência do Índice (Variáveis Auxiliares)}
\noindent Como $q(i)$ ainda depende do índice de iteração usaremos o cálculo de diferenças finitas. Ao contrário das derivadas do cálculo diferencial em que analisamos uma linha contínua, olhamos apenas para a diferença entre o valor atual e o próximo.
\begin{equation}
    \Delta f(i) = f(i+1) - f(i)
\end{equation}

\noindent \textbf{1. A Primeira Diferença ($\Delta q$):}

\noindent Se desenvolvermos o produto do termo $q(i)$ obtemos um polinómio quadrático (de grau 2):
\begin{equation}
    q(i) = (2i+3)(2i+2) = 4i^2 + 4i + 6i + 6 = 4i^2 + 10i + 6
\end{equation}

\noindent De seguida calculámos uma expressão para o ($\Delta q$):
\begin{align*}
    \Delta q(i) &= q(i+1) - q(i) \\
    &= 4(i+1)^2 + 10(i+1) + 6 - (4i^2 + 10i + 6) \\
    &= 4(i^2+2i+1) + 10i + 16 - 4i^2 - 10i - 6 \\
    &= 8i + 14
\end{align*}
\noindent Chegamos ao polinómio de grau 1 $8i + 14$.
No código, esta primeira diferença é mapeada para $m$. Assim, $m$ representa o incremento necessário para passar de $q(i)$ para $q(i+1)$.
Traduzindo tudo para o código,
\begin{itemize}
    \item $j$ representa $q(i)$;
    \item $m$ representa $\Delta q(i)$ (como provámos acima);
    \item a instrução $j + m$ calcula $q(i+1)$, o que deriva a linha de código $j_{new} = j + m$.
\end{itemize}

\noindent Para calcular $m_{start}$, usamos a fórmula de $\Delta q(i)$ com $i=1$:
\begin{equation}
    m_{start} = \Delta q(1) = 8(1) + 14 = 22
\end{equation}

\noindent \textbf{2. A Segunda Diferença ($\Delta^2 q$):}

\noindent Como a variável $m$ ainda depende de $i$, temos de fazer o processo anterior mais uma vez.
Calculámos uma expressão para o ($\Delta^2 q$):
\begin{align*}
    \Delta^2 q(i) &= \Delta q(i+1) - \Delta q(i) \\
    &= [8(i+1) + 14] - [8i + 14] \\
    &= 8i + 8 + 14 - 8i - 14 \\
    &= 8
\end{align*}
Como fizemos a derivação de uma função linear obtemos uma constante.
Isto vai representar quanto vai incrementar o $m$ em cada iteração, o que explica a linha $m + 8$ no código.

\paragraph{Conclusão}
O uso de Diferenças Finitas permite-nos transformar uma função complexa do índice ($i$) num sistema de atualizações incrementais simples (apenas somas).
Em vez de calcularmos $4i^2 + \dots$ a cada passo (o que exigiria multiplicações e saber o valor de $i$), mantemos o estado das "derivadas discretas" ($j$ e $m$) e atualizamo-las com custo computacional mínimo:

\begin{equation}
\begin{cases}
    k_{new} = k \times j & \text{(Aplicação do fator)} \\
    j_{new} = j + m & \text{(Atualização pela 1ª diferença)} \\
    m_{new} = m + 8 & \text{(Atualização pela 2ª diferença constante)}
\end{cases}
\end{equation}

\noindent Tal como demonstrado para $h$ e $k$, a função \texttt{start} coloca o sistema no estado correspondente a $i=1$ (preparação para calcular a transição para $i=2$).

%----------------- Fim do Problema 2 -----------------------------------------%

\subsection*{Problema 3}

\subsection*{Problema 3: Fair-merge como um anamorfismo}

%----------------- Introducao ao Problema 3 -----------------------------------------%

\subsubsection*{Contextualização}

\noindent O objetivo do problema é fundir duas sequências infinitas numa única \textit{stream}, de forma justa, isto é, intercalando alternadamente os elementos provenientes de cada uma das sequências, sem favorecer nenhuma delas.
Parte-se de uma definição baseada em recursividade mútua, que explicita essa alternância, e pretende-se reformulá-la como um anamorfismo, identificando o estado e a função geradora responsáveis por produzir, passo a passo, a sequência resultante.

\subsubsection*{Lei Dual da Recursividade Mútua}

\noindent Vamos primeiro derivar a lei dual da recursivade mútua, ou seja,
\begin{eqnarray*}
	|either f g = ana(either h k)| & \equiv & |lcbr (out . f = fF (either f g) . h)(out . g = fF (either f g) . k)|
\end{eqnarray*}

\noindent Sejam
\[
f :: A \to F \; B \quad \text{e} \quad g :: C \to F \; B
\]
duas funções definidas sobre os ramos esquerdo e direito de um coproducto,
e seja
\[
either\ f\ g :: A + C \to F \; B
\]
a função obtida por copareamento.

\noindent Sejam ainda
\[
h :: D \to A \quad \text{e} \quad k :: D \to C
\]
tais que
\[
either\ f\ g = ana (either\ h\ k).
\]
Então, podemos dizer que:
\begin{eqnarray*}
\start
|
    either f g = ana(either h k)
|
\just\equiv{ universal-ana }
|
    out . either f g = ( fF (either f g)) . either h k
|
\just\equiv{ fusão-|+| (twice) }
|
    either (out . f) (out . g) = either (fF (either f g) . h) (fF (either f g) . k)
|
\just\equiv{ eq-|+| }
|
lcbr(
    out . f = fF (either f g) . h
)(
    out . g = fF (either f g) . k
)
|
\end{eqnarray*}

\noindent Provámos assim a lei dual da recursividade mútua.

\subsubsection*{Aplicação do dual no problema do \textit{fair-merge}}

\noindent Parte-se da definição original por recursivade mútua:

\begin{code}
fair_merge :: Either (Stream a, Stream a) (Stream a, Stream a) -> Stream a
fair_merge = either h k where
   h (Cons(x,xs), y) = Cons(x , k(xs,y))
   k (x, Cons(y,ys)) = Cons(y , h(x,ys))
\end{code}

\noindent O objetivo é reescrever \texttt{fair-merge} como um anamorfismo de \texttt{Stream}, isto é, na forma

\begin{code}
fair_merge = anaStream g
\end{code}

\noindent para uma função geradora adequada \texttt{g}.

\subsubsection*{1. Identificação do functor e dos operadores estruturais}

\noindent Para o tipo
\begin{code}
data Stream a = Cons (a, Stream a)
\end{code}

\noindent obtém-se diretamente o functor base:

\begin{spec}
    F X = A >< X (ou F S = A >< S)
\end{spec}

\noindent e tem-se:
\begin{itemize}
    \item outStream :: Stream a -> (a, Stream a)
    \item anaStream g = Cons . (id >< anaStream g) . g
\end{itemize}

\vspace{0.5cm}

\noindent \textbf{Nota:}
A derivação do functor base resulta da aplicação do procedimento padrão para tipos algébricos recursivos definidos por pares, em que:
\begin{itemize}
    \item o primeiro componente é um valor não recursivo;
    \item o segundo componente corresponde à chamada recursiva do tipo.
\end{itemize}

\noindent O processo é o seguinte:
\begin{itemize}
    \item Elimina-se o nome do tipo e escreve-se apenas com a sua forma estrutural:
    \begin{spec}
        Stream a = a >< Stream a
    \end{spec}
    \item Subsitui-se a ocorrência recursiva por uma variável abstrata X, obtendo o funtor base:
    \begin{spec}
        F(X) = a >< X
    \end{spec}
\end{itemize}

\noindent O functor descreve assim o padrão de construção de uma \texttt{Stream}: em cada passo é produzido um elemento do tipo \textit{a} e um novo estado recursivo.

\subsubsection*{2. Instanciação da lei dual}

\noindent Aplicando a lei ao problema, tem-se que:
\begin{itemize}
    \item f = h
    \item g = k
    \item out = outStream
\end{itemize}

\noindent Logo, basta verificar que as equações estruturais acima são satisfeitas e, a partir delas, extrair a função geradora.

\subsubsection*{3. Cálculo explícito de outStream . h e outStream . k}

\noindent Comecemos por h.

\noindent \textbf{Definição:}
\begin{eqnarray*}
\start
|
    h (Cons(x,xs), y) = Cons(x, k(xs,y))
|
\just\equiv{ aplica-se outStream }
|
    outStream . h (Cons(x,xs), y) = outStream . Cons(x, k(xs,y))
|
\just\equiv{ def-comp }
|
    outStream (h (Cons(x,xs), y)) = outStream (Cons(x, k(xs,y)))
|
\just\equiv{ def-outStream }
|
    outStream (h (Cons(x,xs), y)) = (x, k(xs,y))
|
\end{eqnarray*}

\noindent Por outro lado, como
\[
\begin{aligned}
    |fF (either h k) = (id >< either h k)|
\end{aligned}
\]

\noindent temos que
\[
\begin{aligned}
    |(id >< either h k) (x, (xs,y)) = (x, either h k (xs,y))|
\end{aligned}
\]

\noindent Observa-se também que:
\[
\begin{aligned}
    |either h k (xs,y) = k (xs,y)|
\end{aligned}
\]

\noindent pois (xs,y) pertence ao ramo direito do Either.

\noindent Assim:
\[
\begin{aligned}
    |outStream . h = (id >< either h k) . h'|
\end{aligned}
\]
\noindent onde
\[
\begin{aligned}
    |h' (Cons(x,xs), y) = (x, Right(xs,y))|
\end{aligned}
\]

\noindent Agora para k.

\noindent De forma análoga:
\begin{eqnarray*}
\start
|
    k (x, Cons(y,ys)) = Cons(y, h(x,ys))
|
\just\equiv{ aplica-se outStream }
|
    outStream . k (x, Cons(y,ys)) = outStream . Cons(y, h(x,ys))
|
\just\equiv{ def-comp }
|
    outStream (h (x, Cons(y,ys))) = outStream (Cons(y, h(x,ys)))
|
\just\equiv{ def-outStream }
|
    outStream (k (x, Cons(y,ys))) = (y, h(x,ys))
|
\end{eqnarray*}

\noindent E:
\[
\begin{aligned}
    |(id >< either h k) (y, (x,ys)) = (y, either h k (x,ys))|
\end{aligned}
\]
\noindent Como (x,ys) pertence ao ramo esquerdo:
\[
\begin{aligned}
    |either h k (x,ys) = h (x,ys)|
\end{aligned}
\]

\noindent Logo:
\[
\begin{aligned}
    |outStream . k = (id >< either h k) . k'|
\end{aligned}
\]

\noindent com
\[
\begin{aligned}
    |k' (x, Cons(y,ys)) = (y, Left (x,ys))|
\end{aligned}
\]

\subsubsection*{4. Extração da função geradora g}

\noindent Das duas equações anteriores, conclui-se que existe uma única função

\begin{code}
g :: Either (Stream a, Stream a) (Stream a, Stream a) -> (a, Either (Stream a, Stream a) (Stream a, Stream a))
\end{code}

\noindent tal que:

|fair_merge = anaStream g|

\noindent Explicitamente:

\begin{code}
g (Left (Cons(x,xs), y)) = (x, Right (xs,y))
g (Right (x, Cons(y,ys))) = (y, Left (x,ys))
\end{code}

\subsubsection*{5. Definição final como anamorfismo}

\noindent A definição pedida fica então:

\begin{code}
fair_merge :: Either (Stream a, Stream a) (Stream a, Stream a) -> Stream a
fair_merge = anaStream g
    where
        g (Left (Cons(x,xs), y)) = (x, Right (xs,y))
        g (Right (x, Cons(y,ys))) = (y, Left (x,ys))
\end{code}

\subsubsection*{6. Interpretação estrutural}

\begin{itemize}
    \item O estado do anamorfismo é um Either que indica de que via vem o próximo elemento.
    \item A função g:
    \begin{itemize}
        \item produz o próximo valor da Stream;
        \item alterna deterministicamente o estado (Left <-> Right), garantindo fair-merge.
    \end{itemize}
    \item A alternância, que antes surgia como recursividade mútua explícita, passa a estar presente no estado do anamorfismo.
\end{itemize}

\noindent Isto completa a derivação pedida.

%----------------------------------------- Fim do Problema 3 -----------------------------------------%

\subsection*{Problema 4}

\subsection*{Problema 4: Catamorfismos probabilísticos}

%----------------- Introducao ao Problema 4 -----------------------------------------%

\subsubsection*{1. Contextualização}

\noindent O objetivo do problema é calcular a probabilidade de como uma mensagem é transmitida através de um canal com falhas, utilizando um mónade de distribuições probabilísticas, (\texttt{Dist}).
Este canal apresenta duas falhas diferentes:
\begin{itemize}
    \item No corpo da mensagem, cada palavra tem $5\%$ de chance de se perder (não ser transmitida);
    \item No final da mensagem, é suposto ser sempre enviada a palavra |"stop"|, que não é enviada $10\%$ das vezes.
\end{itemize}

\noindent O objetivo é modelar a função como um catamorfismo probabilístico de listas:

\begin{spec}
transmitir :: [String] -> Dist [String]
transmitir = pcataList gene
\end{spec}

\noindent Portanto, teremos de derivar o \emph{gene} que captura a lógica das falhas e calcular as probabilidades de todas as possibilidades de uma dada mensagem transmitida.

\subsubsection*{2. O Problema da Composição Simples}

\noindent Para explicar a origem da função \texttt{pcataList}, é necessário entender como se compõem funções que produzem distribuições probabilísticas.

\noindent Numa composição de duas funções $f :: A \to B$ e $g :: B \to C$ dada por $g \circ f$ o tipo da saída de $f$ tem de coincidir com o tipo de entrada de $g$.

\noindent No entanto, neste problema, estamos a lidar com funções que produzem distribuições probabilísticas. Sejam:
\begin{itemize}
    \item $f :: A \to \mathsf{Dist}\ B$
    \item $k :: B \to \mathsf{Dist}\ C$
\end{itemize}

\noindent A composição simples $k \circ f$ é impossível, pois existe um conflito de tipos: o tipo de saída de $f$ é uma distribuição ($\mathsf{Dist}\ B$) ao contrário do tipo de entrada de $k$, que é um valor concreto ($B$).
$$ \mathsf{Dist}\ B \neq B $$

\subsubsection*{3. Definição de \texttt{pcataList} através da Composição de Kleisli}

\noindent Para compor estas funções, podemos recorrer à \emph{Composição de Kleisli}, denotada por $k \bullet f$ ou \texttt{>=>} em Haskell.

\noindent Matematicamente, dada uma distribuição $d :: \mathsf{Dist}\ B$ e a função $k$, a composição define-se através do operador \emph{bind} ($\gg=$):
$$ (k \bullet f)(x) = f(x) \gg= k $$
onde o operador $\gg=$ aplica $k$ a cada resultado possível dentro da distribuição $f(x)$ tendo assim em conta todas as possibilidades dadas.

\noindent Agora que sabemos que precisamos de uma Composição de Kleisli, podemos chegar à implementação de \texttt{pcataList}.

\noindent A operação de processar a lista resume-se a dois passos sequenciais:
\begin{enumerate}
    \item Calcular o resultado da recursão sobre a cauda da lista;
    \item Usar esse resultado para aplicar o gene à cabeça da lista.
\end{enumerate}

\noindent Ao usarmos o operador \emph{bind} (\texttt{>>=}) do mónade, a definição do passo recursivo passa a ser:
\begin{spec}
pcataList g (x:xs) = pcataList g xs >>= (\y -> g (Right (x, y)))
\end{spec}

\noindent Nesta expressão, \texttt{pcataList g xs} produz a distribuição da cauda, e a função \texttt{lambda} consome o valor concreto \texttt{y} dessa distribuição para aplicar o gene.

\noindent Uma maneira alternativa de escrever esta expressão em Haskell é usando a notação \texttt{do}. Ao adicionarmos também o caso base, chegamos à definição final da função:

\begin{code}
pcataList g [] = g (Left ())
pcataList g (x:xs) = do
    y <- pcataList g xs
    g (Right (x, y))
\end{code}

\subsubsection*{4. Interpretação Probabilística e Propagação de Incerteza}

\noindent Neste contexto podemos dividir a interpretação probabilística em duas partes.

\noindent \textbf{Injeção de Falhas:}

\noindent Para cada elemento da lista, o gene bifurca a execução em duas situações possíveis: um onde a falha ocorre e outro onde ocorre o funcionamento esperado (respeitando as probabilidades de $5\%$ e $10\%$).

\noindent \textbf{Combinação Global (Produto de Probabilidades):}

\noindent Como vimos na secção anterior, a instrução \texttt{y <- pcataList g xs} significa que $y$ é um dos resultados possíveis da transmissão do resto da mensagem.
Quando o gene processa a cabeça da lista e a combina com $y$, o mónade \texttt{Dist} realiza o produto cartesiano dos eventos.

\noindent Se a transmissão da cauda gerar $N$ resultados possíveis e o processamento da cabeça gerar $M$ resultados possíveis, a distribuição vai ter $N \times M$ cenários.
Como as falhas são independentes, isto é, o que acontece a uma palavra não afeta outra, a probabilidade final de uma frase é a multiplicação das probabilidades de cada passo.
$$ P(\text{Cenário Global}) = P(\text{Evento na Cabeça}) \times P(\text{Evento na Cauda}) $$

\subsubsection*{4. Construção do Gene Probabilístico}

\noindent Começando pelo tipo do gene, este normalmente, sendo de um catamorfismo de listas, tem o tipo $1 + A \times B \to B$.
Mas como o resultado é uma distribuição de listas ($B = \mathsf{Dist\ [String]}$) e o gene processa valores concretos e não com distribuições, o seu tipo é:
\begin{code}
gene :: Either () (String, [String]) -> Dist [String]
\end{code}

\noindent A construção do gene pode-se dividir em duas fases:
\begin{itemize}
    \item O Caso Base, ou seja, quando é inserido (ou não) o \texttt{"stop"}
    \item O Corpo da mensagem
\end{itemize}

\noindent \textbf{Caso Base: O Sufixo (Left ())}

\noindent Quando a travessia atinge o fim da lista o esperado é que seja inserido a palavra \texttt{"stop"} no final.
O enunciado diz que esta operação falha em $10\%$ dos casos.
Logo, temos os seguintes casos possíveis:
\begin{itemize}
    \item Sucesso ($90\%$): O sufixo é gerado corretamente. Resultado: \texttt{["stop"]};
    \item Falha ($10\%$): Nada é gerado. Resultado: \texttt{[]}.
\end{itemize}

\noindent Implementado em Haskell fica:
\begin{spec}
gene (Left ()) = D [ (["stop"], 0.90), ([], 0.10) ]
\end{spec}

\noindent \textbf{Caso Recursivo: O Corpo da Mensagem (Right (a, x))}

\noindent Neste passo, recebemos um par \texttt{(a,x)}, onde \texttt{a} é a palavra atual e \texttt{x} é o resto da mensagem que já foi processada.
No enunciado é-nos dito que a probabilidade de uma palavra é:
\begin{itemize}
    \item Sucesso ($95\%$): A palavra \texttt{a} é trasmitida e é colocada à cabeça da lista \texttt{x}. Resultado: \texttt{a : x};
    \item Perda ($5\%$): Existe uma falha de transmissão na palavra \texttt{a}. O resultado é apenas a cauda \texttt{x}.
\end{itemize}

\noindent Implementado em Haskell fica:
\begin{spec}
gene (Right (a, x)) = D [ (a:x, 0.95), (x, 0.05) ]
\end{spec}

\subsubsection*{5. Definição Final}

\noindent Ao juntarmos o que derivamos nas secções anteriores, obtemos a seguinte função:

\begin{code}
gene (Left ()) = D [ (["stop"], 0.90), ([], 0.10) ]
gene (Right (a, x)) = D [ (a:x, 0.95), (x, 0.05) ]

transmitir_final :: [String] -> Dist [String]
transmitir_final = pcataList gene
\end{code}

\noindent A função \texttt{transmitir\_final} é definida como o catamorfismo probabilístico parametrizado pelo gene que construímos e será utilizada para calcular a solução final.

\subsubsection*{6. Cálculo da probabilidade da frase}

\noindent Considere-se a frase
\[
\texttt{words = ["Vamos", "atacar", "hoje"]},
\]
apresentada no enunciado.
Ao aplicar o catamorfismo probabilístico definido anteriormente, isto é,
\[
transmitir = pcataList\; gene,
\]
obtém-se uma distribuição de probabilidade sobre todas as possíveis mensagens recebidas:

\begin{code}
resultado = transmitir words
\end{code}

\noindent Cada elemento de \texttt{resultado} corresponde a uma possível sequência de palavras produzida pelo canal, acompanhada da respetiva probabilidade, resultante da combinação das falhas independentes em cada etapa da transmissão.

\noindent A avaliação do programa produz a seguinte distribuição (os valores apresentados foram arredondados às centésimas):

\begin{center}
\begin{tabular}{l||r}
Mensagem recebida & Probabilidade \\ \hline\hline
\texttt{["Vamos", "atacar", "hoje", "stop"]} & $77.16\%$ \\
\texttt{["Vamos", "atacar", "hoje"]} & $8.57\%$ \\
\texttt{["atacar", "hoje", "stop"]} & $4.06\%$ \\
\texttt{["Vamos", "atacar", "stop"]} & $4.06\%$ \\
\texttt{["Vamos", "hoje", "stop"]} & $4.06\%$ \\
\texttt{["Vamos", "atacar"]} & $0.45\%$ \\
\texttt{["Vamos", "hoje"]} & $0.45\%$ \\
\texttt{["atacar", "hoje"]} & $0.45\%$ \\
\texttt{["Vamos", "stop"]} & $0.21\%$ \\
\texttt{["atacar", "stop"]} & $0.21\%$ \\
\texttt{["hoje", "stop"]} & $0.21\%$ \\
\texttt{["atacar"]} & $0.02\%$ \\
\texttt{["Vamos"]} & $0.02\%$ \\
\texttt{["hoje"]} & $0.02\%$ \\
\texttt{["stop"]} & $0.01\%$ \\
\texttt{[]} & $0.00\%$ \\
\end{tabular}
\end{center}

\noindent A distribuição obtida reflete diretamente a semântica do gene: cada palavra é transmitida ou perdida de forma independente, e o sufixo \texttt{"stop"} é acrescentado apenas no caso de sucesso da etapa final.

\noindent A partir desta tabela, podem ler-se diretamente as respostas às questões colocadas:
\begin{itemize}
    \item a perda da palavra \texttt{"atacar"} com transmissão das restantes corresponde a uma probabilidade de cerca de $4.06\%$;
    \item a receção integral da mensagem sem o sufixo \texttt{"stop"} ocorre com probabilidade aproximada de $8.57\%$;
    \item a transmissão sem qualquer falha ocorre com probabilidade aproximada de $77.16\%$.
\end{itemize}

\subsubsection*{7. Comprovação dos resultados obtidos}

\noindent Para comprovar os resultados obtidos, podemos calcular "à mão", a probabilidade de acontecer cada situação descrita. Tem-se que:
\begin{itemize}
    \item P(transmitir "word") = 0.95;
    \item P(transmitir "stop") = 0.9.
\end{itemize}

\noindent Podemos calcular, assim, as probabilidades:
\begin{itemize}
    \item Probabilidade de "atacar" se perder:
    \begin{equation*}
    \begin{aligned}
    P(\text{"atacar" se perder})
    &= P(\text{transmitir "Vamos"})
        \times P(\text{perder "atacar"}) \\
    &\quad \times P(\text{transmitir "hoje"})
        \times P(\text{transmitir "stop"}) \\
    &= 0.95 \times (1 - 0.95) \times 0.95 \times 0.9
    = 0.0406125 \approx 4.06\%
    \end{aligned}
    \end{equation*}

    \item Probabilidade de não transmitir "stop":
    \begin{equation*}
    \begin{aligned}
    P(\text{não transmitir "stop"})
    &= P(\text{transmitir "Vamos"})
        \times P(\text{transmitir "atacar"}) \\
    &\quad \times P(\text{transmitir "hoje"})
        \times P(\text{perder "stop"}) \\
    &= 0.95 \times 0.95 \times 0.95 \times (1 - 0.9)
    = 0.0857375 \approx 8.57\%
    \end{aligned}
    \end{equation*}

    \item Probabilidade de não haver qualquer falha:
    \begin{equation*}
    \begin{aligned}
    P(\text{transmissão sem falhas})
    &= P(\text{transmitir "Vamos"})
        \times P(\text{transmitir "atacar"}) \\
    &\quad \times P(\text{transmitir "hoje"})
        \times P(\text{transmitir "stop"}) \\
    &= 0.95 \times 0.95 \times 0.95 \times 0.9
    = 0.7716375 \approx 77.16\%
    \end{aligned}
    \end{equation*}
\end{itemize}

%----------------------------------------- Fim do Problema 4 -----------------------------------------%

%----------------------------------------- Conclusão -----------------------------------------%

\subsection*{Conclusão}

Com a resolução dos problemas propostos, considera-se que os objetivos definidos no enunciado foram atingidos, tendo sido possível aplicar de forma consistente os conceitos teóricos abordados ao longo da unidade curricular.

%----------------- Índice remissivo (exige makeindex) -------------------------%

\printindex

%----------------- Bibliografia (exige bibtex) --------------------------------%

\bibliographystyle{plain}
\bibliography{cp2526t}

%----------------- Fim do documento -------------------------------------------%
\end{document}