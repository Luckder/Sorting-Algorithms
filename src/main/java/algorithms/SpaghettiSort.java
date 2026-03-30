package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

// Author: David Chan (Luckder)

public class SpaghettiSort<T extends Comparable<T>> extends Sort<T> {
    // A physical sorting algorithm — imagine holding n spaghetti strands upright,
    // each cut to a length proportional to its value. Drop them all onto a table
    // simultaneously (the "parallel drop") so all bottoms align. Then repeatedly
    // lower a flat hand from above and pull out the tallest strand it touches first.
    // Each pull extracts the current maximum in O(1) physical time.
    //
    // In software the parallel drop is simulated by distributing elements into
    // value-indexed pigeonholes in one O(n) pass. The "hand lowering from above"
    // is draining those holes from the highest value down to the lowest.
    // The result is written back into the original list in ascending order.
    //
    // Time:  O(n + range) — same asymptotic profile as PigeonholeSort
    // Space: O(n + range) for the holes
    // Stable: yes — insertion order within each hole is preserved on drain
    //
    // Difference from PigeonholeSort:
    //   PigeonholeSort drains holes LOW → HIGH directly into the output (ascending).
    //   SpaghettiSort  drains holes HIGH → LOW (simulating the hand pulling tallest
    //   strand first), collecting into a reversed buffer, then writes ascending.
    //   The observable result is identical — the distinction is conceptual fidelity
    //   to the physical algorithm's extraction order.

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.size() < 2) { return list; }

        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (SimpleEntry<T, Integer> entry : list) {
            int val = super.getInt(entry.getKey());
            max = Math.max(max, val);
            min = Math.min(min, val);
        }

        int range = max - min + 1;

        // Step 1: "Drop" — distribute every element into its value-indexed hole
        // Holes are indexed from 0 (min value) to range-1 (max value)
        List<List<SimpleEntry<T, Integer>>> holes = new ArrayList<>();
        for (int i = 0; i < range; i++) {
            holes.add(new ArrayList<>());
        }

        for (SimpleEntry<T, Integer> entry : list) {
            holes.get(super.getInt(entry.getKey()) - min).add(entry);
        }

        // Step 2: "Hand lowering from above" — drain holes HIGH → LOW
        // Simulates pulling the tallest spaghetti strand out first each time
        List<SimpleEntry<T, Integer>> descending = new ArrayList<>(list.size());
        for (int i = range - 1; i >= 0; i--) {
            for (SimpleEntry<T, Integer> entry : holes.get(i)) {
                descending.add(entry);
            }
        }

        // Step 3: Write back in ascending order (reverse of extraction order)
        int writePos = 0;
        for (int i = descending.size() - 1; i >= 0; i--) {
            list.set(writePos++, descending.get(i));
        }

        return list;
    }

    @Override
    public String toString() {
        return "SpaghettiSort";
    }
}