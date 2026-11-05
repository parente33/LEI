[x,fval,exitflag,output] = fminunc(fun,x0,options)

fun - é a função objetivo a minimizar
    – Especificar fun como uma função:
        [x,fval,exitflag,output] = fminunc(@fun,x0)
        function F = fun(x)
        F = ... ;
        end
    – Especificar fun como um identificador de função anónima:
        [x,fval,exitflag,output] = fminunc(@(x)...,x0)
    – Especificar a função fun:
        fun = @(x)...;
        [x,fval,exitflag,output] = fminunc(fun,x0)

x0 - é a aproximação inicial.

options - (opcional) opções para controlo do processo de otimização

Para ver as opções disponíveis no algoritmo por defeito (’quasi-newton’):
optimoptions(’fminunc’)
Para ver a estrutura das opções de otimização disponíveis:
optimset(’fminunc’)
Para ver as opções de um algoritmo específico, p.ex. ’trust-region”:
optimoptions(’fminunc’,’Algorithm’,’trust-region’)

Algorithm Escolhe o algoritmo a usar:
    ’quasi-newton’ (default)
    ’trust-region’ (requer que seja dado o gradiente).

Display Nível de apresentação
    off - não apresenta nada
    iter - resultados em cada iteração e uma mensagem de saída por defeito
    iter-detailed - resultados em cada iteração e uma mensagem técnica de saída
    notify - resultado se a função não convergir e uma mensagem de saída por defeito
    notify-detailed - resultado se a função não convergir e uma mensagem técnica de saída
    final - (default) apresenta apenas o resultado final e uma mensagem de saída por defeito
    final-detailed - apresenta apenas o resultado final e uma mensagem técnica de saída

FunValCheck Verifica se os valores da função objetivo são válidos
    on - apresenta erro se complex, Inf ou NaN.
    off - (default) não apresenta erro

MaxFunEvals Nº máximo de cálculos da função (default=100*NumVar)

MaxIter Nº máximo de iterações (default=400)

PlotFcn Representa graficamente medidas do progresso do algoritmo, ao longo das iterações.
    @optimplotx - representa o ponto atual.
    @optimplotfunccount - representa o número de cálculos da função objetivo.
    @optimplotfval - representa o valor da função objetivo.
    @optimplotstepsize - representa o comprimento do passo.
    @optimplotfirstorderopt - representa a medida de otimalidade de 1a ordem.

GradObj Gradiente da função objetivo definido pelo utilizador.
    on - usa o gradiente definido pelo utilizador.
    off - (default) aproxima o gradiente por diferenças finitas

TolFun Tolerância de paragem da função objetivo (default = 1e-6)

TolX Tolerância de paragem relativamente a x (default=1e-6)

Hessian Definição da Hessiana (default=off aproxima por dif. finitas)

HessUpdate Método para escolher a direção de procura ’quasi-Newton’.
    bfgs - (default) aproxima a Hessiana pela fórmula BFGS
    lbfgs - aproxima a Hessiana pela Low-memory BFGS (large-scale)
    dfp - aproxima a Hessiana pela fórmula DFP
    steepdesc - descida máxima

Argumentos de saída:

x - é a solução ótima do problema (minimizante).
fval - é o valor da função objetivo em x (mínimo).
exitflag - descreve valores de saída do fminunc.

exitflag
1 a magnitude do vetor gradiente é menor que a tolerância de otimalidade OptimalityTolerance.
2 um alteração em x é menor que a tolerância TolX.
3 uma alteração na função objetivo é menor que a tolerância TolFun.
5 um previsto decréscimo na função objetivo é menor que a tolerância TolFun.
0 o número de iterações excedeu o MaxIter ou o MaxFunEvals.
-1 indica que a função não convergiu para uma solução.
-3 o problema parece ser não limitado.

output - estrutura com informação acerca do processo de otimização.
iterations número de iterações realizadas
funcCount número de cálculos da função
firstorderopt valor da medida de otimalidade de 1a ordem (norma do gradiente na solução em x)
algorithm algoritmo usado
cgiterations nº total de iterações PCG (apenas para ’trust-region’)
lssteplength comprimento do passo da procura direta relativamente à direção de procura (apenas para ’quasi-newton’)
stepsize comprimento do passo final em x
message mensagem de saída.

Algoritmos de otimização
 Quasi-Newton Algorithm
O algoritmo quasi-Newton usa a fórmula de atualização BFGS (por defeito) pra aproximar a matriz
hessiana e um método de procura linear cúbica.
 Trust Region Algorithm
O algoritmo consiste no método de Newton ”interior-reflective” cuja técnica de globalização é baseada nas
regiões de confiança (trust-region algorithm). O uso deste algoritmo requer o fornecimento do gradiente
da função objetivo. E baseado no ’interior-reflective Newton method’, em que em cada iteração envolve a ´
solução aproximada de um sistema linear de grandes dimensões usando o método de gradientes conjugados
pré-condicionados(PCG).