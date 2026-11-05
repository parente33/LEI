/* Objective function */
min: tfinal;

/* Variable bounds */
t1 >= 0;
t2 >= 0;
t3 >= t1 + 3;
t4 >= t2 + 5;
t4 >= t3 + 6;
tfinal >= t4 + 4;