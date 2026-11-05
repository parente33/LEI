op = optimset('tolX', 1e-2)
[x,fval,exitflag,output] = fzero(@exerc1, 2, op)
function f = exerc1(x)
    f = cos(2*x) + 4*x - 8
end
