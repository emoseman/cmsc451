package org.emoseman.cmsc451.project1.alg;

/**
 * Evan Moseman
 * CMSC-451
 * Project 1
 * November 11, 2025
 * <p>
 * Shell sort implementation instrumented to capture operation counts and total
 * runtime.
 */
public class ShellSort
    extends AbstractSort {

    /**
     * Execute Shell sort on the provided array while recording timing and
     * operation metrics.
     *
     * @param array array to sort in-place
     */
    @Override
    public void sort(int[] array) {
        startSort();
        shellSort(array);
        endSort();
    }

    /**
     * Core Shell sort implementation.
     *
     * <p>Algorithm source: https://www.geeksforgeeks.org/dsa/shell-sort/</p>
     *
     * @param array array to sort in-place
     */
    private void shellSort(int[] array) {
        int arrayLength = array.length;

        // Start with a large gap and reduce the gap size
        for (int gap = arrayLength / 2; gap > 0; gap /= 2) {
            // Perform gapped insertion
            for (int i = gap; i < arrayLength; i++) {
                int temp = array[i];
                int j = i;

                // Shift elements that are greater than temp
                while (j >= gap) {
                    incrementCount(); // comparison between gap-spaced elements
                    if (array[j - gap] <= temp) {
                        break;
                    }

                    array[j] = array[j - gap];
                    j -= gap;
                }

                // finally place temp in its correct place.
                array[j] = temp;
            }
        }
    }
}
