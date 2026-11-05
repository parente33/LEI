clear
x = [30 35 45 50];
y = [12 12 10 8];
%Coeficientes do modelo
x0=[1 1];
[c,S] = lsqcurvefit(@exerc_34,x0,x,y)
function f = exerc_34(c,x)
    f=c(1)*x+c(2)*exp(-x);
end