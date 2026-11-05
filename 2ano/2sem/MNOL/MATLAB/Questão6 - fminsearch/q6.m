clear all
x0 = [0.1;0.1];
op = optimset('Tolx',10e-1);
fun = @(x)(12*abs(x(1)^3)+abs(x(1)+x(2)));
[x,fval,exitflag,output] = fminsearch(fun,x0,op)

%x0 = [3;3];
%op = optimset('Tolx', 10e-1);
%fun = @(x)max(2*(x(1)),abs(x(1)-x(2)));
%[x,fval,exitflag,output] = fminsearch(fun,x0,op)