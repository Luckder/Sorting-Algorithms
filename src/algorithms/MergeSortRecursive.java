package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

// Author: David Chan (Luckder)

public class MergeSortRecursive<T extends Comparable<T>> extends Sort<T> {
    // THE GOAT!!!

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null || list.size() < 2) { return list; }
        int n = list.size();
        int mid = n / 2;

        List<SimpleEntry<T, Integer>> left = sort(list.subList(0, mid));
        List<SimpleEntry<T, Integer>> right = sort(list.subList(mid, n));

        return merge(left, right);
    }

    protected List<SimpleEntry<T, Integer>> merge(List<SimpleEntry<T, Integer>> left, List<SimpleEntry<T, Integer>> right) {
        List<SimpleEntry<T, Integer>> merged = new ArrayList<>();

        int i = 0; // Left Index
        int j = 0; // Right Index

        while (i < left.size() && j < right.size()) {
            if (left.get(i).getKey().compareTo(right.get(j).getKey()) <= 0) {
                merged.add(left.get(i));
                i++;
            } else {
                merged.add(right.get(j));
                j++;
            }
        }

        while (i < left.size()) {
            merged.add(left.get(i));
            i++;
        }

        while (j < right.size()) {
            merged.add(right.get(j));
            j++;
        }

        return merged;
    }

    @Override
    public String toString() {
        return "MergeSort (Recursive)";
    }

}
