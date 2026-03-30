import algorithms.Sort;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import static org.junit.jupiter.api.Assertions.assertTrue;

// This allows us to share state (the testData and results list) across all parameterized runs
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class Tournament {

    private List<SimpleEntry<Integer, Integer>> testData;
    private final List<SortResult> raceResults = new ArrayList<>();

    // Data class to hold the metrics for our final table
    private static class SortResult {
        String name;
        long timeNanos;
        boolean passed;
        String notes;

        SortResult(String name, long timeNanos, boolean passed, String notes) {
            this.name = name;
            this.timeNanos = timeNanos;
            this.passed = passed;
            this.notes = notes;
        }
    }

    @BeforeAll
    void setup() {
        System.out.println("Generating test data (n = 100,000)...");
        testData = Main.makeIntegerList(100_000);

        System.out.println("Warming up the JVM (Triggering JIT Compilation)...");
        // Use a small subset so the warmup is fast
        List<SimpleEntry<Integer, Integer>> warmupData = new ArrayList<>(testData.subList(0, 10000));
        List<Sort<Integer>> algs = provideAlgorithms();

        // Run every algorithm a few times to prime the JIT compiler and CPU caches
        for (int i = 0; i < 5; i++) {
            for (Sort<Integer> sorter : algs) {
                try {
                    sorter.sort(new ArrayList<>(warmupData));
                } catch (Throwable ignored) {
                    // Silently ignore any algorithm crashes during warmup
                }
            }
        }
        System.out.println("JVM warmed up. Starting sequential benchmark runs...\n");
    }

    // Your stream logic, isolated into the method source
    static List<Sort<Integer>> provideAlgorithms() {
        return Main.load().stream()
                .filter(alg -> !(alg.toString().equals("CosmicSort")))
                .filter(alg -> !(alg.toString().equals("BogoSort")))
                .map(alg -> {
                    @SuppressWarnings("unchecked")
                    Sort<Integer> s = (Sort<Integer>) alg;
                    return s;
                })
                .toList();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideAlgorithms")
    void testSingleAlgorithm(Sort<Integer> sorter) {
        // 1. Isolate the copy time so it doesn't affect the benchmark
        List<SimpleEntry<Integer, Integer>> copy = new ArrayList<>(testData);

        long start = System.nanoTime();
        long elapsed = 0;

        try {
            // 2. Run the sort
            List<SimpleEntry<Integer, Integer>> sorted = sorter.sort(copy);
            elapsed = System.nanoTime() - start;

            // 3. Verify correctness
            boolean isSorted = sorter.isSorted(sorted);

            // Record success or failure
            if (isSorted) {
                raceResults.add(new SortResult(sorter.toString(), elapsed, true, "OK"));
            } else {
                raceResults.add(new SortResult(sorter.toString(), elapsed, false, "FAILED: Not sorted"));
            }

            assertTrue(isSorted, sorter + " did not sort correctly!");

        } catch (Throwable t) {
            // Catch crashes (like StackOverflow or OutOfBounds) to put in the table
            if (elapsed == 0) elapsed = System.nanoTime() - start;
            raceResults.add(new SortResult(sorter.toString(), elapsed, false, "CRASHED: " + t.getClass().getSimpleName()));
            throw t; // Rethrow so JUnit actually marks the test as failed
        }
    }

    @AfterAll
    void printLeaderboard() {
        // Sort the results so the fastest algorithm appears at the top
        raceResults.sort(Comparator.comparingInt((SortResult r) -> r.passed ? 0 : 1)
                .thenComparingLong(r -> r.timeNanos));

        // Print the pretty ASCII table
        System.out.println("\n===============================================================================");
        System.out.println("                          ALGORITHM RACE RESULTS                               ");
        System.out.println("===============================================================================");
        System.out.printf("%-4s | %-35s | %-12s | %-15s%n", "Rank", "Algorithm", "Time (ms)", "Status");
        System.out.println("-------------------------------------------------------------------------------");

        int rank = 1;
        for (SortResult r : raceResults) {
            double timeMs = r.timeNanos / 1_000_000.0;

            // Format time nicely. If it failed, don't show a misleading fast time.
            String timeStr = r.passed ? String.format("%8.3f", timeMs) : "   ---   ";

            System.out.printf("%-4d | %-35s | %-12s | %-15s%n",
                    rank++, r.name, timeStr, r.notes);
        }
        System.out.println("===============================================================================\n");
    }
}