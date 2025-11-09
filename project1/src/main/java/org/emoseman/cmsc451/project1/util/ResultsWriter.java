package org.emoseman.cmsc451.project1.util;

import org.emoseman.cmsc451.project1.alg.AbstractSort;
import org.emoseman.cmsc451.project1.model.RunCounter;
import org.emoseman.cmsc451.project1.model.Statistics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Evan Moseman
 * CMSC-451
 * Project 1
 * November 11, 2025
 * <p>
 * This class handles writing the benchmark results statistics to a file.
 */
public class ResultsWriter {

    private static final String BASE_FILE_NAME = "benchmark-results-%s.csv";

    /**
     * Print out a summary of the benchmark results.
     *
     * @param stats
     */
    public void storeBenchmarkStatistics(Map<AbstractSort, Map<Integer, List<RunCounter>>> benchmarkStats) {
        System.out.println("benchmarkStats = " + benchmarkStats);
        Map<AbstractSort, Map<Integer, Statistics>>
            summarized =
            SummarizeStatistics.summarize(benchmarkStats);

        System.out.println("summarized = " + summarized);

        // Foreach sorter
        for (final AbstractSort sorter : summarized.keySet()) {
            String
                filename =
                String.format(BASE_FILE_NAME,
                              sorter.getClass().getSimpleName());

            // foreach element count
            try (FileWriter fw = new FileWriter(filename)) {
                for (Integer elementCount : summarized.get(sorter)
                                                      .keySet()
                                                      .stream()
                                                      .sorted()
                                                      .toList()) {

                    Statistics stats = summarized.get(sorter).get(elementCount);

                    String
                        row =
                        String.format("%d, %.2f, %.2f, %.2f, %.2f\n",
                                      elementCount,
                                      stats.averageCount(),
                                      stats.countCoefficient(),
                                      stats.averageTime(),
                                      stats.timeCoefficient());
                    fw.write(row);
                }
            }
            catch (IOException e) {
                System.err.printf(
                    "Failed to write benchmark statistics to file: %s - %s",
                    filename,
                    e.getMessage());
            }
        }
    }
}
