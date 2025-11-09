package org.emoseman.cmsc451.project1.alg;

import java.util.Arrays;

/**
 * Evan Moseman
 * CMSC-451
 * Professor Jiang
 * November 11, 2025
 * <p>
 * An implementation of the merge sort algorithm that will record duration and
 * operation count during the sorting process.
 */
public class MergeSort
    extends AbstractSort {

    @Override
    public void sort(int[] array) {
        startSort();

        // perform sort
        mergeSort(array, 0, array.length - 1);

        endSort();
    }

    /**
     * Recursive merge sort implementation.
     * <p>
     * Algorithm source: https://www.geeksforgeeks.org/dsa/merge-sort/
     *
     * @param a Array of ints
     * @param n
     */
    private void mergeSort(int[] a, int leftIndex, int rightIndex) {
        if (leftIndex < rightIndex) {
            int middleIndex = leftIndex + (rightIndex - leftIndex) / 2;

            // Sort halves
            mergeSort(a, leftIndex, middleIndex);
            mergeSort(a, middleIndex + 1, rightIndex);

            // Merge the sorted halves
            merge(a, leftIndex, middleIndex, rightIndex);
        }
    }

    /**
     * Merge two subarrays.
     *
     * @param array
     * @param leftIndex
     * @param middleIndex
     * @param rightIndex
     */
    private void merge(int[] array,
                       int leftIndex,
                       int middleIndex,
                       int rightIndex) {
        // Determine size of two subarrays to marge
        int leftSubarrayLength = middleIndex - leftIndex + 1;
        int rightSubarrayLength = rightIndex - middleIndex;

        // Create copies of subarrays
        int[]
            leftSubarray =
            Arrays.copyOfRange(array, leftIndex, middleIndex + 1);
        int[]
            rightSubarray =
            Arrays.copyOfRange(array, middleIndex + 1, rightIndex + 1);

        // Initial merge of subarrays
        int i = 0, j = 0;
        int k = leftIndex;
        while (i < leftSubarrayLength && j < rightSubarrayLength) {
            incrementCount(); // critical operation
            if (leftSubarray[i] <= rightSubarray[j]) {
                array[k] = leftSubarray[i];
                i++;
            }
            else {
                array[k] = rightSubarray[j];
                j++;
            }
            k++;
        }

        // Copy remaining elements of leftIndex subarray if any
        while (i < leftSubarrayLength) {
            array[k] = leftSubarray[i];
            i++;
            k++;
        }

        // Copy remaining elements of rightIndex subarray if any
        while (j < rightSubarrayLength) {
            array[k] = rightSubarray[j];
            j++;
            k++;
        }
    }
}
