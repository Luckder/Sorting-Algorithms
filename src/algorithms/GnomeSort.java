package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class GnomeSort<T extends Comparable<T>> extends Sort<T> {
    // Walks forward when the current pair is in order.
    // Swaps and steps back when out of order — like insertion sort
    // but moving the displaced element back one step at a time instead of shifting.
    // O(n²) time, O(1) space, stable.

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.size() < 2) { return list; }

        int n = list.size();
        int i = 1;

        while (i < n) {
            if (list.get(i).getKey().compareTo(list.get(i - 1).getKey()) >= 0) {
                // Current pair is in order — move forward
                i++;
            } else {
                // Out of order — swap and step back
                super.swap(list, i, i - 1);
                if (i > 1) { i--; }
            }
        }

        return list;
    }

    @Override
    public String toString() {
        return "GnomeSort";
    }
}