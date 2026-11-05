x = [1 2 3 4 5 6];
y = [1 1.25 1.75 2.25 3 3.15];

% spline cubica completa
%guardar 2 e penultimo pontos para estimar f’0 e f’n
xx = [1 3 4 6];
yy = [1 1.75 2.25 3.15];
f_linha_0 = (1.25-1)/(2-1);
f_linha_n = (3.15-3)/(6-5);
s_completa = spline(xx, [f_linha_0 yy f_linha_n]);
s_completa.coefs % mostra os segmento da spline
% s^1_3(x)=-0.0089844(x-1)^3 + 0.0804688(x-1)^2 + 0.2500000(x-1) + 1.00 % x [1,3]
% s^2_3(x)=0.0093750(x-3)^3 + 0.0265625(x-3)^2 + 0.4640625(x-3) + 1.75 % x [3,4]
% s^3_3(x)=-0.0511719(x-4)^3 + 0.0546875(x-4)^2 + 0.5453125(x-4) + 2.25 % x [4,6]
s_1_5 = spline(xx, [f_linha_0 yy f_linha_n],1.5) %s_3(1.5)
