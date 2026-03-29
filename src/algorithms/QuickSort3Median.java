package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class QuickSort3Median<T extends Comparable<T>> extends QuickSortHoare<T> {
    // Hoare partition is identical — only pivot SELECTION changes.
    // Median-of-3 picks the median of lo, mid, hi and moves it to lo
    // so Hoare's existing logic uses it as pivot. No other changes needed.

    @Override
    protected int partition(List<SimpleEntry<T, Integer>> list, int low, int high) {
        int mid = low + (high - low) / 2;

        // Sort lo, mid, hi so that list[mid] is the median
        if (list.get(mid).getKey().compareTo(list.get(low).getKey())  < 0) super.swap(list, low, mid);
        if (list.get(high).getKey().compareTo(list.get(low).getKey()) < 0) super.swap(list, low, high);
        if (list.get(high).getKey().compareTo(list.get(mid).getKey()) < 0) super.swap(list, mid, high);

        // Hoare uses list.get(low) as pivot — move the median there
        super.swap(list, low, mid);

        return super.partition(list, low, high);
    }

    @Override
    public String toString() {
        return "QuickSort (Median-of-3)";
    }
}