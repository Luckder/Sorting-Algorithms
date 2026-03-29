package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class QuickSortHoare<T extends Comparable<T>> extends Sort<T> {
    // THE BALLSY ALGORITHM

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }
        quickSort(list, 0, list.size() - 1);
        return list;
    }

    protected void quickSort(List<SimpleEntry<T, Integer>> list, int low, int high) {
        if (low >= high) { return; }
        int p = partition(list, low, high);
        quickSort(list, low, p);
        quickSort(list, p + 1, high);
    }

    protected int partition(List<SimpleEntry<T, Integer>> list, int low, int high) {
        T pivot = list.get(low).getKey();
        int i = low - 1;
        int j = high + 1;
        while (true) {
            do { i++; } while (list.get(i).getKey().compareTo(pivot) < 0);
            do { j--; } while (list.get(j).getKey().compareTo(pivot) > 0);
            if (i >= j) { return j; }
            super.swap(list, i, j);
        }
    }

    @Override
    public String toString() {
        return "QuickSort (Hoare)";
    }
}
