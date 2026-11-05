/* Objective function */
max: xfinal;

/* Variable bounds */
primano: x11 + x21 = 1;
segano: x12 + x22 = 1.7 x11;
terano: x13 + x23 = 1.7 x12 + 3 x21;
quarano: x14 + x24 = 1.7 x13 + 3 x22;
quinano: x15 + x25 = 1.7 x14 + 3 x23;
final: 1.7 x15 + 3 x24 = xfinal;