package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

// Author: David Chan (Luckder)

public class MergeSortIterative<T extends Comparable<T>> extends Sort<T> {

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null || list.size() < 2) { return list; }

        List<SimpleEntry<T, Integer>> sortMe = new ArrayList<>(list);
        int n = sortMe.size();

        // Outer Loop: The size of the sub-lists to merge (1, 2, 4, 8...)
        for (int size = 1; size < n; size *= 2) {

            // Inner Loop: Pick the 'left' and 'right' chunks for the current size
            for (int leftStart = 0; leftStart < n - size; leftStart += 2 * size) {

                // Calculate boundaries
                int mid = leftStart + size;
                int rightEnd = Math.min(leftStart + 2 * size, n);

                // Extract the sublists
                // Note: subList(from, to) is exclusive of the 'to' index
                List<SimpleEntry<T, Integer>> leftList = new ArrayList<>(sortMe.subList(leftStart, mid));
                List<SimpleEntry<T, Integer>> rightList = new ArrayList<>(sortMe.subList(mid, rightEnd));

                // Use existing merge logic
                List<SimpleEntry<T, Integer>> mergedChunk = merge(leftList, rightList);

                // Update the original list with the merged results
                for (int i = 0; i < mergedChunk.size(); i++) {
                    sortMe.set(leftStart + i, mergedChunk.get(i));
                }
            }
        }

        return sortMe;
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
        return "MergeSort (Iterative)";
    }

}
