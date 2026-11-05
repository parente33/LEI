clear all

op=optimset('Tolfun',0.1,'HessUpdate','bfgs');
x0=[2;1];
fun = @(x)-((x(1)*x(2))/(1+x(1)*x(2))-0.04*x(1)-0.06*x(2));
[X,FVAL,EXITFLAG,OUTPUT] = fminunc(fun,x0,op)