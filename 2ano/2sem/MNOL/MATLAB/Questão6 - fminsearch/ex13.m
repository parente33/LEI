clear all
op=optimset('Tolx',0.75);
x0=[-1;1];
fun = @(x)max(abs(x(1)),abs(x(2)-1));
[X,FVAL,EXITFLAG,OUTPUT] = fminsearch(fun,x0,op)