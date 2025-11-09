package org.emoseman.cmsc451.project1.util;

import org.emoseman.cmsc451.project1.alg.AbstractSort;
import org.emoseman.cmsc451.project1.model.RunCounter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Evan Moseman
 * CMSC-451
 * Project 1
 * November 11, 2025
 * <p>
 * Serializes benchmark run statistics for each sorter into CSV files for later
 * analysis.
 */
public class ResultsWriter {

    private static final String BASE_FILE_NAME = "benchmark-results-%s.csv";

    /**
     * Persist statistics for each sorter to a CSV file whose name includes the
     * sorter type.
     *
     * @param benchmarkStats map of sorter -> element count -> recorded runs
     */
    public void storeBenchmarkStatistics(Map<AbstractSort, Map<Integer, List<RunCounter>>> benchmarkStats) {
        // Foreach sorter
        for (final AbstractSort sorter : benchmarkStats.keySet()) {
            String
                filename =
                String.format(BASE_FILE_NAME,
                              sorter.getClass().getSimpleName());

            // foreach element count
            try (FileWriter fw = new FileWriter(filename)) {
                for (Integer elementCount : benchmarkStats.get(sorter)
                                                          .keySet()
                                                          .stream()
                                                          .sorted()
                                                          .toList()) {

                    String
                        rowData =
                        benchmarkStats.get(sorter)
                                      .get(elementCount)
                                      .stream()
                                      .map(s -> s.operationCount() +
                                                ":" +
                                                s.runDuration())
                                      .collect(Collectors.joining(","));

                    String
                        row =
                        String.format("%d,%s\n", elementCount, rowData);
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
