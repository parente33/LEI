clear all
%x = [1 5 7 12 20 25]
%f = [16 18 20 23 27 30]
%[c,RESNORM] = lsqcurvefit(@exerc,[1,1,1],x,f)
%RESNORM
%exerc(c,17)
%function [ m ] = exerc(c,x)
%    m = 2*c(1) + c(2)*x
%end
x = [2 5 6 10 12 15]
f = [122 177 270 150 120 140]
[P,S] = polyfit(x,f,2)
S.normr^2
    