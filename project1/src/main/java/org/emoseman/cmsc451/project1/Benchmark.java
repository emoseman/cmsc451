package org.emoseman.cmsc451.project1;

import org.emoseman.cmsc451.project1.alg.AbstractSort;
import org.emoseman.cmsc451.project1.alg.MergeSort;
import org.emoseman.cmsc451.project1.alg.ShellSort;
import org.emoseman.cmsc451.project1.exp.UnsortedException;
import org.emoseman.cmsc451.project1.model.RunCounter;
import org.emoseman.cmsc451.project1.util.DataGenerator;
import org.emoseman.cmsc451.project1.util.ResultsWriter;
import org.emoseman.cmsc451.project1.util.Validator;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Evan Moseman
 * CMSC-451
 * Project 1
 * November 11, 2025
 * <p>
 * Coordinates the warmup, benchmarking runs, and persistence of statistics for
 * the configured sorting algorithms.
 */
public class Benchmark
    implements Runnable {

    // Benchmark parameters
    private static final int RUN_COUNT = 40;
    private static final int DATASET_SIZES = 12;
    private static final int ELEMENT_COUNT_INC = 500;

    // Warmup parameters
    private static final int WARMUP_ELEMENT_COUNT = ELEMENT_COUNT_INC * 20;
    private static final int MIN_WARMUP_ITERATIONS = 5;
    private static final int MAX_WARMUP_ITERATIONS = 1000;
    private static final double WARMUP_STABILITY_THRESHOLD = 0.005;
    private static final int REQUIRED_STABLE_ITERATIONS = 3;

    private static final AbstractSort[]
        sorters =
        {new MergeSort(), new ShellSort()};

    /**
     * Entry point for the benchmark thread: performs warmup, runs each data
     * size, and persists the aggregated results.
     *
     * @throws UnsortedException if any sorter fails to produce sorted output
     */
    @Override
    public void run()
        throws UnsortedException {

        // Container for the sort run statistics for each sorter
        Map<AbstractSort, Map<Integer, List<RunCounter>>>
            benchmarkResults =
            initializeResultMap();

        // Warm up JVM/JIT
        performWarmup(sorters);

        // for each data set size:
        for (int i = 1; i <= DATASET_SIZES; i++) { // dataset sizes

            int dataSetSize = i * ELEMENT_COUNT_INC;

            benchmarkResults.putAll(executeRunsForSize(dataSetSize,
                                                       benchmarkResults));
        }

        new ResultsWriter().storeBenchmarkStatistics(benchmarkResults);
    }

    /**
     * Perform sorting runs until the difference between consecutive runs is
     * below the configured threshold or the iteration limit is reached.
     *
     * @param sorters sorting algorithm implementations to warm up
     *
     * @throws UnsortedException if a sorter fails validation during warmup
     */
    private void performWarmup(AbstractSort[] sorters)
        throws UnsortedException {

        int warmupSize = WARMUP_ELEMENT_COUNT;
        int[] warmupData = DataGenerator.generateData(warmupSize);
        Map<AbstractSort, Duration>
            previousDurations =
            Arrays.stream(sorters)
                  .collect(Collectors.toMap(Function.identity(),
                                            sorter -> Duration.ZERO));
        Map<AbstractSort, Integer>
            stableCounts =
            Arrays.stream(sorters)
                  .collect(Collectors.toMap(Function.identity(), sorter -> 0));

        for (int iteration = 1;
             iteration <= MAX_WARMUP_ITERATIONS;
             iteration++) {

            boolean allStable = true;

            for (AbstractSort sorter : sorters) {
                Instant iterationStart = Instant.now();
                runSorter(sorter, warmupData);

                Duration
                    duration =
                    Duration.between(iterationStart, Instant.now());

                Duration previousDuration = previousDurations.get(sorter);

                boolean
                    hasBaseline =
                    previousDuration != null && !previousDuration.isZero();
                boolean meetsMinimum = iteration >= MIN_WARMUP_ITERATIONS;
                boolean isStable = false;

                if (hasBaseline && meetsMinimum) {
                    double
                        percentDifference =
                        Math.abs(duration.toNanos() -
                                 previousDuration.toNanos()) /
                        (double) previousDuration.toNanos();
                    isStable = percentDifference <= WARMUP_STABILITY_THRESHOLD;
                }

                if (isStable) {
                    int newStableCount = stableCounts.get(sorter) + 1;
                    stableCounts.put(sorter, newStableCount);
                    if (newStableCount < REQUIRED_STABLE_ITERATIONS) {
                        allStable = false;
                    }
                }
                else {
                    stableCounts.put(sorter, 0);
                    allStable = false;
                }

                previousDurations.put(sorter, duration);
            }

            if (allStable) {
                System.out.printf("Warmup stabilized after %d iterations.%n",
                                  iteration);
                return;
            }
        }

        System.out.printf(
            "Warmup reached max iterations (%d) without full stabilization%n",
            MAX_WARMUP_ITERATIONS);
    }

    /**
     * Run the sorter algorithm on a copy of the provided test data and validate
     * the sorted result.
     *
     * @param sorter     algorithm to execute
     * @param sourceData dataset to copy before sorting
     *
     * @throws UnsortedException if the sorter does not yield ordered data
     */
    private void runSorter(AbstractSort sorter, int[] sourceData)
        throws UnsortedException {

        int[] testData = Arrays.copyOf(sourceData, sourceData.length);
        sorter.sort(testData);
        Validator.verifyDataOrder(testData);
    }

    /**
     * Initialize the nested map structure that holds run statistics for each
     * sorter keyed by element count.
     *
     * @return empty results map ready for population
     */
    private Map<AbstractSort, Map<Integer, List<RunCounter>>> initializeResultMap() {
        Map<AbstractSort, Map<Integer, List<RunCounter>>>
            benchmarkResults =
            new HashMap<>();
        for (AbstractSort sorter : sorters) {
            benchmarkResults.put(sorter, new HashMap<>());
        }
        return benchmarkResults;
    }

    /**
     * Execute the configured number of runs for a given dataset size and update
     * the accumulated statistics map.
     *
     * @param dataSetSize      number of elements in the generated dataset
     * @param benchmarkResults map of sorter -> element count -> run stats to
     *                         update
     *
     * @return the updated benchmark map
     *
     * @throws UnsortedException if any sorter produces unsorted output
     */
    private Map<AbstractSort, Map<Integer, List<RunCounter>>> executeRunsForSize(
        int dataSetSize,
        Map<AbstractSort, Map<Integer, List<RunCounter>>> benchmarkResults)
        throws UnsortedException {

        for (int j = 0; j < RUN_COUNT; j++) {
            int[] testDataSource = DataGenerator.generateData(dataSetSize);
            for (AbstractSort sorter : sorters) {
                runAndRecord(sorter,
                             testDataSource,
                             benchmarkResults.get(sorter),
                             dataSetSize);
            }
        }
        return benchmarkResults;
    }

    /**
     * Run a sorter using the supplied data and append the resulting statistics
     * to the per-size collection.
     *
     * @param sorter      algorithm being benchmarked
     * @param sourceData  dataset that will be copied before sorting
     * @param stats       map of dataset size to recorded runs for this sorter
     * @param dataSetSize number of elements in the current dataset
     *
     * @throws UnsortedException if the sorter fails validation
     */
    private void runAndRecord(AbstractSort sorter,
                              int[] sourceData,
                              Map<Integer, List<RunCounter>> stats,
                              int dataSetSize)
        throws UnsortedException {

        runSorter(sorter, sourceData);
        stats.putIfAbsent(dataSetSize, new ArrayList<>());
        stats.get(dataSetSize)
             .add(new RunCounter(sorter.getTime(), sorter.getCount()));
    }
}
