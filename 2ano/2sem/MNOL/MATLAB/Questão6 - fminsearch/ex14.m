clear all
op=optimset('Tolx',1);
x0=[1;-1];
fun = @(x)abs(x(1)-1)+abs(x(1)-x(2));
[X,FVAL,EXITFLAG,OUTPUT] = fminsearch(fun,x0,op)