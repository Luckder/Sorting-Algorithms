import algorithms.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;
import java.util.AbstractMap.SimpleEntry;

// Author: David Chan (Luckder)

public final class SortingTest {

    private static final List<Sort<? extends Comparable<?>>> algorithms = Main.load();

    static Stream<Sort<Integer>> provideAlgorithms() {
        return algorithms.stream()
                .filter(alg -> !(alg instanceof CosmicSort))
                .filter(alg -> !(alg instanceof BogoSort))
                .map(alg -> {
                    @SuppressWarnings("unchecked")
                    Sort<Integer> s = (Sort<Integer>) alg;
                    return s;
                });
    }

    @Test
    void sortTestOne() {
        Random rng = new Random();
        @SuppressWarnings("unchecked")
        Sort<Integer> sorter = (Sort<Integer>) algorithms.stream()
                .filter(alg -> !(alg instanceof CosmicSort))
                .filter(alg -> !(alg instanceof BogoSort))
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

}