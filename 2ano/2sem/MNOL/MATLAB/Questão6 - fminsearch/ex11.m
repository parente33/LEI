clear all
op = optimset ('Tolx',1.2)
x0 = [1;1];
fun = @(x)abs(x(1)*x(2)) + x(2)^2;
[x,fval,exitflag,output] = fminsearch(fun,x0,op)