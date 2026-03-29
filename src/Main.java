import algorithms.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

// Author: David Chan (Luckder)

public final class Main {
    private static final List<Sort<? extends Comparable<?>>> algorithms = load();

    private static List<Sort<? extends Comparable<?>>> load() {
        List<Sort<? extends Comparable<?>>> list = new ArrayList<>();

        try (ScanResult scan = new ClassGraph()
                .enableClassInfo()
                .acceptPackages("algorithms")
                .scan()) {

            for (ClassInfo info : scan.getSubclasses(Sort.class)) {
                if (!info.isAbstract()) {
                    Sort<? extends Comparable<?>> instance = (Sort<? extends Comparable<?>>) info.loadClass()
                            .getDeclaredConstructor()
                            .newInstance();
                    list.add(instance);
                }
            }

            return list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sorting algorithms", e);
        }
    }

    private static String getTime(long nanos) {
        // I ain't gonna do days
        if (nanos > 3_600_000_000_000L) { return nanos / 3_600_000_000_000.0 + " h"; }
        else if (nanos > 60_000_000_000L)    { return nanos / 60_000_000_000.0    + " min"; }
        else if (nanos > 1_000_000_000L)     { return nanos / 1_000_000_000.0     + " s"; }
        else { return nanos / 1_000_000.0 + " ms"; }
    }

    public static <T extends Comparable<T>> void run(List<SimpleEntry<T, Integer>> list) {

        int size = list.size();

        if (size <= 10000) {
            System.out.println("\nOriginal List: " + list + "\n");
        } else {
            System.out.println("\nOriginal List is too long to print! Size is " + size + " elements.\n");
        }

        System.out.println("Skipping algorithms.CosmicSort for obvious reasons...");
        System.out.println("Skipping algorithms.BogoSort if List length > 10 elements...");
        System.out.println("WARNING: algorithms.CountingSort can get very angsty!\n");

        for (Sort<?> sorterRaw : algorithms) {

            if (sorterRaw instanceof CosmicSort) continue;
            if (sorterRaw instanceof BogoSort && size > 10) {
                System.out.println("BogoSort was skipped!\n");
                continue;
            }

            @SuppressWarnings("unchecked")
            Sort<T> sorter = (Sort<T>) sorterRaw;

            System.out.println("Using " + sorter.toString() + "...");

            List<SimpleEntry<T, Integer>> copy = new ArrayList<>(list);

            long start = System.nanoTime();
            List<SimpleEntry<T, Integer>> sorted = sorter.sort(copy);
            long end = System.nanoTime();

            System.out.println("Sorted! Time Elapsed: " + getTime(end - start));

            System.out.println("Checking if stable...");
            System.out.println("Sorted List is stable? " + sorter.isStable(sorted));

            if (size <= 10000) {
                System.out.println("Sorted List: " + sorted + "\n");
            } else {
                System.out.println("Sorted List is too long to print!\n");
            }
        }
    }

    protected static  List<SimpleEntry<Integer, Integer>> makeIntegerList(int cap) {
        int count = 0;
        Random rng = new Random();
        //int limit = rng.nextInt(2, 16); // 2 to 15
        int limit = cap;
        List<SimpleEntry<Integer, Integer>> test = new ArrayList<>();

        while (count < limit - 1) {
            if (cap - test.size() >= 2 && rng.nextInt(4) == 0) {
                // Force adjacent duplicates
                int value = rng.nextInt(1000);
                test.add(new SimpleEntry<>(value, count));
                test.add(new SimpleEntry<>(value, count + 1));
                count += 2;
            } else {
                test.add(new SimpleEntry<>(rng.nextInt(1000), count));
                count++;
            }
        }

        return test;
    }

    public static void main(String[] args) {
        run(makeIntegerList(100001));
    }

}
