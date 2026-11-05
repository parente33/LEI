op = optimset('tolfun', 0.015, 'tolX', 0.015); %tolerancias de paragem
% o primeiro valor, tolfun, é o ε1
% o segundo valor, o tolX, é o ε2

[x,fval,exitflag,output] = fsolve(@exerc,[0 1])
% @exerc refere-se à chamada da função, que se define a seguir
%[0 1] é o intervalo de valores que está no enunciado
% se se usar as tolerancias de paragem, então ficaria:
[x,fval,exitflag,output] = fsolve(@exerc,[0 1],op)

% também se pode definir o intervalo de valores fora da chamada da função
% ficaria algo do género:
y = [1.2 2.3]
[x,fval,exitflag,output] = fsolve(@exerc,y,op)

% se no enunciado não tiver os valores dos ε, e disser "máximo de n
% iterações", então faz-se o seguinte:
op = optimoptions('fsolve','MaxIterations', 1) % aqui usei 1 como exemplo,
% , mas substituir pelo valor que é dado, naturalmente

function [ F ] = exerc(x)
    F(1) = 4*x(1) + log(x(2))/3 - 2;
    F(2) = x(1)*x(2) - exp(x(1)^x(2)) - 1/7;
end
% os valores que em princípio ela deve pedir são os que estão no lado
% direito, em "x". Este valor é chamado de x*.
% se ela pedir os valores da função nesse ponto (geralmente chamado de f(x*), é só ir ao sistema de
% equações (os que estão definidos nas linhas 38 e 39, neste documento, e
% substituir as variáveis pelos dois valores (o primeiro valor é o x(1), e
% o segundo é o x(2))
