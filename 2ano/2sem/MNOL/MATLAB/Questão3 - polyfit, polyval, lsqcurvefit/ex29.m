x = [0 1.25 2.5 3.75 5 6.25]
f = [0.260 0.208 0.172 0.145 0.126 0.113]
[P2,S2] = polyfit(x,f,2) %P2 - coeficientes do polinomio
%P2 = 0.0030*x^2 - 0.0418*x + 0.2583
S2.normr^2 %calcula a soma do quadrado dos residuos
%Calcular P2(3) - consumo para distancia = 3?
polyval(2,3)