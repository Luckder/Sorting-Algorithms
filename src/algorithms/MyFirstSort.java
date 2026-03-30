package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public class MyFirstSort<T extends Comparable<T>> extends Sort<T> {
    // My first sorting algorithm ever made that worked.
    // It was so long ago, and I was so naive.
    // Very logically straightforward, but it works. I am proud of it.

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.size() < 2) { return list; }

        List<SimpleEntry<T, Integer>> sorted = new ArrayList<>();

        while (sorted.size() != list.size()) {
            int min = Integer.MAX_VALUE;
            int minIndex = 0;

            for (int i = 0; i < list.size(); i++) {

                if (list.get(i) == null) {
                    continue;
                } else {
                    if (min > super.getInt(list.get(i).getKey())) {
                        min = super.getInt(list.get(i).getKey());
                        minIndex = i;
                    }
                }

            }

            sorted.add(list.get(minIndex));
            list.set(minIndex, null);

        }

        return sorted;
    }

    @Override
    public String toString() {
        return "My First Sorting Algorithm";
    }
}
