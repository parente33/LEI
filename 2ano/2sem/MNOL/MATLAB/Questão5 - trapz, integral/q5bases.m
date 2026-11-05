%Calcula o integral aproximado de y através do método trapezoidal
%(com espaçamento unitário), com base num conjunto de pontos

z = trapz(x,y)

% x -> vetor com os pontos
% y -> vetor com os valores das funções nos pontos
% z -> valor aproximado do integral


%--------------------%

% Calcula numericamente um integral de uma função dentro dos limites
% inferior xmin e superior xmax, através do método da quadratura adaptativa
% global

z2 = integral(fun, xmin, xmax, Name, values)

% fun -> função que se pretende integrar; Usar os operadores ./, .* e .^ na
% definição da função
% Opção 1: Especificar fun como uma função:
    z3 = integral(@fun,xmin,xmax)
    function f = fun(x)
        f = ... ;
    end
% Opção 2: Especificar fun como um identificador de função anónima:
    fun = @(x)...;
    z = integral(fun,xmin,xmax)

% xmin -> limite inferior de integração
% xmax -> limite superior de integração
% Name, Value -> (opcional) especifica pares ocasionais de argumentos como
% (Name,Value), em que Name é o nome do argumento e Value é o valor
% correspondente.
    % AbsTol -> Tolerância do erro absoluto (default = 1e-10)
    % RelTol -> Tolerância do erro relativo (default = 1e-6)
 
% z -> Valor aproximado do integral