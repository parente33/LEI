clear all

op=optimset('Tolfun',0.15,'HessUpdate','dfp');
x0=[6;7;5];
fun = @(x)x(1)+x(2)+x(3)+2401/(x(1)*x(2)*x(3));
[X,FVAL,EXITFLAG,OUTPUT] = fminunc(fun,x0,op)
