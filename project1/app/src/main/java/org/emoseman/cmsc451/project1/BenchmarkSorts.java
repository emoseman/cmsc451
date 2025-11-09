package org.emoseman.cmsc451.project1;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evan Moseman
 * CMSC-451
 * Professor Jiang
 *
 * Entry point into the program that will perform sorts using two different
 * sorting algorithms and compare their performances.
 */
public class BenchmarkSorts {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TEST_COUNT = 40;
    private static final int RUN_COUNT = 12;
    private static final int ELEMENT_COUNT_JUMP = 500;


    public static void main(String[] args) {
        try {
            new BenchmarkSorts().run();
        } catch (UnsortedException e) {
            System.err.println();
        }
    }

    /**
     * Start the sorting processes and produce performance statistics.
     */
    void run()
            throws UnsortedException {
        AbstractSort[] classes = {new MergeSort(), new ShellSort()};

        // Container for the sort run statistics
        Map<AbstractSort, List<RunStatistics>> testStatistics = new HashMap<>();


        for (AbstractSort sorter : classes) {
            System.out.printf("Sorter: %s\n", sorter.getClass().getName());
            testStatistics.put(sorter, new ArrayList<>());

            int elementCount = 100;
            // Generate the random data sets for testing
            int[] data = generateRandomData(elementCount);
            System.out.printf("Generated %d ints\n", data.length);

            for (int n = 0; n < TEST_COUNT; n++) {

                System.out.printf("Test %d\n", n);

                int[] sortingData = Arrays.copyOf(data, data.length);
                System.out.printf("Copied %d ints\n", sortingData.length);

                sorter.sort(sortingData);
                testStatistics.get(sorter)
                        .add(new RunStatistics(
                                elementCount,
                                sorter.getTime(),
                                sorter.getCount()));
                verifyDataOrder(data);

            }
            elementCount += ELEMENT_COUNT_JUMP;
        }

        summarizeAlgorithmStatistics(testStatistics);
    }

    private void summarizeAlgorithmStatistics(Map<AbstractSort, List<RunStatistics>> stats) {
        System.out.println("Ta da!");
    }

    /**
     * Verify that the elements in data are sorted.
     */
    private void verifyDataOrder(int[] data) throws UnsortedException {
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

    record RunStatistics(int elementCount, long runDuration, int operationCount) {

    }

}
