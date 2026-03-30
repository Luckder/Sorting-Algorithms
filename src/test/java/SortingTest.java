import algorithms.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;
import java.util.AbstractMap.SimpleEntry;

import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

// Author: David Chan (Luckder)

public final class SortingTest {

    private static final List<Sort<? extends Comparable<?>>> algorithms = Main.load();

    static Stream<Sort<Integer>> provideAlgorithms() {
        return algorithms.stream()
                .filter(alg -> !(alg.toString().equals("CosmicSort")))
                .filter(alg -> !(alg.toString().equals("BogoSort")))
                .map(alg -> {
                    @SuppressWarnings("unchecked")
                    Sort<Integer> s = (Sort<Integer>) alg;
                    return s;
                });
    }

    @Test
    void sortTestSpecific() {
        Random rng = new Random();
        @SuppressWarnings("unchecked")
        Sort<Integer> sorter = (Sort<Integer>) algorithms.stream()
                .filter(alg -> (alg.toString().equals("MergeSort (Iterative)")))
                .toList()
                .get(0);
        System.out.println("Testing " + sorter + " with 100,000 elements...");
        assertTrue(sorter.isSorted(sorter.sort(Main.makeIntegerList(1001))), "List has to be sorted!");
    }

    @Test
    void sortTestOne() {
        Random rng = new Random();
        @SuppressWarnings("unchecked")
        Sort<Integer> sorter = (Sort<Integer>) algorithms.stream()
                .filter(alg -> !(alg.toString().equals("CosmicSort")))
                .filter(alg -> !(alg.toString().equals("BogoSort")))
                .toList()
                .get(rng.nextInt(algorithms.size()));
        System.out.println("Testing " + sorter + " with 100,000 elements...");
        assertTrue(sorter.isSorted(sorter.sort(Main.makeIntegerList(100001))), "List has to be sorted!");
    }

    @ParameterizedTest
    @MethodSource("provideAlgorithms")
    void sortTestAll(Sort<Integer> sorter) {
        List<SimpleEntry<Integer, Integer>> fresh = sorter.sort(Main.makeIntegerList(10000));
        assertTrue(sorter.isSorted(fresh), sorter + " did not sort correctly!");
    }

    @ParameterizedTest
    @MethodSource("provideAlgorithms")
    void sortPerformanceReport(Sort<Integer> sorter) {
        int size = 100_000;
        List<SimpleEntry<Integer, Integer>> list = Main.makeIntegerList(size);

        long start = System.nanoTime();
        sorter.sort(list);
        long elapsed = System.nanoTime() - start;

        System.out.printf("%s | n = %d | %s%n", sorter, size, Main.getTime(elapsed));
        // No assertion — this is purely informational
    }

    @ParameterizedTest
    @MethodSource("provideAlgorithms")
    void sortTimeLimitTest(Sort<Integer> sorter) {
        int size = 100_000;
        List<SimpleEntry<Integer, Integer>> list = Main.makeIntegerList(size);

        // Fails the test if sorting takes longer than the limit
        assertTimeout(Duration.ofSeconds(10), () -> {
            sorter.sort(list);
        }, sorter + " exceeded the time limit!");
    }

    @Test
    void sortRace() throws InterruptedException {
        int limit = 100_000;
        List<SimpleEntry<Integer, Integer>> original = Main.makeIntegerList(limit);
        Instant raceStart = Instant.now();

        List<Sort<Integer>> racers = algorithms.stream()
                .filter(alg -> !(alg.toString().equals("CosmicSort")))
                .filter(alg -> !(alg.toString().equals("BogoSort")))
                .map(alg -> {
                    @SuppressWarnings("unchecked")
                    Sort<Integer> s = (Sort<Integer>) alg;
                    return s;
                })
                .toList();

        // Each thread gets its own copy, all copied before any thread starts
        Map<Sort<Integer>, List<SimpleEntry<Integer, Integer>>> copies = new LinkedHashMap<>();
        for (Sort<Integer> sorter : racers) {
            copies.put(sorter, new ArrayList<>(original));
        }

        CountDownLatch ready  = new CountDownLatch(racers.size()); // All threads signal when ready
        CountDownLatch start  = new CountDownLatch(1);             // Main fires the starting gun
        CountDownLatch finish = new CountDownLatch(racers.size()); // Main waits for all to finish

        ExecutorService pool = Executors.newFixedThreadPool(racers.size());

        List<Throwable> raceErrors = Collections.synchronizedList(new ArrayList<>());

        for (Sort<Integer> sorter : racers) {
            List<SimpleEntry<Integer, Integer>> copy = copies.get(sorter);

            pool.submit(() -> {
                try {
                    ready.countDown();
                    start.await();

                    long threadStart = System.nanoTime();
                    List<SimpleEntry<Integer, Integer>> sorted = sorter.sort(copy);
                    long elapsed = System.nanoTime() - threadStart;

                    String timestamp = DateTimeFormatter
                            .ofPattern("HH:mm:ss.SSS")
                            .format(LocalTime.now());

                    System.out.printf("[%s] %-35s finished in %s%n",
                            timestamp, sorter.toString(), Main.getTime(elapsed));

                    assertTrue(sorter.isSorted(sorted), sorter + " did not sort correctly!");

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Throwable t) {
                    // Catch assertion errors or unexpected runtime exceptions
                    raceErrors.add(t);
                } finally {
                    // GUARANTEE the latch counts down even if the algorithm crashes
                    finish.countDown();
                }
            });
        }

        ready.await();   // Wait until every thread is staged and ready
        System.out.println("\n>>> RACE START — " + racers.size() + " algorithms, n = " + limit + "\n");
        start.countDown(); // Fire!

        boolean allFinished = finish.await(2, TimeUnit.MINUTES);
        pool.shutdown();

        Duration totalRaceTime = Duration.between(raceStart, Instant.now());
        System.out.printf("%n>>> RACE OVER — wall clock time: %.3f s%n",
                totalRaceTime.toMillis() / 1000.0);

        assertTrue(raceErrors.isEmpty(), "Algorithms failed during the race: " + raceErrors);
    }

}