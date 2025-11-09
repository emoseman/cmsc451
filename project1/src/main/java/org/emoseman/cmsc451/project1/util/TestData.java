package org.emoseman.cmsc451.project1.util;

import java.security.SecureRandom;
import java.time.Instant;

/**
 * Evan Moseman
 * <p>
 * TODO
 * <p>
 * This utility class handles data generation for the benchmarks.
 */
public final class TestData {
    private TestData() {
    }

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generate random integers.
     */
    public static int[] generateRandomData(int count) {
        RANDOM.setSeed(Instant.now().getEpochSecond());

        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = RANDOM.nextInt();
        }
        return result;
    }
}
