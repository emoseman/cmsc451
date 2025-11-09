package org.emoseman.cmsc451.project1.util;

import java.util.List;

/**
 * Evan Moseman
 * <p>
 * // TODO
 */
public final class MathHelper {
    private MathHelper() {
    }

    /**
     * TODO
     */
    public static double calculateAverage(List<Long> values) {
        long total = 0;
        for (Long value : values) {
            total += value;
        }
        return total / (double) values.size();
    }

    /**
     * Calculate the coefficient of variation for a list of values.
     *
     * @param values The values.
     *
     * @return coefficient of variation
     */
    public static double calculateCoV(List<Long> values) {
        if (values == null || values.isEmpty()) {
            return 0.0D;
        }

        double mean = calculateAverage(values);
        if (mean == 0.0D) {
            return 0.0D;
        }

        double sumOfSquares = 0.0D;
        for (Long value : values) {
            double diff = value - mean;
            sumOfSquares += diff * diff;
        }

        double standardDeviation = Math.sqrt(sumOfSquares / values.size());
        return standardDeviation / Math.abs(mean);
    }
}
