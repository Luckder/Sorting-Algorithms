import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public final class Main<T extends Comparable<T>> {

    private List<SimpleEntry<T, Integer>> list;
    private final Map<String, Sort<T>> algorithms;

    public Main(List<SimpleEntry<T, Integer>> list) {
        this.list = list;
        this.algorithms = new HashMap<>();

        // HashMaps goes from bottom to top

        this.algorithms.put("bogo", new BogoSort<>());
        this.algorithms.put("merge1", new MergeSortRecursive<>());
        this.algorithms.put("merge12", new MergeSortRecursiveModified<>());
        this.algorithms.put("merge2", new MergeSortIterative<>());
        this.algorithms.put("insertion", new InsertionSort<>());
        this.algorithms.put("cosmic", new CosmicSort<>());
        this.algorithms.put("selection", new SelectionSort<>());
        this.algorithms.put("bubble", new BubbleSort<>());
    }

    public static void main(String[] args) {
        // The below is for T = Integer type, but you can change it to any T type that implements Comparable<T>
        int count = 0;
        Random rng = new Random();
        //int limit = rng.nextInt(2, 16); // 2 to 15
        int limit = 10000;
        List<SimpleEntry<Integer, Integer>> test = new ArrayList<>();

        while (count < limit) {
            if (rng.nextInt(2) == 0) {
                test.add(new SimpleEntry<>(rng.nextInt(100), count));
                count++;
            } else {
                int value = rng.nextInt(100);
                test.add(new SimpleEntry<>(value, count));
                test.add(new SimpleEntry<>(value, count + 1));
                count += 2;
            }
        }

        Main<Integer> main = new Main<>(test);

        System.out.println("\nOriginal List: " + main.list);
        System.out.println("Skipping CosmicSort for obvious reasons...");
        System.out.println("Skipping BogoSort if List length > 10 elements...\n");
        if (main.list.size() > 10) {
            System.out.println("BogoSort was skipped!\n");
        }

        for (Sort<Integer> sorter : main.algorithms.values()) {
            String name = sorter.toString();

            if (name.equals("CosmicSort")) { continue; }
            else if (name.equals("BogoSort") && main.list.size() > 10) { continue; }

            System.out.println("Using " + name);

            List<SimpleEntry<Integer, Integer>> copy = new ArrayList<>(main.list);

            long start = System.nanoTime();
            List<SimpleEntry<Integer, Integer>> sorted = sorter.sort(copy);
            long end = System.nanoTime();

            System.out.println("Sorted! Time Elapsed: " + (end - start) / 1_000_000.0 + " ms");
            System.out.println("Checking if stable...");
            System.out.println("Sorted List is stable? " + sorter.isStable(sorted));
            System.out.println("Sorted List: " + sorted + "\n");
        }
    }

}
