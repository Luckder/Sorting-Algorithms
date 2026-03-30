package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

// Author: David Chan (Luckder)

public class TreeSort<T extends Comparable<T>> extends Sort<T> {
    // Red-Black Tree FTW!!!

    // Kinda cheating to use Java's built-in TreeMap, should code it myself
    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        TreeMap<T, List<SimpleEntry<T, Integer>>> count = new TreeMap<>();

        for (SimpleEntry<T, Integer> element : list) {
            count.computeIfAbsent(element.getKey(), k -> new ArrayList<>()).add(element);
        }

        List<SimpleEntry<T, Integer>> sorted = new ArrayList<>();

        for (List<SimpleEntry<T, Integer>> group : count.values()) {
            sorted.addAll(group); // original insertion order preserved within group → stable
        }

        // REVERT back to this return statement for testing,
        // Current return statement is for animation
        //return sorted;

        for (int i = 0; i < sorted.size(); i++) { list.set(i, sorted.get(i)); }
        return list;
    }

    @Override
    public String toString() {
        return "TreeSort";
    }

}
