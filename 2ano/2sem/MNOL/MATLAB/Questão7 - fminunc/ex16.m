clear all

op=optimset('Tolfun',0.1);
x0=[1;1];
fun = @(x)x(1)*x(2)^2+(2-x(1))^2;
[X,FVAL,EXITFLAG,OUTPUT] = fminunc(fun,x0,op)