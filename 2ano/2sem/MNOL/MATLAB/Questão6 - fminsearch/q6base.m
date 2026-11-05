[x,fval,exitflag,output] = fminsearch(fun,x0,options)

% fun - função objetivo a minimizar
% fun como função:
% function F = fun(x)
% F = ... ;
% end

% fun como um identificador de função anónima:
% [x,fval,exitflag,output] = fminsearch(@(x)...,x0)

% Especificar a fun¸c˜ao fun:
% fun = @(x)...;
% [x,fval,exitflag,output] = fminsearch(fun,x0)

% x0 - aproximação inicial

% options:
% Display - Nível de apresentação.
%       notify - (default) apresenta resultado se a função não convergir
%       final - apresenta apenas o resultado final
%       off - não apresenta nada
%       iter - apresenta resultados em cada iteração
% MaxFunEvals - Nº máximo de cálculos da função (default=200*NumVar)
% MaxIter - Nº máximo de iterações (default=200*NumVar)
% PlotFcns - Representa graficamente medidas do progresso do algoritmo, ao
% longo das iterações
%       @optimplotx - aproximação à solução x.
%       @optimplotfunccount - número de cálculos da função.
%       @optimplotfval - valor da função objetivo.
% TolFun - Tolerância de paragem da função objetivo (default=1e-4)
% TolX - Tolerância de paragem em x (default=1e-4)
% FunValCheck - Verifica se os valores da função objetivo são válidos.
%       on - apresenta erro
%       off - (default) não apresenta qualquer erro

%----------------------------%

% x - solução ótima do problema (minimizante)
% fval - valor da função objetivo em x (mínimo)
% exitflag - descreve valores de saída do fminsearch
%       1 - indica que a função convergiu para a solução x
%       0 - indica que o número de iterações excedeu o MaxIter ou o
%       MaxFunEvals
%       -1 - indica que a função não convergiu para uma solução
% output - estrutura com informação acerca do processo de otimização
%       iterations - nº de iterações realizadas
%       funcCount - nº de cálculos da função
%       algorithm - algoritmo usado
%       message - mensagem de saída