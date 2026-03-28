import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class MergeSortRecursiveModified<T extends Comparable<T>> extends MergeSortRecursive<T> {
    // A modified version of MergeSort that uses InsertionSort for small lists (size < 32)

    private final InsertionSort<T> insertionSort = new InsertionSort<>();

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return list; }
        if (list.size() < 32 ) { return insertionSort.sort(list); } // Use InsertionSort for small lists
        int n = list.size();

        int mid = n / 2;
        List<SimpleEntry<T, Integer>> left = sort(list.subList(0, mid));
        List<SimpleEntry<T, Integer>> right = sort(list.subList(mid, n));

        return merge(left, right);
    }

    @Override
    public String toString() {
        return "MergeSort (Recursive-Modified)";
    }

}
