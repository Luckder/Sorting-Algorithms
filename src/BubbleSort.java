import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public class BubbleSort<T extends Comparable<T>> extends Sort<T> {

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        List<SimpleEntry<T, Integer>> sortMe = new ArrayList<>(list);
        int n = list.size();

        boolean notSorted = true;

        while(notSorted) {
            notSorted = false; // Assume sorted

            for (int i = 1; i < n; i++) {
                if (sortMe.get(i).getKey().compareTo(sortMe.get(i - 1).getKey()) < 0) {
                    notSorted = true; // List is not sorted

                    SimpleEntry<T, Integer> temp = sortMe.get(i);
                    sortMe.set(i, sortMe.get(i - 1));
                    sortMe.set(i - 1, temp);
                }
            }
        }

        return sortMe;
    }

    @Override
    public String toString() {
        return "BubbleSort";
    }

}
