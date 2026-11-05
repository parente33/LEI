clear all
d=[0.239212 0.239215 0.239221];
p=[0.00015 0.00020 0.00030];
[c,RESNORM] = lsqcurvefit(@fun,[20,1],d,p)
% P(d)=10.9843*d^2 + 0.4393*ln(d);
function P = fun(c,d)
    P=c(1).*d.^2 + c(2).*log(d);
end
