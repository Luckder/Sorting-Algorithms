package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

// Author: David Chan (Luckder)

public class CountingSort<T extends Comparable<T>> extends Sort<T> {
    // No Comparisons!!!

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        int n = list.size();
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (SimpleEntry<T, Integer> entry : list) {
            max = Math.max(max, super.getInt(entry.getKey()));
            min = Math.min(min, super.getInt(entry.getKey()));
        }

        int[] count = new int[max - min + 1];

        for (SimpleEntry<T, Integer> entry : list) {
            count[super.getInt(entry.getKey()) - min]++;
        }

        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
        }

        SimpleEntry<T, Integer>[] buffer = new SimpleEntry[n];

        for (int i = n - 1; i >= 0; i--) {
            SimpleEntry<T, Integer> entry = list.get(i);
            int pos = --count[super.getInt(entry.getKey()) - min];
            buffer[pos] = entry;
        }

        for (int i = 0; i < n; i++) {
            list.set(i, buffer[i]);
        }

        return list;
    }

    @Override
    public String toString() {
        return "CountingSort";
    }

}
