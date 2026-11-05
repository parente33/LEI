% polyfit -> calcula os coeficientes de um polinómio de grau n que
% melhor se ajusta aos dados Y, no sentido de mínimos quadrados.
% Calcula também o resíduo

[p,S] = polyfit(X,Y,n)

% X -> vetor com os pontos
% Y -> vetor com os valores da função nos pontos
% n -> grau do polinómio que se quer construir

% p -> vetor com os coeficientes do polinómio, em potências decrescentes
% pn(x) = c1*(x^n) + c2*(x^n−1) + . . . + cn*x + cn+1
% S -> estrutura para obter uma estimativa do erro. Contém os seguintes
% campos:
    % R -> matriz triangular superior da decomposição QR da matriz de
    % Vandermonde de X
    % df -> graus de liberdade
    % normr -> norma dos resíduos, ie, a raiz quadrada da soma dos
    % quadrados dos resíduos


%------------------%

% polyval -> calcula o valor do polinómio p num ponto ou conjunto de pontos
% x. p é um vetor de comprimento n+1, cujos elementos são os coeficientes
% do polinómio, em potências decrescentes

y = polyval(p,x)

% p -> vetor com os coeficientes do polinómio, em potências decrescentes
% x -> ponto ou vetor de pontos para os quais se pretende calcular o valor
% do polinómio

% y -> valor ou vetor de valores do polinómio


%-------------------%

% lsqcurvefit -> calcula os coeficientes de um modelo M(x) que melhor se
% ajusta aos dados y, no sentido de mínimos quadrados. Também calcula o
% resíduo

[c, resnorm] = lsqcurvefit(fun,c0,x,y)

% fun -> função com o modelo (tem dois argumentos de entrada: c (vetor dos
% coeficientes do modelo), e x (variável do modelo)). Usar os operadores
% ./, .* e .^ na definição da função

% Especificar fun como função
% [c, resnorm] = lsqcurvefit(@fun,c0,x,y)
% function M = fun(c,x)
% M = ...;
% end

% Especificar fun como um identificar de função anónima:
% [c,resnorm] = lsqcurvefit(@(c,x)...,c0,x,y)
% 
% Especificar a função fun:
% fun = @(c,x)...;
% [c,resnorm] = lsqcurvefit(fun,c0,x,y)
% 
% c0 -> vetor com a aproximação inicial aos coeficientes
% x -> vetor com os pontos
% y -> vetor com os valores da função nos pontos
% 
% c -> vetor com os coeficientes do modelo
% resnorm -> soma do quadrado dos resíduos (erro)