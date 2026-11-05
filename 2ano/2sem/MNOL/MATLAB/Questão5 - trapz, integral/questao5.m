clear
%x = [3.0 4 4.5 5 6.5];
%y = [2.3 3 2.7 4 3.2];
%trapz(x,y)
In = integral(@(x)x.*exp(x),0,2)