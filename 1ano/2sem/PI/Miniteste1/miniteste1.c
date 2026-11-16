#include <stdio.h>


int main() {
    long x = 15, y = 13;
    int v = 5;
    char d = 's';

    long max_dist = 0;
    long current_dist = 0;
    char current_dir = d; 

    for (int t = 1; t <= 10000; t++) {
        if (t % 3 == 0 && t % 5 == 0) {
            if (d == 'n') {
                d = 'e';
            } else if (d == 'e') {
                d = 's';
            } else if (d == 's') {
                d = 'w';
            } else {
                d = 'n';
            }

            if (current_dist > max_dist) {
                max_dist = current_dist;
            }
            current_dist = 0;
            current_dir = d;

        } else if (t % 3 == 0) {
            v += 2;
        } else if (t % 5 == 0) {
            v -= 1;
        } else {
            if (d == 'n') {
                y += v;
            } else if (d == 'e') {
                x += v;
            } else if (d == 's') {
                y -= v;
            } else {
                x -= v;
            }

            if (d == current_dir) {
                current_dist += v;
            } else {
                if (current_dist > max_dist) {
                    max_dist = current_dist;
                }
                current_dist = v;
                current_dir = d;
            }
        }
    }

    if (current_dist > max_dist) {
        max_dist = current_dist;
    }

    printf("Maior distância: %ld\n", max_dist);
    printf("x = %ld\n", x);

    return 0;
}