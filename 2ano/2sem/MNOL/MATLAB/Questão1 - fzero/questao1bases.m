%preencher com o valor do epsilon1 do enunciado
op = optimset('tolX', 2)         %porexemplo
[x,fval,exitflag,output] = fzero(@exerc1, [10,20], op)
%funcao
function f = exerc1(x)
    f = 2200*log(1.6e+5/(1.6e+5-2680*x))-9.8*x-1000; %exemplo
end

%----------%

%se for preciso ver o grafico da funcao
fplot(@exerc1,[0,50])
grid %vê a grelha
%resto

%----------%

%resolver com opcao de ver por iteracao
op1 = optimset('Display','iter')
[x,fval,exitflag,output] = fzero(@exerc1, 6, op1)

%resolver com opcao de tolfun
op2 = optimset('tolfun',1e-3)
[x,fval,exitflag,output] = fzero(@exerc1,6,op2)

%resolver com opcao de tolfun e tolx
op3 = optimset('tolfun',1e-3,'tolx',1e-3)
[x,fval,exitflag,output] = fzero(@exerc1, 6,op3)

%resto