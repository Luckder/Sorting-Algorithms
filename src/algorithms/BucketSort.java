package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

// Author: David Chan (Luckder)

public class BucketSort<T extends Comparable<T>> extends Sort<T> {
    // Distributes elements into value-range buckets, recursively sorts each bucket
    // using BucketSort itself (no other algorithm), then concatenates back.
    // Extends CountingSort only to reuse getInt() — no counting logic is inherited.

    private static final int BUCKET_COUNT = 10;

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        bucketSort(list);

        return list;
    }

    private void bucketSort(List<SimpleEntry<T, Integer>> list) {
        if (list.size() <= 1) { return; }

        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (SimpleEntry<T, Integer> entry : list) {
            int val = super.getInt(entry.getKey());
            max = Math.max(max, val);
            min = Math.min(min, val);
        }

        // All elements are equal — already sorted, nothing to do
        if (max == min) { return; }

        // Use fewer buckets than BUCKET_COUNT if the list is very small
        int bucketCount = Math.min(BUCKET_COUNT, list.size());

        List<List<SimpleEntry<T, Integer>>> buckets = new ArrayList<>();
        for (int i = 0; i < bucketCount; i++) {
            buckets.add(new ArrayList<>());
        }

        // Distribute: map each element's value linearly into [0, bucketCount - 1]
        for (SimpleEntry<T, Integer> entry : list) {
            int val = super.getInt(entry.getKey());
            int idx = (int)((long)(val - min) * (bucketCount - 1) / (max - min));
            buckets.get(idx).add(entry);
        }

        // Recursively sort each non-trivial bucket using BucketSort, then write back
        int writePos = 0;
        for (List<SimpleEntry<T, Integer>> bucket : buckets) {
            if (bucket.size() > 1) {
                bucketSort(bucket); // Recurse — no other sort used
            }
            for (SimpleEntry<T, Integer> entry : bucket) {
                list.set(writePos++, entry);
            }
        }
    }

    @Override
    public String toString() {
        return "BucketSort";
    }
}