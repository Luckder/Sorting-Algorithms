package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public class QuickSort3Way<T extends Comparable<T>> extends Sort<T> {
    // Bentley-McIlroy

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null)  { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        partition(list, 0, list.size() - 1);

        return list;
    }

    protected void partition(List<SimpleEntry<T, Integer>> list, int l, int r) {
        if  (l >= r) { return; }

        int i = l - 1, j = r;
        int p = l - 1, q = r;
        SimpleEntry<T, Integer> v = list.get(r);

        while (true)
        {

            // From left, find the first element greater than
            // or equal to v. This loop will definitely
            // terminate as v is last element
            while (list.get(++i).getKey().compareTo(v.getKey()) < 0)
                ;

            // From right, find the first element smaller than
            // or equal to v
            while (list.get(--j).getKey().compareTo(v.getKey()) > 0)
                if (j == l)
                    break;

            // If i and j cross, then we are done
            if (i >= j)
                break;

            // Swap, so that smaller goes on left greater goes
            // on right
            super.swap(list, i, j);

            // Move all same left occurrence of pivot to
            // beginning of array and keep count using p
            if (list.get(i).getKey().compareTo(v.getKey()) == 0) {
                p++;
                super.swap(list, i, p);
            }

            // Move all same right occurrence of pivot to end of
            // array and keep count using q
            if (list.get(j).getKey().compareTo(v.getKey()) == 0) {
                q--;
                super.swap(list, q, j);
            }
        }

        // Move pivot element to its correct index
        super.swap(list, i, r);

        // Move all left same occurrences from beginning
        // to adjacent to arr[i]
        j = i - 1;

        for (int k = l; k <= p; k++, j--)
        {
            super.swap(list, j, k);
        }

        // Move all right same occurrences from end
        // to adjacent to arr[i]
        i = i + 1;

        for (int k = r - 1; k >= q; k--, i++)
        {
            super.swap(list, i, k);
        }

        partition(list, l, j);
        partition(list, i, r);
    }

    @Override
    public String toString() {
        return "QuickSort (3-Way)";
    }
}
