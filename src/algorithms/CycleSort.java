package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class CycleSort<T extends Comparable<T>> extends Sort<T> {
    // Theoretically optimal in terms of total WRITES to memory — O(n) writes guaranteed.
    // Useful for flash storage or EEPROMs where write cycles are expensive.
    // Trades this write-minimisation for O(n²) comparisons — not a speed champion.
    // Unstable: equal elements may be reordered during cycle rotation.

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        int n = list.size();

        for (int cycleStart = 0; cycleStart < n - 1; cycleStart++) {
            SimpleEntry<T, Integer> item = list.get(cycleStart);

            // Count how many elements are smaller than item — that is item's correct position
            int pos = cycleStart;
            for (int i = cycleStart + 1; i < n; i++) {
                if (list.get(i).getKey().compareTo(item.getKey()) < 0) { pos++; }
            }

            // item is already in its correct position — no cycle to rotate
            if (pos == cycleStart) { continue; }

            // Skip over duplicates of item so we land on the rightmost empty slot
            while (item.getKey().compareTo(list.get(pos).getKey()) == 0) { pos++; }

            // Place item into its correct slot, pick up what was there and carry it forward
            SimpleEntry<T, Integer> displaced = list.get(pos);
            list.set(pos, item);
            item = displaced;

            // Keep rotating the cycle until we arrive back at cycleStart
            while (pos != cycleStart) {
                // Recount: where does the currently held item belong?
                pos = cycleStart;
                for (int i = cycleStart + 1; i < n; i++) {
                    if (list.get(i).getKey().compareTo(item.getKey()) < 0) { pos++; }
                }

                // Skip duplicates again before placing
                while (item.getKey().compareTo(list.get(pos).getKey()) == 0) { pos++; }

                // Place item, pick up the next displaced element
                displaced = list.get(pos);
                list.set(pos, item);
                item = displaced;
            }
        }

        return list;
    }

    @Override
    public String toString() {
        return "CycleSort";
    }
}