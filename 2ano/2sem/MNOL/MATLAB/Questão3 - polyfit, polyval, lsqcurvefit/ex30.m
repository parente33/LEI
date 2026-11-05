t = [2 4 8 10]
H = [1.6 1.4 0.2 0.8]
% modelo linear nao polinomial
[c,RESNORM] = lsqcurvefit(@exerc_30,[1,1,1],t,H)
RESNORM
function [ m ] = exerc_30(c,t)
    m = c(1) + c(2)*sin((2*pi.*t)/12) + c(3)*cos((2*pi.*t)/12);
end