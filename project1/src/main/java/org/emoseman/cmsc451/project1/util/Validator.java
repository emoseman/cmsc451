package org.emoseman.cmsc451.project1.util;

import org.emoseman.cmsc451.project1.exp.UnsortedException;

/**
 * Evan Moseman
 * CMSC-451
 * Project 1
 * November 11, 2025
 * <p>
 * Provides validation helpers to ensure generated benchmark results remain
 * sorted as expected.
 */
public final class Validator {

    private Validator() {
    }

    /**
     * Verify that the supplied array is sorted in non-decreasing order.
     *
     * @param data array to inspect
     *
     * @throws UnsortedException if the array is found to be unsorted
     */
    public static void verifyDataOrder(int[] data)
        throws UnsortedException {
        for (int i = 0; i < data.length - 1; i++) {
            if (data[i] > data[i + 1]) {
                throw new UnsortedException(String.format(
                    "Data not sorted at index %d (values %d, %d)",
                    i,
                    data[i],
                    data[i + 1]));
            }
        }
    }
}
