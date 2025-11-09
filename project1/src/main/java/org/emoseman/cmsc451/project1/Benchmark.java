package org.emoseman.cmsc451.project1;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Benchmark
    implements Runnable {
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int RUN_COUNT = 40;
    private static final int DATASET_SIZES = 12;
    private static final int ELEMENT_COUNT_INC = 200;

    /// The thread that will perform the benchmark.
    ///
    /// @throws UnsortedException If the array isn't sorted after the algorithm
    ///                                                     runs it exits and
    ///
    ///
    ///
    ///
    ///                                             the
    ///
    ///
    ///
    ///                                                      whole
    ///
    ///
    ///                                benchmark is
    ///                                                     halted.
    @Override
    public void run()
        throws UnsortedException {

        AbstractSort[] sorters = {new MergeSort(), new ShellSort()};

        // Container for the sort run statistics
        Map<AbstractSort, List<RunStatistics>> testStatistics = new HashMap<>();
        for (AbstractSort sorter : sorters) {
            testStatistics.put(sorter, new ArrayList<>());
        }

        // for each data set size:
        for (int i = 1; i <= DATASET_SIZES; i++) { // dataset sizes

            int dataSetSize = i * ELEMENT_COUNT_INC;
            int[] testDataSource = generateRandomData(dataSetSize);
            System.out.printf("Generated %d ints\n", testDataSource.length);

            for (int j = 0; j < RUN_COUNT; j++) {  // run count

                // Test each algorithm
                for (AbstractSort sorter : sorters) {
                    int[]
                        testData =
                        Arrays.copyOf(testDataSource, testDataSource.length);

                    sorter.sort(testData);
                    verifyDataOrder(testData);

                    testStatistics.get(sorter)
                                  .add(new RunStatistics(dataSetSize,
                                                         sorter.getTime(),
                                                         sorter.getCount()));
                }
            }
        }

        summarizeAlgorithmStatistics(testStatistics);
    }

    /**
     * Print out a summary of the benchmark results.
     *
     * @param stats
     */
    private void summarizeAlgorithmStatistics(Map<AbstractSort, List<RunStatistics>> stats) {

        System.out.println("Ta da!");
    }

    /**
     * Verify that the elements in data are sorted.
     */
    private void verifyDataOrder(int[] data)
        throws UnsortedException {
        for (int i = 0; i < data.length - 1; i++) {
            if (data[i] > data[i + 1]) {
                // throw new UnsortedException();
            }
        }
    }

    /**
     * Generate random integers.
     */
    private int[] generateRandomData(int count) {
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = RANDOM.nextInt();
        }
        return result;
    }

    record RunStatistics(
        int elementCount, long runDuration, int operationCount) {

    }
}
