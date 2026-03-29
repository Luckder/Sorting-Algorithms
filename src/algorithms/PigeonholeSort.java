package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

// Author: David Chan (Luckder)

public class PigeonholeSort<T extends Comparable<T>> extends CountingSort<T> {
    // Conceptually identical to CountingSort but stores actual elements in holes
    // rather than using a count array + prefix sum + buffer placement.
    // Each "pigeonhole" corresponds to one distinct value in the input range.
    // Naturally stable — elements enter each hole in original order and leave the same way.
    //
    // Key difference from CountingSort:
    //   CountingSort:    count[] → prefix sum → place into buffer via index arithmetic
    //   PigeonholeSort:  holes[] → each is a List → append in order → drain in order
    //
    // Best when range (max - min) is close to n. Degrades badly if range >> n
    // since you allocate one empty List per value in the range regardless of occupancy.

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (SimpleEntry<T, Integer> entry : list) {
            int val = super.getInt(entry.getKey());
            max = Math.max(max, val);
            min = Math.min(min, val);
        }

        int range = max - min + 1;

        // One pigeonhole per distinct possible value in [min, max]
        List<List<SimpleEntry<T, Integer>>> holes = new ArrayList<>();
        for (int i = 0; i < range; i++) {
            holes.add(new ArrayList<>());
        }

        // Drop each element into the hole that matches its value
        for (SimpleEntry<T, Integer> entry : list) {
            holes.get(super.getInt(entry.getKey()) - min).add(entry);
        }

        // Drain holes left to right — values are ascending, insertion order within each
        // hole is preserved, so the result is stable
        int writePos = 0;
        for (List<SimpleEntry<T, Integer>> hole : holes) {
            for (SimpleEntry<T, Integer> entry : hole) {
                list.set(writePos++, entry);
            }
        }

        return list;
    }

    @Override
    public String toString() {
        return "PigeonholeSort";
    }
}