package org.emoseman.cmsc451.project1;

import net.openhft.affinity.AffinityLock;
import software.chronicle.enterprise.internals.impl.NativeAffinity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Evan Moseman
 * <p>
 * CMSC-451
 * <p>
 * Professor Jiang
 *
 * <p>
 * Entry point into the program that will perform benchmarking.
 * <p>
 * This class will establish CPU affinity if it is available.
 */
public class Platform {

    private static final ExecutorService threadPool = Executors.newSingleThreadExecutor();

    /**
     * Use processor affinity if it is available.
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Affinity.LOADED = " + NativeAffinity.LOADED);

        if (NativeAffinity.LOADED) {
            // Target the last CPU
            int cpuTarget = Runtime.getRuntime().availableProcessors() - 1;

            try (AffinityLock al = AffinityLock.acquireCore(true)) {
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
