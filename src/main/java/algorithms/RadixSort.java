package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class RadixSort<T extends Comparable<T>> extends CountingSort<T> {
    // Many rounds of CountingSort but with small and known domain of [0, 9], YEAH!!!

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.size() < 2) { return list; }

        int max = Integer.MIN_VALUE;

        for (SimpleEntry<T, Integer> entry : list) {
            if (super.getInt(entry.getKey()) > max) {
                max = super.getInt(entry.getKey());
            }
        }

        List<SimpleEntry<Integer, SimpleEntry<T, Integer>>> temp = null;

        for (long i = 1; i <= max; i *= 10) {
            long dumb = i;

            if (i == 1) {
                temp = list.stream()
                        .map(x -> new SimpleEntry<>(getDigit(super.getInt(x.getKey()), dumb), x))
                        .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
            } else {
                temp = temp.stream()
                        .map(x -> {
                            return new SimpleEntry<>(getDigit(super.getInt(x.getValue().getKey()), dumb), x.getValue());
                        })
                        .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
            }

            countingSort(temp);
        }

        for (int i = 0; i < list.size(); i++) {
            list.set(i, temp.get(i).getValue());
        }

        return list;
    }

    protected int getDigit(int n, long i) {
        return Math.toIntExact((Math.abs(n) / i) % 10);
    }

    protected List<SimpleEntry<Integer, SimpleEntry<T, Integer>>> countingSort(List<SimpleEntry<Integer, SimpleEntry<T, Integer>>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        int n = list.size();
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (SimpleEntry<Integer, ?> entry : list) {
            max = Math.max(max, entry.getKey());
            min = Math.min(min, entry.getKey());
        }

        int[] count = new int[max - min + 1];

        for (SimpleEntry<Integer, ?> entry : list) {
            count[entry.getKey() - min]++;
        }

        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
        }

        SimpleEntry<Integer, SimpleEntry<T, Integer>>[] buffer = new SimpleEntry[n];

        for (int i = n - 1; i >= 0; i--) {
            SimpleEntry<Integer, SimpleEntry<T, Integer>> entry = list.get(i);
            int pos = --count[entry.getKey() - min];
            buffer[pos] = entry;
        }

        for (int i = 0; i < n; i++) {
            list.set(i, buffer[i]);
        }

        return list;
    }

    @Override
    public String toString() {
        return "RadixSort";
    }

}
