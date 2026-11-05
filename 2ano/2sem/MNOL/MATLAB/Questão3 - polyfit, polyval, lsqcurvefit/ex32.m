x = [1 3 6 10 12]
f = [122 188 270 160 120]
[c,RESNORM] = lsqcurvefit(@exerc_32,[1,1],x,f)
% c1 = 24.7203 c2= 148.3147
% m = 24.7203*x + 148.3147*sin(x)
RESNORM
exerc_32(c,8)
function [ m ] = exerc_32(c,x)
    m = c(1).*x + c(2).*sin(x);
end