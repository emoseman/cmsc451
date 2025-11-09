package org.emoseman.cmsc451.project1.util;

import org.emoseman.cmsc451.project1.alg.AbstractSort;
import org.emoseman.cmsc451.project1.model.RunCounter;
import org.emoseman.cmsc451.project1.model.Statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Evan Moseman
 * <p>
 * // TODO
 */
public final class SummarizeStatistics {
    private SummarizeStatistics() {
    }

    /**
     *
     * @param runStatistics
     *
     * @return
     */
    public static Map<AbstractSort, Map<Integer, Statistics>> summarize(Map<AbstractSort, Map<Integer, List<RunCounter>>> runStatistics) {

        Map<AbstractSort, Map<Integer, Statistics>> results = new HashMap<>();

        for (final Map.Entry<AbstractSort, Map<Integer, List<RunCounter>>> sorterEntry : runStatistics.entrySet()) {
            results.put(sorterEntry.getKey(), new HashMap<>());

            Map<Integer, List<RunCounter>>
                perSorterRunStats =
                sorterEntry.getValue();

            for (Integer elementCount : perSorterRunStats.keySet()) {

                List<RunCounter> runStats = perSorterRunStats.get(elementCount);

                List<Long>
                    durationValues =
                    runStats.stream()
                            .map(RunCounter::runDuration)
                            .collect(Collectors.toList());
                double avgDuration = calculateAverage(durationValues);
                double
                    durationCoefficient =
                    calculateCoefficientOfVariation(durationValues);

                List<Long>
                    countValues =
                    runStats.stream()
                            .map(RunCounter::operationCount)
                            .map(Long::valueOf)
                            .toList();
                double avrCount = calculateAverage(countValues);
                double
                    countCoefficient =
                    calculateCoefficientOfVariation(countValues);

                Statistics
                    stats =
                    new Statistics(avrCount,
                                   countCoefficient,
                                   avgDuration,
                                   durationCoefficient);

                results.get(sorterEntry.getKey()).put(elementCount, stats);
            }
        }

        return results;
    }

    private static double calculateAverage(List<Long> values) {
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
    private static double calculateCoefficientOfVariation(List<Long> values) {
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
