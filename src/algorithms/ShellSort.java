package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class ShellSort<T extends Comparable<T>> extends Sort<T> {
    // A generalisation of InsertionSort that sorts elements far apart first,
    // progressively shrinking the gap until gap = 1 (plain InsertionSort).
    // The shrinking gap means elements travel long distances early, so the final
    // InsertionSort pass has very little work left — nearly sorted input.
    //
    // Gap sequence used: Ciura (2001) — empirically the best known sequence.
    // Worst-case complexity depends on gap sequence; Ciura gives roughly O(n^1.3).
    // O(1) space, not stable (gap > 1 swaps can cross equal elements).

    // Ciura's empirically optimal gap sequence — proven best known in benchmarks
    private static final int[] GAPS = { 701, 301, 132, 57, 23, 10, 4, 1 };

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.size() < 2) { return list; }

        int n = list.size();

        for (int gap : GAPS) {
            if (gap >= n) { continue; } // Gap larger than list — skip, not applicable

            // Insertion sort with this gap width
            // Every element at distance 'gap' from each other forms a sub-sequence
            // that is independently insertion-sorted
            for (int i = gap; i < n; i++) {
                SimpleEntry<T, Integer> temp = list.get(i);
                int j = i;

                // Shift elements of the gap-sub-sequence that are greater than temp
                // one position ahead, making room for temp's correct position
                while (j >= gap && list.get(j - gap).getKey().compareTo(temp.getKey()) > 0) {
                    list.set(j, list.get(j - gap));
                    j -= gap;
                }

                list.set(j, temp);
            }
        }

        return list;
    }

    @Override
    public String toString() {
        return "ShellSort";
    }
}