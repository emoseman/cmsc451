#include "stdio.h"
#include "stdlib.h"

int smallest(int array[], int n, int j)
{
    if (j == n - 1)
    {
        return j;
    }
    int k = smallest(array, n, j + 1);
    return array[j] < array[k] ? j : k;
}

void sort(int array[], int n, int i)
{
    if (i < n)
    {
        int j = smallest(array, n, i);
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        sort(array, n, i + 1);
    }
}

void selectionSort(int array[], int n)
{
    sort(array, n, 0);
}

void main(int argc, char **argv)
{
    int n = atoi(argv[1]);

    int array[n];

    printf("Initial\n");
    for (int i; i<n; i++) {
        array[i] = rand() % (1000 - 0 + 1);
        printf("%d - %d\n", i, array[i]);
    }

    selectionSort(array, n);

    printf("Sorted:\n");
    for (int i = 0; i < n; i++)
    {
        printf("%d - %d\n", i, array[i]);
    }
}
