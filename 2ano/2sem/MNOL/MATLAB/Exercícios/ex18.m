op = optimoptions('fsolve','MaxIterations',1)
[x,fval,exitflag,output]=fsolve(@exerc_18,[-1.9 -0.15],op)
function [ F ] = exerc_18(x)
    F(1) = 70*exp(x(1))+20*exp(x(2))-27.5702;
    F(2) = 70*exp(2*x(1))+20*exp(2*x(2))-17.6567;
end