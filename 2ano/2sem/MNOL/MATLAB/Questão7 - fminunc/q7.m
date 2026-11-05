clear all

op = optimset('Tolfun',1e-2,'HessUpdate','bfgs');
x0=[1;2];
fun=@(x)(x(1)^2+10*(x(2)^2))
[X,FVAL,EXITFLAG,OUTPUT] = fminunc(fun,x0,op)

%op = optimset('TolX',1e-2,'HessUpdate','bfgs');
%x0=[0;-3];
%fun=@(x)x(1)^2-4*(x(2)^3)-2*x(1)*x(2)
%[X,FVAL,EXITFLAG,OUTPUT] = fminunc(fun,x0,op)