import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Random;

// Author: David Chan (Luckder)

public class BogoSort<T extends Comparable<T>> extends Sort<T> {

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        Random rng = new Random();
        int n = list.size();

        while (!isSorted(list)) {
            // Fisher–Yates Modern Shuffling Algorithm
            for (int i = n - 1; i > 0; i--) {
                int j = rng.nextInt(i + 1);

                super.swap(list, i, j);
            }
        }

        return list;
    }

    @Override
    public String toString() {
        return "BogoSort";
    }

}
