t = [10 20 30]
cm = [1003 1010 1015]
% modelo linear nao polinomial
[c,RESNORM] = lsqcurvefit(@exerc_28,[1 1],t,cm)
RESNORM % soma do quadrado dos residos - erro
% c(t) = 0.6000*t + 997.3333
exerc_28(c,18) %estimar o comprimento para t=18
function [ m ] = exerc_28(c,t)
    %a->c(1)
    %b->c(2)
    m = c(1).*t + c(2)
end