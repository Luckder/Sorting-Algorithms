package algorithms;
import  java.util.AbstractMap.SimpleEntry;
import  java.util.List;

// Author: David Chan (Luckder)

public class HeapSort<T extends  Comparable<T>> extends Sort<T> {
    // Heapify!

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.size() <= 1) { return list; }



        return List.of();
    }

    @Override
    public String toString() {
        return "HeapSort";
    }
}
