package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

// Author: David Chan (Luckder)

public class TimSortFake <T extends Comparable<T>> extends MergeSortIterative<T> {
    // I cannot even begin to implement galloping mode and reduce memory space usage
    // Had AI helped me with this code, that shows how crazy TimSort is,
    // and how much crazier Tim Petersen is for inventing it for Python and Java

    private static final int MIN_MERGE = 32;

    private int minRunLength(int n) {
        int r = 0;
        while (n >= MIN_MERGE) {
            r |= (n & 1);
            n >>= 1;
        }
        return n + r;
    }

    private void binaryInsertionSort(List<SimpleEntry<T, Integer>> list, int lo, int hi, int start) {
        if (start == lo) start++;
        for (; start < hi; start++) {
            SimpleEntry<T, Integer> pivot = list.get(start);
            int left = lo;
            int right = start;

            while (left < right) {
                int mid = (left + right) >>> 1;
                if (pivot.getKey().compareTo(list.get(mid).getKey()) < 0) right = mid;
                else left = mid + 1;
            }

            for (int m = start; m > left; m--) {
                list.set(m, list.get(m - 1));
            }
            list.set(left, pivot);
        }
    }

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null || list.size() < 2) return list;

        int n = list.size();
        int minRun = minRunLength(n);

        // Stack to store run base indices and lengths
        // runStack[i][0] = base, runStack[i][1] = length
        int[][] runStack = new int[40][2];
        int stackSize = 0;

        int i = 0;
        while (i < n) {
            // 1. Find a natural run
            int runLen = countRunAndMakeAscending(list, i);

            // 2. If run is too short, extend it to minRun using Binary Insertion algorithms.Sort
            if (runLen < minRun) {
                int force = Math.min(n - i, minRun);
                binaryInsertionSort(list, i, i + force, i + runLen);
                runLen = force;
            }

            // 3. Push run to stack and merge if invariants are violated
            runStack[stackSize][0] = i;
            runStack[stackSize][1] = runLen;
            stackSize++;
            i += runLen;

            stackSize = mergeCollapse(list, runStack, stackSize);
        }

        // 4. Final merge of everything on the stack
        while (stackSize > 1) {
            stackSize = mergeAt(list, runStack, stackSize, stackSize - 2);
        }

        return list;
    }

    private int countRunAndMakeAscending(List<SimpleEntry<T, Integer>> list, int lo) {
        int hi = lo + 1;
        if (hi == list.size()) return 1;

        if (list.get(hi).getKey().compareTo(list.get(lo).getKey()) < 0) { // Descending
            while (hi < list.size() && list.get(hi).getKey().compareTo(list.get(hi - 1).getKey()) < 0) hi++;
            reverseRange(list, lo, hi - 1);
        } else { // Ascending
            while (hi < list.size() && list.get(hi).getKey().compareTo(list.get(hi - 1).getKey()) >= 0) hi++;
        }
        return hi - lo;
    }

    private void reverseRange(List<SimpleEntry<T, Integer>> list, int lo, int hi) {
        while (lo < hi) {
            SimpleEntry<T, Integer> temp = list.get(lo);
            list.set(lo++, list.get(hi));
            list.set(hi--, temp);
        }
    }

    private int mergeCollapse(List<SimpleEntry<T, Integer>> list, int[][] stack, int size) {
        while (size > 1) {
            int n = size - 2;
            if (n >= 0 && stack[n][1] <= stack[n+1][1] + (n+2 < size ? stack[n+2][1] : 0)) {
                if (stack[n][1] < (n+2 < size ? stack[n+2][1] : Integer.MAX_VALUE)) size = mergeAt(list, stack, size, n);
                else size = mergeAt(list, stack, size, n + 1);
            } else if (stack[size-2][1] <= stack[size-1][1]) {
                size = mergeAt(list, stack, size, size - 2);
            } else {
                break;
            }
        }
        return size;
    }

    private int mergeAt(List<SimpleEntry<T, Integer>> list, int[][] stack, int size, int i) {
        int base1 = stack[i][0];
        int len1 = stack[i][1];
        int base2 = stack[i+1][0];
        int len2 = stack[i+1][1];

        // Use your existing merge logic here!
        List<SimpleEntry<T, Integer>> left = new ArrayList<>(list.subList(base1, base1 + len1));
        List<SimpleEntry<T, Integer>> right = new ArrayList<>(list.subList(base2, base2 + len2));
        List<SimpleEntry<T, Integer>> merged = merge(left, right);

        for (int j = 0; j < merged.size(); j++) {
            list.set(base1 + j, merged.get(j));
        }

        stack[i][1] = len1 + len2; // Update new length
        if (i == size - 3) { // Shift stack down
            stack[i+1][0] = stack[i+2][0];
            stack[i+1][1] = stack[i+2][1];
        }
        return size - 1;
    }

    @Override
    public String toString() {
        return "TimSort (No Galloping)";
    }

}
