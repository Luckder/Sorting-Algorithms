package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class BubbleSort<T extends Comparable<T>> extends Sort<T> {

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        int n = list.size();

        boolean notSorted = true;

        while(notSorted) {
            notSorted = false; // Assume sorted

            for (int i = 1; i < n; i++) {
                if (list.get(i).getKey().compareTo(list.get(i - 1).getKey()) < 0) {
                    notSorted = true; // List is not sorted

                    super.swap(list, i, i - 1);
                }
            }

            n--; // Last element is in place
        }

        return list;
    }

    @Override
    public String toString() {
        return "BubbleSort";
    }

}
