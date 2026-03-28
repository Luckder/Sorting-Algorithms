import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public class InsertionSort<T extends Comparable<T>> extends Sort<T> {

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        List<SimpleEntry<T, Integer>> sortMe = new ArrayList<>(list);
        int n = list.size();

        for (int i = 1; i < n; i++) {
            SimpleEntry<T, Integer> curr = sortMe.get(i);
            int j = i - 1;

            // j always points to an empty space in the left subarray
            while (j >= 0 && sortMe.get(j).getKey().compareTo(curr.getKey()) > 0) {
                sortMe.set(j + 1, sortMe.get(j));
                j--;
            }

            // j points to the left of the empty space
            sortMe.set(j + 1, curr);
        }

        return sortMe;
    }

    @Override
    public String toString() {
        return "InsertionSort";
    }

}
