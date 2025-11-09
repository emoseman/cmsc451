package org.emoseman.cmsc451.project1.util;

import java.security.SecureRandom;
import java.time.Instant;

/**
 * Evan Moseman
 * CMSC-451
 * Project 1
 * November 11, 2025
 * <p>
 * Generates pseudo-random integer arrays used as input data sets for the
 * benchmarks.
 */
public final class DataGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    private DataGenerator() {
    }

    /**
     * Generate random integers.
     *
     * @param count number of integers to create
     *
     * @return array populated with pseudo-random values
     */
    public static int[] generateData(int count) {
        RANDOM.setSeed(Instant.now().getNano());

        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = RANDOM.nextInt();
        }
        return result;
    }
}
