clear all

op=optimset('Tolfun',0.02,'HessUpdate','DFP');
x0=[1;0];
fun = @(x)x(1)^2+x(2)^2-x(1)*x(2);
[X,FVAL,EXITFLAG,OUTPUT] = fminunc(fun,x0,op)