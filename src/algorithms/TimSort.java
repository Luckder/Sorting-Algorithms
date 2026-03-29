package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

// Author: David Chan (Luckder)

public class TimSort<T extends Comparable<T>> extends TimSortFake<T> {

    // When one run wins this many comparisons in a row, switch to gallop mode
    private static final int MIN_GALLOP = 7;
    // Adaptive — penalised when gallop mode proves unhelpful, rewarded when useful
    private int minGallop = MIN_GALLOP;

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        minGallop = MIN_GALLOP; // Reset adaptive threshold between calls
        return super.sort(list);
    }

    // -------------------------------------------------------------------------
    // GALLOP METHODS
    // Both use exponential search from hint position, then binary search to land.
    // This is O(log k) where k is how far the answer is — fast for long runs.
    // -------------------------------------------------------------------------

    // Find leftmost position in list[base..base+len) where key should be inserted.
    // "Leftmost" means: first slot where list[slot] >= key.
    private int gallopLeft(T key, List<SimpleEntry<T, Integer>> list, int base, int len, int hint) {
        int lastOfs = 0, ofs = 1;

        if (key.compareTo(list.get(base + hint).getKey()) > 0) {
            // Key is right of hint — exponentially search rightward
            int maxOfs = len - hint;
            while (ofs < maxOfs && key.compareTo(list.get(base + hint + ofs).getKey()) > 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if (ofs <= 0) ofs = maxOfs; // int overflow guard
            }
            ofs = Math.min(ofs, maxOfs);
            lastOfs += hint;
            ofs += hint;
        } else {
            // Key is left of (or at) hint — exponentially search leftward
            int maxOfs = hint + 1;
            while (ofs < maxOfs && key.compareTo(list.get(base + hint - ofs).getKey()) <= 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if (ofs <= 0) ofs = maxOfs;
            }
            ofs = Math.min(ofs, maxOfs);
            int tmp = lastOfs;
            lastOfs = hint - ofs;
            ofs     = hint - tmp;
        }

        // Binary search in (lastOfs, ofs] to pinpoint exact position
        lastOfs++;
        while (lastOfs < ofs) {
            int m = lastOfs + ((ofs - lastOfs) >>> 1);
            if (key.compareTo(list.get(base + m).getKey()) > 0) lastOfs = m + 1;
            else ofs = m;
        }
        return ofs;
    }

    // Find rightmost position in list[base..base+len) where key should be inserted.
    // "Rightmost" means: last slot where list[slot] <= key.
    private int gallopRight(T key, List<SimpleEntry<T, Integer>> list, int base, int len, int hint) {
        int lastOfs = 0, ofs = 1;

        if (key.compareTo(list.get(base + hint).getKey()) < 0) {
            int maxOfs = hint + 1;
            while (ofs < maxOfs && key.compareTo(list.get(base + hint - ofs).getKey()) < 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if (ofs <= 0) ofs = maxOfs;
            }
            ofs = Math.min(ofs, maxOfs);
            int tmp = lastOfs;
            lastOfs = hint - ofs;
            ofs     = hint - tmp;
        } else {
            int maxOfs = len - hint;
            while (ofs < maxOfs && key.compareTo(list.get(base + hint + ofs).getKey()) >= 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if (ofs <= 0) ofs = maxOfs;
            }
            ofs = Math.min(ofs, maxOfs);
            lastOfs += hint;
            ofs     += hint;
        }

        lastOfs++;
        while (lastOfs < ofs) {
            int m = lastOfs + ((ofs - lastOfs) >>> 1);
            if (key.compareTo(list.get(base + m).getKey()) < 0) ofs = m;
            else lastOfs = m + 1;
        }
        return ofs;
    }

    // -------------------------------------------------------------------------
    // MERGE METHODS
    // mergeLo: left run is smaller — copy left to buffer, merge left-to-right.
    // mergeHi: right run is smaller — copy right to buffer, merge right-to-left.
    // Always copying the smaller run minimises buffer allocation.
    // -------------------------------------------------------------------------

    private void mergeLo(List<SimpleEntry<T, Integer>> list, int base1, int len1, int base2, int len2) {
        List<SimpleEntry<T, Integer>> tmp = new ArrayList<>(list.subList(base1, base1 + len1));
        int c1 = 0, c2 = base2, dest = base1;
        int mg = minGallop;

        outer:
        while (true) {
            int cnt1 = 0, cnt2 = 0;

            // Linear mode: merge one-by-one until one side wins mg times in a row
            do {
                if (list.get(c2).getKey().compareTo(tmp.get(c1).getKey()) < 0) {
                    list.set(dest++, list.get(c2++));
                    len2--; cnt2++; cnt1 = 0;
                    if (len2 == 0) break outer;
                } else {
                    list.set(dest++, tmp.get(c1++));
                    len1--; cnt1++; cnt2 = 0;
                    if (len1 == 0) break outer;
                }
            } while ((cnt1 | cnt2) < mg);

            // Gallop mode: use exponential search to skip entire runs at once
            do {
                cnt1 = gallopRight(list.get(c2).getKey(), tmp, c1, len1, 0);
                if (cnt1 > 0) {
                    for (int k = 0; k < cnt1; k++) list.set(dest++, tmp.get(c1++));
                    len1 -= cnt1;
                    if (len1 == 0) break outer;
                }
                list.set(dest++, list.get(c2++));
                if (--len2 == 0) break outer;

                cnt2 = gallopLeft(tmp.get(c1).getKey(), list, c2, len2, 0);
                if (cnt2 > 0) {
                    for (int k = 0; k < cnt2; k++) list.set(dest++, list.get(c2++));
                    len2 -= cnt2;
                    if (len2 == 0) break outer;
                }
                list.set(dest++, tmp.get(c1++));
                if (--len1 == 0) break outer;

                mg = Math.max(mg - 1, 1); // Reward gallop mode — make it easier to re-enter
            } while (cnt1 >= MIN_GALLOP || cnt2 >= MIN_GALLOP);

            mg += 2; // Penalise for exiting gallop mode
        }

        // Flush remaining left buffer into list
        while (len1 > 0) { list.set(dest++, tmp.get(c1++)); len1--; }
        // Right run is already in place — nothing to do
        minGallop = Math.max(mg, 1);
    }

    private void mergeHi(List<SimpleEntry<T, Integer>> list, int base1, int len1, int base2, int len2) {
        List<SimpleEntry<T, Integer>> tmp = new ArrayList<>(list.subList(base2, base2 + len2));
        int c1   = base1 + len1 - 1; // rightmost element of left run in list
        int c2   = len2 - 1;          // rightmost element of right run in tmp
        int dest = base2 + len2 - 1;  // rightmost output slot
        int mg   = minGallop;

        outer:
        while (true) {
            int cnt1 = 0, cnt2 = 0;

            do {
                if (tmp.get(c2).getKey().compareTo(list.get(c1).getKey()) >= 0) {
                    list.set(dest--, tmp.get(c2--));
                    len2--; cnt2++; cnt1 = 0;
                    if (len2 == 0) break outer;
                } else {
                    list.set(dest--, list.get(c1--));
                    len1--; cnt1++; cnt2 = 0;
                    if (len1 == 0) break outer;
                }
            } while ((cnt1 | cnt2) < mg);

            do {
                // How many from left run can we copy in bulk?
                cnt1 = len1 - gallopLeft(tmp.get(c2).getKey(), list, base1, len1, len1 - 1);
                if (cnt1 > 0) {
                    dest -= cnt1; c1 -= cnt1; len1 -= cnt1;
                    for (int k = cnt1 - 1; k >= 0; k--) list.set(dest + 1 + k, list.get(c1 + 1 + k));
                    if (len1 == 0) break outer;
                }
                list.set(dest--, tmp.get(c2--));
                if (--len2 == 0) break outer;

                // How many from right run (tmp) can we copy in bulk?
                cnt2 = (c2 + 1) - gallopRight(list.get(c1).getKey(), tmp, 0, c2 + 1, c2);
                if (cnt2 > 0) {
                    dest -= cnt2; c2 -= cnt2; len2 -= cnt2;
                    for (int k = cnt2 - 1; k >= 0; k--) list.set(dest + 1 + k, tmp.get(c2 + 1 + k));
                    if (len2 == 0) break outer;
                }
                list.set(dest--, list.get(c1--));
                if (--len1 == 0) break outer;

                mg = Math.max(mg - 1, 1);
            } while (cnt1 >= MIN_GALLOP || cnt2 >= MIN_GALLOP);

            mg += 2;
        }

        // Flush remaining right buffer
        while (len2 > 0) { list.set(dest--, tmp.get(c2--)); len2--; }
        // Left run already in place
        minGallop = Math.max(mg, 1);
    }

    // -------------------------------------------------------------------------
    // OVERRIDE mergeAt
    // Adds two optimisations over the parent's naive merge:
    //   1. Trim — gallopRight/Left to skip already-ordered prefix/suffix before merging
    //   2. Small-buffer strategy — always copy the smaller run into the tmp buffer
    // -------------------------------------------------------------------------

    protected int mergeAt(List<SimpleEntry<T, Integer>> list, int[][] stack, int size, int i) {
        int base1 = stack[i][0],   len1 = stack[i][1];
        int base2 = stack[i+1][0], len2 = stack[i+1][1];

        // Update stack to reflect the new merged run
        stack[i][1] = len1 + len2;
        if (i == size - 3) {
            stack[i+1][0] = stack[i+2][0];
            stack[i+1][1] = stack[i+2][1];
        }

        // Trim prefix: find where the first element of the right run would go in the left run.
        // Everything before that index is already in its final position.
        int k = gallopRight(list.get(base2).getKey(), list, base1, len1, 0);
        base1 += k; len1 -= k;
        if (len1 == 0) return size - 1;

        // Trim suffix: find where the last element of the left run would go in the right run.
        // Everything after that index is already in its final position.
        len2 = gallopLeft(list.get(base1 + len1 - 1).getKey(), list, base2, len2, len2 - 1);
        if (len2 == 0) return size - 1;

        // Merge using smaller run as buffer — minimises memory allocation
        if (len1 <= len2) mergeLo(list, base1, len1, base2, len2);
        else              mergeHi(list, base1, len1, base2, len2);

        return size - 1;
    }

    @Override
    public String toString() {
        return "TimSort";
    }
}