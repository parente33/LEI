clear all
op=optimset('Tolx',0.5);
x0=[-1;-1];
fun = @(x)sqrt(abs(x(1)*x(2)));
[X,FVAL,EXITFLAG,OUTPUT] = fminsearch(fun,x0,op)