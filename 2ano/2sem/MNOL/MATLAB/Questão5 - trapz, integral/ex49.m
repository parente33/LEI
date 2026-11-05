clear
%alinea a)
%Estimar a velocidade na 1ª parte da corrida, usando a fórmula de
%integração mais adequada

x = [0 0.5 1 1.5];
y = [0 0.35 0.55 0.9];
trapz(x,y)

% alinea b)
% Estimar a velocidade na 2ª parte da corrida, usando a fórmula composta do
% trapézio, com erro de truncatura em valor absoluto inferior a 0.3
Q = integral(@(x)0.5*x.^2-0.15*x,1.5,7.5,'AbsTol',0.3)
% a(x) = 0.5*(x^2) - 0.15*x