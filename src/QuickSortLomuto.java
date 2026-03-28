import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class QuickSortLomuto<T extends Comparable<T>> extends Sort<T> {
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
        quickSort(list, low, p - 1);
        quickSort(list, p + 1, high);
    }

    protected int partition(List<SimpleEntry<T, Integer>> list, int low, int high) {
        T pivot = list.get(high).getKey();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (list.get(j).getKey().compareTo(pivot) < 0) {
                i++;
                super.swap(list, i, j);
            }
        }
        super.swap(list, i + 1, high);
        return i + 1;
    }

    @Override
    public String toString() {
        return "QuickSort (Lomuto)";
    }

}
