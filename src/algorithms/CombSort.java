package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class CombSort<T extends Comparable<T>> extends BubbleSort<T> {
    // CombSort is to BubbleSort what ShellSort is to InsertionSort.
    // BubbleSort only ever compares adjacent elements (gap = 1), which means
    // small values near the end of the list ("turtles") move left very slowly.
    // CombSort eliminates turtles early by starting with a large gap and
    // shrinking it by a factor of 1.3 each pass until gap = 1,
    // at which point it becomes plain BubbleSort for the final cleanup.
    //
    // Shrink factor 1.3 is empirically optimal — derived by Lacey & Box (1991).
    // Time: O(n²) worst case, O(n log n) average. Space: O(1). Not stable.

    private static final double SHRINK = 1.3;

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.size() < 2) { return list; }

        int n   = list.size();
        int gap = n;
        boolean sorted = false;

        while (!sorted) {
            // Shrink the gap — floor it so we never get stuck above 1
            gap = (int)(gap / SHRINK);
            if (gap <= 1) {
                gap    = 1;
                sorted = true; // Assume sorted — if any swap occurs below, flip back
            }

            // One pass comparing elements 'gap' apart — same structure as BubbleSort
            // but with stride 'gap' instead of stride 1
            for (int i = 0; i + gap < n; i++) {
                if (list.get(i + gap).getKey().compareTo(list.get(i).getKey()) < 0) {
                    super.swap(list, i, i + gap);
                    sorted = false; // A swap occurred — not sorted yet
                }
            }
        }

        return list;
    }

    @Override
    public String toString() {
        return "CombSort";
    }
}