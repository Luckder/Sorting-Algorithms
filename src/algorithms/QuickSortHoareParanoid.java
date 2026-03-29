package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Random;

public class QuickSortHoareParanoid<T extends Comparable<T>> extends QuickSortHoare<T> {

    private final Random rng = new Random();

    @Override
    protected int partition(List<SimpleEntry<T, Integer>> list, int low, int high) {
        int n = high - low + 1;

        // Paranoid check meaningless for tiny subarrays
        if (n <= 3) {
            super.swap(list, low + rng.nextInt(n), high);
            return super.partition(list, low, high);
        }

        int pivotIndex;

        do {
            int randomIndex = low + rng.nextInt(n);
            super.swap(list, randomIndex, low); // Hoare uses first element as pivot
            pivotIndex = super.partition(list, low, high);
        } while (pivotIndex < low + n / 4 || pivotIndex > low + 3 * n / 4); // 25 | 75

        return pivotIndex;
    }

     @Override
    public String toString() {
        return "Paranoid QuickSort (Hoare)";
    }
}
