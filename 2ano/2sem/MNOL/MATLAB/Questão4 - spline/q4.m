%x = [-4 -1 0]
%y = [0.56 -1.84 0]

%s = spline(x, [-0.37 y 1]) 
%yq = ppval(s,-2)

clear
x = [0 1 3 4 7 8 10 12]
y = [10 6 4 6 24 34 60 94]

flinha1 = (10-6)/(0-1);
flinha2 = (60-94)/(10-12);

s = spline(x, [flinha1 y flinha2])
yq = ppval(s,7.5);