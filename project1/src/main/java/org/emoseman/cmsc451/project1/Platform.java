package org.emoseman.cmsc451.project1;

import net.openhft.affinity.Affinity;
import net.openhft.affinity.AffinityLock;
import org.emoseman.cmsc451.project1.exp.UnsortedException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Evan Moseman
 * <p>
 * CMSC-451
 * <p>
 * Professor Jiang
 * November 11, 2025
 *
 * <p>
 * Entry point into the program that will perform benchmarking.
 * <p>
 * This class will establish CPU affinity if it is available.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class Platform {

    private static final ExecutorService
        threadPool =
        Executors.newSingleThreadExecutor();

    /**
     * Use processor affinity if it is available.
     *
     * @param args
     */
    public static void main(String[] args) {
        boolean affinityAvailable = Affinity.isJNAAvailable();
        System.out.println("Affinity JNA available = " + affinityAvailable);

        // If processor affinity is available lock the benchmark thread to that core.
        if (affinityAvailable) {
            threadPool.execute(() -> {
                try (AffinityLock al = AffinityLock.acquireCore()) {
                    new Benchmark().run();
                }
                catch (UnsortedException e) {
                    System.err.printf("%s - %s", e.getClass(), e.getMessage());
                }
            });

            threadPool.shutdown();
            try {
                threadPool.awaitTermination(1, TimeUnit.HOURS);
            }
            catch (InterruptedException e) {
                //ignored
            }
        }
        else {
            try {
                threadPool.execute(new Benchmark());
                threadPool.shutdown();
                threadPool.awaitTermination(1, TimeUnit.HOURS);
            }
            catch (UnsortedException e) {
                System.err.printf("%s - %s", e.getClass(), e.getMessage());
            }
            catch (InterruptedException e) {
                // ignored
            }
        }
    }
}
