/* Objective function */
min: 5 x1 + 5.5 x2 + 6.5 x3 + 6.5 x4 + 6.5 x5 + 6.5 x6 + 6 x7;

/* Variable bounds */
segfeira:  x1 + x2 + x3 + x4 + x5           >= 10;
terfeira:      x2 + x3 + x4 + x6 + x7       >= 16;
quafeira:           x3 + x4 + x5 + x6 + x7  >= 12;
quiffeira: x1 +           x4 + x5 + x6 + x7 >= 8;
sexfeira:  x1 + x2 +           x5 + x6 + x7 >= 15;
Sabado:    x1 + x2 + x3 +           x6 + x7 >= 18;
Domingo:   x1 + x2 + x3 + x4 +           x7 >= 12;

int x1, x2, x3, x4, x5, x6, x7;