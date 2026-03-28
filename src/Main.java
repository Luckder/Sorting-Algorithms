import java.util.*;
import java.util.AbstractMap.SimpleEntry;

// Author: David Chan (Luckder)

public final class Main<T extends Comparable<T>> {

    private List<SimpleEntry<T, Integer>> list;
    private final List<Sort<T>> algorithms;

    public Main(List<SimpleEntry<T, Integer>> list) {
        this.list = list;
        this.algorithms = new ArrayList<>();

        // O(n + k) Algorithms | All Stable
        this.algorithms.add(new CountingSort<>());

        // O(n log n) Algorithms [Guaranteed] | All Stable
        this.algorithms.add(new TimSortFake<>());
        this.algorithms.add(new MergeSortRecursive<>());
        this.algorithms.add(new MergeSortRecursiveModified<>());
        this.algorithms.add(new MergeSort3Way<>());
        this.algorithms.add(new MergeSortIterative<>());
        this.algorithms.add(new TreeSort<>());

        // O(n log n) Algorithms [Not Guaranteed; Worst Case O(n^2)] | All Unstable
        this.algorithms.add(new QuickSortLomuto<>());
        this.algorithms.add(new QuickSortHoare<>());
        this.algorithms.add(new QuickSortLomutoParanoid<>());
        this.algorithms.add(new QuickSortHoareParanoid<>());

        // O(n^2) Algorithms
        this.algorithms.add(new InsertionSort<>()); // Stable
        this.algorithms.add(new BubbleSort<>()); // Stable
        this.algorithms.add(new SelectionSort<>()); // Unstable

        // Joke Algorithms
        this.algorithms.add(new BogoSort<>());
        this.algorithms.add(new CosmicSort<>());
    }

    private static String getTime(long nanos) {
        // I ain't gonna do days
        if (nanos > 3_600_000_000_000L) { return nanos / 3_600_000_000_000.0 + " h"; }
        else if (nanos > 60_000_000_000L)    { return nanos / 60_000_000_000.0    + " min"; }
        else if (nanos > 1_000_000_000L)     { return nanos / 1_000_000_000.0     + " s"; }
        else { return nanos / 1_000_000.0 + " ms"; }
    }

    private static void integer(int cap) {
        // The below is for T = Integer type, but you can change it to any T type that extends Comparable<T>

        int count = 0;
        Random rng = new Random();
        //int limit = rng.nextInt(2, 16); // 2 to 15
        int limit = cap;
        List<SimpleEntry<Integer, Integer>> test = new ArrayList<>();

        while (count < limit - 1) {
            if (rng.nextInt(2) == 0) {
                test.add(new SimpleEntry<>(rng.nextInt(1000), count));
                count++;
            } else {
                int value = rng.nextInt(1000);
                test.add(new SimpleEntry<>(value, count));
                test.add(new SimpleEntry<>(value, count + 1));
                count += 2;
            }
        }

        Main<Integer> main = new Main<>(test);

        int size = main.list.size();

        if (limit <= 10000) {
            System.out.println("\nOriginal List: " + main.list + "\n");
        } else {
            System.out.println("\nOriginal List is too long to print! Size is " + size + " elements.\n");
        }

        System.out.println("Skipping CosmicSort for obvious reasons...");
        System.out.println("Skipping BogoSort if List length > 10 elements...");
        System.out.println("WARNING: CountingSort can get very angsty!\n");

        for (Sort<Integer> sorter : main.algorithms) {
            String name = sorter.toString();

            if (name.equals("CosmicSort")) { continue; }
            if (name.equals("BogoSort") && size > 10) {
                System.out.println("BogoSort was skipped!\n");
                continue;
            }

            System.out.println("Using " + name + "...");

            List<SimpleEntry<Integer, Integer>> copy = new ArrayList<>(main.list);

            long start = System.nanoTime();
            List<SimpleEntry<Integer, Integer>> sorted = sorter.sort(copy);
            long end = System.nanoTime();
            long time = end - start;

            System.out.println("Sorted! Time Elapsed: " + getTime(time));

            System.out.println("Checking if stable...");
            System.out.println("Sorted List is stable? " + sorter.isStable(sorted));

            if (limit <= 10000) {
                System.out.println("Sorted List: " + sorted + "\n");
            } else {
                System.out.println("Sorted List is too long to print!\n");
            }
        }
    }

    public static void main(String[] args) {
        integer(100001);
    }

}
