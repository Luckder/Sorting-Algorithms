package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)
// Based on Orson Peters' Pattern-Defeating QuickSort (pdqsort), 2015.
// https://github.com/orlp/pdqsort

public class QuickSortPD<T extends Comparable<T>> extends IntroSort<T> {

    // Slightly larger than IntroSort's 16 — pdqsort's insertion sort is more effective
    // because it runs on nearly-sorted data (good pivots leave near-sorted subarrays)
    private static final int INSERTION_THRESHOLD = 24;

    // Above this size, use pseudomedian-of-9 (ninther) instead of median-of-3.
    // More expensive to compute but gives a dramatically better pivot on large arrays.
    private static final int NINTHER_THRESHOLD = 128;

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null || list.size() < 2) return list;

        // --- OPTIMISTIC LINEAR SCAN FOR O(N) BEST CASE ---
        // Check if the array is already strictly sorted or reverse-sorted.
        int n = list.size();
        boolean isAscending = true;
        boolean isDescending = true;

        for (int i = 1; i < n; i++) {
            int cmp = list.get(i).getKey().compareTo(list.get(i - 1).getKey());
            if (cmp < 0) isAscending = false;
            if (cmp > 0) isDescending = false;

            // If it's neither, break early to save time
            if (!isAscending && !isDescending) break;
        }

        if (isAscending) {
            return list; // Already sorted! Best-case O(n) achieved.
        }

        if (isDescending) {
            // Reverse sorted! Flip it in place in O(n) and return.
            for (int i = 0; i < n / 2; i++) {
                super.swap(list, i, n - 1 - i);
            }
            return list;
        }
        // -------------------------------------------------

        int limit = 2 * (int)(Math.log(n) / Math.log(2));
        pdqSort(list, 0, n - 1, limit);
        return list;
    }

    private void pdqSort(List<SimpleEntry<T, Integer>> list, int lo, int hi, int limit) {
        pdqSort(list, lo, hi, limit, false);
    }

    private void pdqSort(List<SimpleEntry<T, Integer>> list, int lo, int hi, int limit, boolean scrambleNext) {
        while (hi - lo >= INSERTION_THRESHOLD) {

            // Depth exceeded — fall back to heapsort, same as IntroSort
            if (limit == 0) {
                heapSort(list, lo, hi);
                return;
            }

            int size = hi - lo + 1;

            // Bad partition detection: if either side is less than 1/8 of the array,
            // the pivot was poor. Decrement limit (heapsort fallback) and scramble
            // a few elements to break the adversarial pattern that caused it.
            if (scrambleNext && size > 4) {
                // Swap elements that are likely causing the pattern
                super.swap(list, lo + 1, lo + size / 4);
                super.swap(list, hi - 1, hi - size / 4);
                super.swap(list, lo + size / 2 - 1, lo + size / 2);
            }

            // Choose pivot via ninther or median-of-3, bring it to lo
            int pivotPos = choosePivot(list, lo, hi);
            super.swap(list, pivotPos, lo);

            // Dutch National Flag partition: splits into <, ==, > regions in one pass.
            // This collapses all equal elements into a single skip region —
            // arrays with many duplicates go from O(n²) to O(n).
            int[] bounds = partitionDNF(list, lo, hi);
            int lt = bounds[0]; // first index of == region
            int gt = bounds[1]; // last  index of == region

            boolean badPartition = lt - lo < size / 8 || hi - gt < size / 8;
            if (badPartition) limit--;

            // Skip the == region entirely — those elements are permanently in place.
            // Recurse on the smaller half, iterate on the larger (stack depth = O(log n)).
            if (lt - lo < hi - gt) {
                pdqSort(list, lo, lt - 1, limit, badPartition);
                lo = gt + 1;
            } else {
                pdqSort(list, gt + 1, hi, limit, badPartition);
                hi = lt - 1;
            }
            scrambleNext = badPartition; // iterative half also gets the flag
        }
        if (hi > lo) insertionSort(list, lo, hi);
    }

    // -------------------------------------------------------------------------
    // PIVOT SELECTION
    // Median-of-3 for small arrays, pseudomedian-of-9 (ninther) for large ones.
    // Ninther: compute median of 3 samples in each third of the array,
    //          then take the median of those 3 medians.
    // Result: pivot is almost always near the true median → balanced partitions.
    // -------------------------------------------------------------------------

    private int choosePivot(List<SimpleEntry<T, Integer>> list, int lo, int hi) {
        int mid = lo + (hi - lo) / 2;
        if (hi - lo >= NINTHER_THRESHOLD) {
            int step = (hi - lo) / 8;
            // Three local medians — one per third of the array
            sortThree(list, lo,          lo + step,    lo + 2 * step);
            sortThree(list, mid - step,  mid,          mid + step);
            sortThree(list, hi - 2*step, hi - step,    hi);
            // Median of the three medians — the ninther
            sortThree(list, lo + step,   mid,          hi - step);
            // True median is now at mid
            return mid;
        }
        // Small array — plain median-of-three suffices
        sortThree(list, lo, mid, hi);
        return mid;
    }

    // Sort three elements into ascending order using a 3-element sorting network.
    // A sorting network is a fixed sequence of comparisons with no branches —
    // extremely CPU-cache-friendly and branch-predictor-friendly.
    private void sortThree(List<SimpleEntry<T, Integer>> list, int a, int b, int c) {
        if (list.get(b).getKey().compareTo(list.get(a).getKey()) < 0) super.swap(list, a, b);
        if (list.get(c).getKey().compareTo(list.get(b).getKey()) < 0) super.swap(list, b, c);
        if (list.get(b).getKey().compareTo(list.get(a).getKey()) < 0) super.swap(list, a, b);
    }

    // -------------------------------------------------------------------------
    // DUTCH NATIONAL FLAG PARTITION (Dijkstra, 1976)
    // Partitions list[lo..hi] around list[lo] as pivot into three regions:
    //   list[lo .. lt-1]  — strictly less than pivot
    //   list[lt .. gt]    — equal to pivot
    //   list[gt+1 .. hi]  — strictly greater than pivot
    // Returns {lt, gt}.
    // Unlike a standard two-way partition this handles duplicates in O(n)
    // rather than the O(n²) degenerate case of naive quicksort.
    // -------------------------------------------------------------------------

    private int[] partitionDNF(List<SimpleEntry<T, Integer>> list, int lo, int hi) {
        T pivot = list.get(lo).getKey();
        int lt = lo;      // boundary: everything before lt is < pivot
        int gt = hi;      // boundary: everything after  gt is > pivot
        int i  = lo + 1;  // current element under inspection

        while (i <= gt) {
            int cmp = list.get(i).getKey().compareTo(pivot);
            if      (cmp < 0) super.swap(list, lt++, i++); // < pivot: move to left region
            else if (cmp > 0) super.swap(list, i, gt--);   // > pivot: move to right region
            else              i++;                          // == pivot: already in middle region
        }
        return new int[]{lt, gt};
    }

    @Override
    public String toString() {
        return "Pattern-Defeating QuickSort";
    }
}