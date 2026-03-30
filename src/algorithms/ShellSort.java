package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class ShellSort<T extends Comparable<T>> extends InsertionSort<T> {
    // A generalisation of InsertionSort that sorts elements far apart first,
    // progressively shrinking the gap until gap = 1 (plain InsertionSort).
    // The shrinking gap means elements travel long distances early, so the final
    // InsertionSort pass has very little work left — nearly sorted input.
    //
    // Gap sequence used: Ciura (2001) — empirically the best known sequence.
    // Worst-case complexity depends on gap sequence; Ciura gives roughly O(n^1.3).
    // O(1) space, not stable (gap > 1 swaps can cross equal elements).

    // Ciura's empirically optimal gap sequence — proven best known in benchmarks
    // Better than ShellSort's original n/2 halvings and Knuth's (3^k - 1) / 2
    private static final int[] GAPS = { 701, 301, 132, 57, 23, 10, 4, 1 };

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.size() < 2) { return list; }

        int n = list.size();

        for (int gap : GAPS) {
            if (gap >= n) { continue; }
            insertionSortGapped(list, 0, n, gap); // reuses InsertionSort's logic
        }

        return list;
    }

    protected void insertionSortGapped(List<SimpleEntry<T, Integer>> list, int lo, int hi, int gap) {
        for (int i = lo + gap; i < hi; i++) {
            SimpleEntry<T, Integer> curr = list.get(i);

            int j = i - gap;

            while (j >= lo && list.get(j).getKey().compareTo(curr.getKey()) > 0) {
                list.set(j + gap, list.get(j));
                j -= gap;
            }

            list.set(j + gap, curr);
        }
    }

    @Override
    public String toString() {
        return "ShellSort";
    }
}