package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class IntroSort<T extends Comparable<T>> extends HeapSort<T> {

    // Below this size, insertion sort beats quicksort due to lower overhead
    protected static final int INSERTION_THRESHOLD = 16;

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null || list.size() < 2) return list;
        // Depth limit of 2*log2(n) — if quicksort exceeds this, switch to heapsort.
        // This guarantees O(n log n) worst case even on adversarial inputs.
        int depthLimit = 2 * (int)(Math.log(list.size()) / Math.log(2));
        introSort(list, 0, list.size() - 1, depthLimit);
        return list;
    }

    private void introSort(List<SimpleEntry<T, Integer>> list, int lo, int hi, int depth) {
        while (hi - lo > INSERTION_THRESHOLD) {
            if (depth == 0) {
                // Too many bad partitions — fall back to guaranteed O(n log n) heapsort
                heapSort(list, lo, hi);
                return;
            }
            depth--;

            int p = partition(list, lo, hi);

            // Tail recurse on smaller partition, iterate on larger.
            // This keeps stack depth O(log n) in the worst case.
            if (p - lo < hi - p) {
                introSort(list, lo, p - 1, depth);
                lo = p + 1;
            } else {
                introSort(list, p + 1, hi, depth);
                hi = p - 1;
            }
        }
        // Small subarray — insertion sort has lower constant than quicksort here
        insertionSort(list, lo, hi);
    }

    // -------------------------------------------------------------------------
    // PARTITION — Hoare-style with median-of-three pivot selection
    // Median of (lo, mid, hi) avoids worst-case O(n²) on sorted/reverse input.
    // -------------------------------------------------------------------------

    // Rearranges list[lo], list[mid], list[hi] so lo <= mid <= hi,
    // then moves the median (best pivot) to hi-1 and returns its index.
    private int medianOfThree(List<SimpleEntry<T, Integer>> list, int lo, int hi) {
        int mid = lo + (hi - lo) / 2;
        if (list.get(lo).getKey().compareTo(list.get(mid).getKey()) > 0) super.swap(list, lo, mid);
        if (list.get(lo).getKey().compareTo(list.get(hi).getKey())  > 0) super.swap(list, lo, hi);
        if (list.get(mid).getKey().compareTo(list.get(hi).getKey()) > 0) super.swap(list, mid, hi);
        // lo <= mid <= hi — move median to hi-1 as pivot
        // list[lo] and list[hi] now act as sentinels for the scan loops
        super.swap(list, mid, hi - 1);
        return hi - 1;
    }

    protected int partition(List<SimpleEntry<T, Integer>> list, int lo, int hi) {
        // 2 or fewer elements — handle directly, no median-of-three needed
        if (hi - lo < 2) {
            if (list.get(hi).getKey().compareTo(list.get(lo).getKey()) < 0) super.swap(list, lo, hi);
            return lo;
        }

        int pivotIdx = medianOfThree(list, lo, hi);
        T pivot = list.get(pivotIdx).getKey();

        // Sentinels: list[lo] <= pivot and list[hi] >= pivot (guaranteed by medianOfThree).
        // These ensure the scan loops always terminate without a bounds check.
        int i = lo, j = hi - 1;
        while (true) {
            while (list.get(++i).getKey().compareTo(pivot) < 0) ;
            while (list.get(--j).getKey().compareTo(pivot) > 0) ;
            if (i >= j) break;
            super.swap(list, i, j);
        }

        super.swap(list, i, hi - 1); // Restore pivot to its final sorted position
        return i;
    }

    // -------------------------------------------------------------------------
    // INSERTION SORT — O(n²) but very fast for small arrays due to low overhead
    // -------------------------------------------------------------------------

    protected void insertionSort(List<SimpleEntry<T, Integer>> list, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            SimpleEntry<T, Integer> key = list.get(i);
            int j = i;
            while (j > lo && list.get(j - 1).getKey().compareTo(key.getKey()) > 0) {
                list.set(j, list.get(j - 1));
                j--;
            }
            list.set(j, key);
        }
    }

    @Override
    public String toString() {
        return "IntroSort";
    }
}