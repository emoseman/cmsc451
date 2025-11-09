package org.emoseman.cmsc451.project1.alg;

import java.time.Duration;
import java.time.Instant;

/**
 * Evan Moseman
 * CMSC-451
 * Project 1
 * November 11, 2025
 *
 * Provides shared timing and operation-count instrumentation for concrete sorting implementations.
 */
public abstract class AbstractSort {

    // The count of critical operations for the sorting process.
    private int operationCount;

    // Elapsed time of the sort process
    private Duration elapsedTime;

    // The Instant of the sorting process start time
    private Instant sortStartInstance;

    /**
     * Returns the final operation count.
     *
     * @return number of critical operations performed
     */
    public int getCount() {
        return operationCount;
    }

    /**
     * Returns the elapsed time of the sort process.
     *
     * @return elapsed time, in nanoseconds
     */
    public long getTime() {
        return elapsedTime.toNanos();
    }

    /**
     * Sort the supplied array of integers.
     *
     * @param array array to be sorted in-place
     */
    public abstract void sort(int[] array);

    /**
     * Called prior to sort to initialize the timer and record the starting time
     * of the sort.
     */
    protected void startSort() {
        operationCount = 0;
        sortStartInstance = Instant.now();
    }

    /**
     * Called after the sort ends and will compute the elapsed time of the sort.
     */
    protected void endSort() {
        elapsedTime = Duration.between(sortStartInstance, Instant.now());
    }

    /**
     * Increment the count of critical operations.
     */
    protected void incrementCount() {
        operationCount++;
    }
}
