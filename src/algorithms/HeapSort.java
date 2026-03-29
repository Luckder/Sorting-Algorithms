package algorithms;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class HeapSort<T extends Comparable<T>> extends Sort<T> {
    //Heapify

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.size() <= 1) { return list; }

        heapSort(list, 0, list.size() - 1);

        return list;
    }

    // -------------------------------------------------------------------------
    // HEAP SORT — O(n log n) guaranteed, O(1) extra space.
    // Used as the fallback when quicksort's depth limit is exceeded.
    // Not stable, but correctness over performance is the goal here.
    // -------------------------------------------------------------------------

    protected void heapSort(List<SimpleEntry<T, Integer>> list, int lo, int hi) {
        int n = hi - lo + 1;
        // Build max-heap (heapify phase)
        for (int i = n / 2 - 1; i >= 0; i--) siftDown(list, i, n, lo);
        // Extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            super.swap(list, lo, lo + i); // Move current max to end
            siftDown(list, 0, i, lo);
        }
    }

    protected void siftDown(List<SimpleEntry<T, Integer>> list, int root, int end, int lo) {
        while (true) {
            int largest = root;
            int left    = 2 * root + 1;
            int right   = 2 * root + 2;
            if (left  < end && list.get(lo + left).getKey().compareTo(list.get(lo + largest).getKey()) > 0) largest = left;
            if (right < end && list.get(lo + right).getKey().compareTo(list.get(lo + largest).getKey()) > 0) largest = right;
            if (largest == root) break;
            super.swap(list, lo + root, lo + largest);
            root = largest;
        }
    }

    @Override
    public String toString() {
        return "HeapSort";
    }
}