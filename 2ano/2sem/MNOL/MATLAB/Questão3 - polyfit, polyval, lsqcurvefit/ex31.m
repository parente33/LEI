t = [0.11 0.52 0.79];
s = [-3.1127 0.0625 3.0351];
[c,RESNORM] = lsqcurvefit(@exerc_31,[1,1],t,s)
function [ m ] = exerc_31(c,t)
    m = 0.2*c(1)*sin(20*pi*t) + 0.2*c(2)*sin(22*pi*t);
end