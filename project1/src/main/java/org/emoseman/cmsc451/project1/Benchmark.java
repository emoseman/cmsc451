package org.emoseman.cmsc451.project1;

import org.emoseman.cmsc451.project1.alg.AbstractSort;
import org.emoseman.cmsc451.project1.alg.MergeSort;
import org.emoseman.cmsc451.project1.alg.ShellSort;
import org.emoseman.cmsc451.project1.exp.UnsortedException;
import org.emoseman.cmsc451.project1.model.RunCounter;
import org.emoseman.cmsc451.project1.util.ResultsWriter;
import org.emoseman.cmsc451.project1.util.TestData;
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
 *
 *
 * TODO
 */
public class Benchmark
    implements Runnable {

    // Benchmark parameters
    private static final int RUN_COUNT = 40;
    private static final int DATASET_SIZES = 12;
    private static final int ELEMENT_COUNT_INC = 300;

    // Warmup parameters
    private static final int WARMUP_ELEMENT_COUNT = 200;
    private static final int MIN_WARMUP_ITERATIONS = 5;
    private static final int MAX_WARMUP_ITERATIONS = 200;
    private static final double WARMUP_STABILITY_THRESHOLD = 0.02;
    private static final int REQUIRED_STABLE_ITERATIONS = 3;

    private static final AbstractSort[]
        sorters =
        {new MergeSort(), new ShellSort()};

    /**
     * This thread performs the benchmark.
     *
     * @throws UnsortedException If the end results of the algorithm don't
     *                           yield
     *                           sorted results, then the benchmark is invalid
     *                           and the program exits.
     */
    @Override
    public void run()
        throws UnsortedException {

        // Container for the sort run statistics for each sorter
        Map<AbstractSort, Map<Integer, List<RunCounter>>>
            benchmarkResults =
            new HashMap<>();
        for (AbstractSort sorter : sorters) {
            benchmarkResults.put(sorter, new HashMap<>());
        }

        // Warm up JVM/JIT
        performWarmup(sorters);

        // for each data set size:
        for (int i = 1; i <= DATASET_SIZES; i++) { // dataset sizes

            int dataSetSize = i * ELEMENT_COUNT_INC;
            int[] testDataSource = TestData.generateRandomData(dataSetSize);

            for (int j = 0; j < RUN_COUNT; j++) {  // run count

                // Test each algorithm
                for (AbstractSort sorter : sorters) {
                    runAndRecord(sorter,
                                 testDataSource,
                                 benchmarkResults.get(sorter),
                                 dataSetSize);
                }
            }
        }

        new ResultsWriter().storeBenchmarkStatistics(benchmarkResults);
    }

    /**
     * Perform sorting runs until the difference between each run is below 2% or
     * until the number of attempts reaches its limit.
     *
     * @param sorters The sorting algorithm implementations.
     *
     * @throws UnsortedException
     */
    private void performWarmup(AbstractSort[] sorters)
        throws UnsortedException {

        int warmupSize = ELEMENT_COUNT_INC * 20;
        int[] warmupData = TestData.generateRandomData(warmupSize);
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
     * Run the sorter algorithm on a copy of the test data, and
     * validate the results.
     *
     * @param sorter
     * @param sourceData
     *
     * @throws UnsortedException
     */
    private void runSorter(AbstractSort sorter, int[] sourceData)
        throws UnsortedException {

        int[] testData = Arrays.copyOf(sourceData, sourceData.length);
        sorter.sort(testData);
        Validator.verifyDataOrder(testData);
    }

    /**
     * Run the sorter algorithm and store the statistics.
     *
     * @param sorter
     * @param sourceData
     * @param stats
     * @param dataSetSize
     *
     * @throws UnsortedException
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
