clear
% Calcular uma aproximação com comprimento do arco da curva f(x) = e^-x no
% intervalo [0,1], usando 5 pontos igualmente espaçados no intervalo
% O integral é dado por: int(a,b) (sqrt(1+(f'(x))^2) dx
h = (1-0)/4 % 5 pontos <=> 4 subintervalos
x = 0:h:1
y = sqrt(1+(-exp(-x)).^2)
sol_5_pontos = trapz(x,y)

% ou então

x = [0 0.25 0.5 0.75 1.0]
y = sqrt(1+(-exp(-x)).^2)
trapz(x,y)

% Se não tivesse sido pedido para usar 5 pontos, fazia-se assim:
Q=integral(@(x)sqrt(1+(-exp(-x)).^2),0,1)