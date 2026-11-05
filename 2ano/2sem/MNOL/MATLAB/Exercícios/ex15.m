op = optimset('tolfun', 0.015, 'tolX', 0.015); %tolerancias de paragem
x=[4.3 2.4]; %aproximacao inicial
[x,fval,exitflag,output]=fsolve(@exerc_15,x,op);
%funcao
function [ F ] = exerc_15(x)
    F(1) = 1-x(1)*cos(x(2))-x(1);
    F(2) = -0.1*x(1)^2+x(1)*sin(x(2))-1+exp(-x(1));
end