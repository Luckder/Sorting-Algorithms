import algorithms.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

// Author: David Chan (Luckder)

public final class Main {
    private static final List<Sort<? extends Comparable<?>>> algorithms = load();

    static List<Sort<? extends Comparable<?>>> load() {
        return List.of(
                new BogoSort<>(),
                new BubbleSort<>(),
                new BucketSort<>(),
                new CocktailSort<>(),
                new CombSort<>(),
                new CosmicSort<>(),
                new CountingSort<>(),
                new CycleSort<>(),
                new GnomeSort<>(),
                new HeapSort<>(),
                new HeapSortTree<>(),
                new InsertionSort<>(),
                new IntroSort<>(),
                new MergeSort3Way<>(),
                new MergeSortIterative<>(),
                new MergeSortRecursive<>(),
                new MergeSortRecursiveModified<>(),
                new MyFirstSort(),
                new PigeonholeSort<>(),
                new QuickSort3Median<>(),
                new QuickSort3Way<>(),
                new QuickSortHoare<>(),
                new QuickSortHoareParanoid<>(),
                new QuickSortLomuto<>(),
                new QuickSortLomutoParanoid<>(),
                new QuickSortPD<>(),
                new RadixSort<>(),
                new SelectionSort<>(),
                new ShellSort<>(),
                new SpaghettiSort<>(),
                new TimSort<>(),
                new TimSortFake<>(),
                new TreeSort<>()
        );
    }

    static String getTime(long nanos) {
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

        System.out.println("DONE!\n");
    }

    public static  List<SimpleEntry<Integer, Integer>> makeIntegerList(int limit) {
        int count = 0;
        Random rng = new Random();
        List<SimpleEntry<Integer, Integer>> test = new ArrayList<>();

        while (count < limit - 1) {
            if (limit - test.size() >= 2 && rng.nextInt(Math.max(limit / 1000, 100)) == 0) {
                // Force adjacent duplicates
                int value = rng.nextInt(limit * 10);
                test.add(new SimpleEntry<>(value, count));
                test.add(new SimpleEntry<>(value, count + 1));
                count += 2;
            } else {
                test.add(new SimpleEntry<>(rng.nextInt(limit * 10), count));
                count++;
            }
        }

        return test;
    }

    public static void main(String[] args) {
        run(makeIntegerList(100001));
    }

}
