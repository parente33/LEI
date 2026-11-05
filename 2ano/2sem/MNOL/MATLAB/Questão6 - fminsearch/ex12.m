clear all
op = optimset('Tolx',0.5);
x0 = [1;1];
fun = @(x)max((x(1)-1)^2,x(1)^2+4*(x(2)-1)^2);
[x,fval,exitflag,output] = fminsearch(fun,x0,op)