import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Random;

public class QuickSortLomutoParanoid<T extends Comparable<T>> extends QuickSortLomuto<T> {

    private final Random rng = new Random();

    @Override
    protected int partition(List<SimpleEntry<T, Integer>> list, int low, int high) {
        int n = high - low + 1;

        // Small arrays: don't overthink
        if (n <= 3) {
            super.swap(list, low + rng.nextInt(n), high);
            return super.partition(list, low, high);
        }

        int randomIndex;
        int pivotIndex;

        int attempts = 0;
        int maxAttempts = 20; // safety cap

        while (true) {
            randomIndex = low + rng.nextInt(n);
            T pivot = list.get(randomIndex).getKey();

            // --- simulate partition (NO SWAPS) ---
            int countLess = 0;
            for (int j = low; j <= high; j++) {
                if (list.get(j).getKey().compareTo(pivot) < 0) {
                    countLess++;
                }
            }

            pivotIndex = low + countLess;

            // check 25%–75% balance
            if (pivotIndex >= low + n / 4 && pivotIndex <= low + 3 * n / 4) {
                break;
            }

            attempts++;
            if (attempts >= maxAttempts) {
                // give up and accept whatever we have
                break;
            }
        }

        // --- NOW do the real partition ONCE ---
        super.swap(list, randomIndex, high);
        return super.partition(list, low, high);
    }

     @Override
    public String toString() {
        return "Paranoid QuickSort (Lomuto)";
    }
}
