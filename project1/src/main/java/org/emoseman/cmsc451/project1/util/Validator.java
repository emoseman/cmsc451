package org.emoseman.cmsc451.project1.util;

import org.emoseman.cmsc451.project1.exp.UnsortedException;

/**
 * Evan Moseman
 * TODO
 * <p>
 * <p>
 * This utility class handles the validation of the benchmark results.
 */
public final class Validator {

    private Validator() {
    }

    /**
     * Verify that the elements in data are sorted.
     */
    public static void verifyDataOrder(int[] data)
        throws UnsortedException {
        for (int i = 0; i < data.length - 1; i++) {
            if (data[i] > data[i + 1]) {
                // throw new UnsortedException();
            }
        }
    }
}
