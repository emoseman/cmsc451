#include "stdio.h"
#include "stdlib.h"

double sum_of_reciprocals(int n) {
    if (n <= 0) {
        return 0.0;
    }
    return 1.0 / n + sum_of_reciprocals(n - 1);
}

void main(int argc, char* argv[]) {
    int n = 3;
    if (argc > 1) {    
        n = atoi(argv[1]);
    }
    printf("n: %i, sum: %0.6f\n", n, sum_of_reciprocals(n));
}

