import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BogoSort<T extends Comparable<T>> extends Sort<T> {

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        List<SimpleEntry<T, Integer>> sortMe = new ArrayList<>(list);
        Random rng = new Random();
        int n = sortMe.size();

        while (!isSorted(sortMe)) {
            // Fisher–Yates Modern Shuffling Algorithm
            for (int i = n - 1; i > 0; i--) {
                int j = rng.nextInt(i + 1);

                SimpleEntry<T, Integer> temp = sortMe.get(i);
                sortMe.set(i, sortMe.get(j));
                sortMe.set(j, temp);
            }
        }

        return sortMe;
    }

    @Override
    public String toString() {
        return "BogoSort";
    }

}
