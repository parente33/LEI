%[x,fval,exitflag,output] = fsolve(@exerc,[2 1])

%function [ F ] = exerc(x)
%    F(1) = log(x(1)) - (x(1)*(x(2))^2);
%    F(2) = (x(1))^2 - (x(2))^2 - 2*x(2) - 1;
%end

op = optimset('tolfun', 1e-2);

[x,fval,exitflag,output] = fsolve(@exerc,[2 2],op)

function [ F ] = exerc(x)
    F(1) = (x(1))^2 + (x(1) - x(2))^2 - 0.5;
    F(2) = (x(1))^3 - 5*x(1)*x(2) + x(2) - 0.5;
end
