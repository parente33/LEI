x = [10 12 18 27 30 34];
y = [20 18 15 9 12 10];

%%spline cubica completa
%guardar 2 e penultimo pontos para estimar f’0 e f’n
xx = [10 18 27 34];
yy = [20 15 9 10];
f_linha_0 = (20-18)/(10-12);
f_linha_n = (12-10)/(30-34);
s_completa = spline(xx, [f_linha_0 yy f_linha_n]);
%para ver os segmentos da spline, fazer:
s_completa.coefs
%a 1ª linha corresponde aos coefcientes do segmento 1 entre [10,18]
s^1-3(x) = -0.0070(x-10)^3 + 0.1027(x-10)^2 -1.0000(x-10) + 20 %para x [10,18]
s^2-3(x) = 0.0076(x-18)^3 -0.0648(x-18)^2 -0.6966(x-18) + 15 %para x [18,27]
s^3-3(x) = -0.0165(x-27)^3 + 0.1395(x-27)^2 -0.0240(x-27) + 9 %para x [27,34]
%para calcular o valor de s(29), fazer:
s_29 = spline(xx, [f_linha_0 yy f_linha_n],29) %s_3(29)