package algorithms;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

// Author: David Chan (Luckder)

public class HeapSort<T extends Comparable<T>> extends Sort<T> {
    //Heapify

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.size() <= 1) { return list; }

        int n = list.size();

        // 1. Build max-heap in-place using Floyd's algorithm (bottom-up)
        //    Start from the last non-leaf node: index (n/2) - 1
        for (int i = (n / 2) - 1; i >= 0; i--) {
            siftDown(list, i, n);
        }

        // 2. Repeatedly extract the max (root) and shrink the heap
        for (int end = n - 1; end > 0; end--) {
            // Swap root (current max) to the sorted region at the back
            super.swap(list, 0, end);
            // Restore heap property over the reduced range [0, end)
            siftDown(list, 0, end);
        }

        return list;
    }

    private void siftDown(List<SimpleEntry<T, Integer>> heap, int i, int size) {
        int largest = i;
        int left    = 2 * i + 1;
        int right   = 2 * i + 2;

        if (left < size && heap.get(left).getKey().compareTo(heap.get(largest).getKey()) > 0) {
            largest = left;
        }

        if (right < size && heap.get(right).getKey().compareTo(heap.get(largest).getKey()) > 0) {
            largest = right;
        }

        if (largest != i) {
            super.swap(heap, i, largest);
            siftDown(heap, largest, size);
        }
    }


    @Override
    public String toString() {
        return "HeapSort";
    }
}