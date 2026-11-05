% calcula uma spline cúbica do tipo not-a-knot

s = spline(x,[f0' y fn'])

% x -> vetor com os pontos
% y -> vetor com os valores da função nos pontos
% f0' -> valor da derivada no extremo inferior
% fn' -> valor da derivada no extremo superior

% s -> estrutura polinomial por partes. Contém os seguintes campos:
%   - form -> é a forma da spline que é um polinómio por partes
%   - breaks -> representam o início e o fim de cada um dos L intervalos,
%   cujo vetor tem L + 1 elementos (nº de pontos do vetor), estritamente
%   crescentes
%   - coefs -> matriz de dimensão L * k, em que cada linha contém os
%   coeficientes do segmento i da spline no intervalo [breaks(i),breaks(i+1)]
%   - pieces -> nº de segmentos da spline (L)
%   - order -> nº de coeficientes do polinómio (k)
%   - dim -> dimensionalidade do objetivo

% Uma vez que os coeficientes polinomiais em coefs são os coeficientes de
% cada segmento da spline em cada intervalo, é necessário subtrair o ponto
% inferior do intervalo do nó correspondente, para construir a expressão
% polinomial convencional de cada segmento.
% Assim, a primeira linha de coefs corresponde aos coeficientes do primeiro
% segmento da spline, s3(1) (x), para x∈[x0,x1]. Supondo que os coefs são
% [a,b,c,d], para o intervalo [x0,x1], a expressão para o 1º segmento da
% spline (polinómio correspondente) é dada por:

% s3(1) (x) = a(x-x0)^3 + b(x-x0)^2 + c(x-x0) + d, para x∈[x0,x1]


% Para determinar o valor da spline cúbica completa num ponto ou vetor de
% pontos:
% Usar o comando ppval(s,xq) para calcular o valor da spline no ponto xq,
% após ter usado o comando spline
s = spline(x,[f0' y fn'])
yq = ppval(s,xq)

% OU, usar o comando spline com um terceiro argumento para calcular o valor
% da spline no ponto xq
yq = spline(x,[f0' y fn'],xq)

% xq -> ponto ou vetor de pontos para os quais se pretende calcular o valor
% da spline
% yq -> valor ou vetor de valores da spline em xq

%----------------------%
% splines só com pontos (sem função)

x =  [10 12 18 27 30 34];
fx = [20 18 15 9 12 10];

ponto = 29;

% mudar só estes 3 ^^^^^^^^ 


x1 = x([1, 3:end-2, end]); % x mas sem os numeros na posicao 2 e n-1
fx1 = fx([1,3:end-2, end]); % msm mas para fx

tamanho = length (fx);
a = x(2);
b = x (tamanho-1);

fdo = (fx(1) - fx(2)) / (x(1) - x(2)); % derivada de fo
fdn = (fx(tamanho-1) - fx(tamanho)) / (x(tamanho-1) - x(tamanho)); % derivada de fn


s = spline (x1, [fdo fx1 fdn])

coeficientes = s.coefs % coeficientes em ordem decrescente 

valor = ppval (s,ponto) % valor da spline no ponto