x = [0 8 12 16 20];
y = [15.43 10.2 10.2 11.86 15.43];
%Parábola é um polinómio de grau 2
[p2,S2] = polyfit(x,y,2)
%S.normr^2
%Calcular o valor de p2 para x=10
distancia=polyval(p2,10)
%Polinómio de grau 3
[p3,S3] = polyfit(x,y,3)
%Coeficientes do modelo
x0=[1 1];
[m,S] = lsqcurvefit(@exerc_33,x0,x,y)
%e)
SQR2=S2.normr^2
SQR3=S3.normr^2
SQRM=S
function f = exerc_33(c,x)
f=c(1)*exp(1-0.1*x)+c(2)*exp(0.1*x-1);
end