package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class SelectionSort<T extends Comparable<T>> extends Sort<T> {

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        int n = list.size();

        int smallest = 0;
        SimpleEntry<T, Integer> temp;
        int count = 0;

        // Does not need to use a while loop because selection sort is guaranteed to be sorted after n - 1 iterations
        for (int j = 0; j < n - 1; j++) {
            for (int i = count; i < n; i++) {
                if (list.get(i).getKey().compareTo(list.get(smallest).getKey()) < 0) {
                    smallest = i;
                }
            }

            super.swap(list, count, smallest);
            count++;
            smallest = count;
        }

        return list;
    }

    @Override
    public String toString() {
        return "SelectionSort";
    }

}
