package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

// Author: David Chan (Luckder)

public class MergeSort3Way<T extends Comparable<T>> extends MergeSortRecursive<T> {
    // A modified version of MergeSort that splits the list into 3 parts instead of 2

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null || list.size() < 2) { return list; }
        if (list.size() < 3) { return super.sort(list); /* Calls Regular 2-Way MergeSort */ }

        int n = list.size();
        int third = n / 3;

        List<SimpleEntry<T, Integer>> left = sort(list.subList(0, third));
        List<SimpleEntry<T, Integer>> middle = sort(list.subList(third, 2 * third));
        List<SimpleEntry<T, Integer>> right = sort(list.subList(2 * third, n));

        return merge(left, middle, right);
    }

    protected List<SimpleEntry<T, Integer>> merge(List<SimpleEntry<T, Integer>> left, List<SimpleEntry<T, Integer>> middle, List<SimpleEntry<T, Integer>> right) {
        List<SimpleEntry<T, Integer>> merged = new ArrayList<>();

        int i = 0; // Left Index
        int j = 0; // Middle Index
        int k = 0; // Right Index

        // Phase 1: All three lists have elements
        while (i < left.size() && j < middle.size() && k < right.size()) {
            T leftKey = left.get(i).getKey();
            T midKey = middle.get(j).getKey();
            T rightKey = right.get(k).getKey();

            if (leftKey.compareTo(midKey) <= 0 && leftKey.compareTo(rightKey) <= 0) {
                merged.add(left.get(i));
                i++;
            } else if (midKey.compareTo(leftKey) <= 0 && midKey.compareTo(rightKey) <= 0) {
                merged.add(middle.get(j));
                j++;
            } else {
                merged.add(right.get(k));
                k++;
            }
        }

        // Phase 2: Only two lists have elements remaining
        // Case A: Left and Middle
        while (i < left.size() && j < middle.size()) {
            if (left.get(i).getKey().compareTo(middle.get(j).getKey()) <= 0) {
                merged.add(left.get(i));
                i++;
            } else {
                merged.add(middle.get(j));
                j++;
            }
        }

        // Case B: Middle and Right
        while (j < middle.size() && k < right.size()) {
            if (middle.get(j).getKey().compareTo(right.get(k).getKey()) <= 0) {
                merged.add(middle.get(j));
                j++;
            } else {
                merged.add(right.get(k));
                k++;
            }
        }

        // Case C: Left and Right
        while (i < left.size() && k < right.size()) {
            if (left.get(i).getKey().compareTo(right.get(k).getKey()) <= 0) {
                merged.add(left.get(i));
                i++;
            } else {
                merged.add(right.get(k));
                k++;
            }
        }

        // Phase 3: Only one list has elements remaining
        while (i < left.size()) {
            merged.add(left.get(i));
            i++;
        }

        while (j < middle.size()) {
            merged.add(middle.get(j));
            j++;
        }
        while (k < right.size()) {
            merged.add(right.get(k));
            k++;
        }

        return merged;
    }

    @Override
    public String toString() {
        return "MergeSort (3-Way)";
    }
}
