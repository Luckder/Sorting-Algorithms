package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class InsertionSort<T extends Comparable<T>> extends Sort<T> {

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        int n = list.size();

        for (int i = 1; i < n; i++) {
            SimpleEntry<T, Integer> curr = list.get(i);
            int j = i - 1;

            // j always points to an empty space in the left subarray
            while (j >= 0 && list.get(j).getKey().compareTo(curr.getKey()) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }

            // j points to the left of the empty space
            list.set(j + 1, curr);
        }

        return list;
    }

    @Override
    public String toString() {
        return "InsertionSort";
    }

}
