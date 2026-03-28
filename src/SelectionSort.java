import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public class SelectionSort<T extends Comparable<T>> extends Sort<T> {

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        List<SimpleEntry<T, Integer>> sortMe = new ArrayList<>(list);
        int n = sortMe.size();

        int smallest = 0;
        int curr;
        SimpleEntry<T, Integer> temp;
        int count = 0;

        // Does not need to use a while loop because selection sort is guaranteed to be sorted after n - 1 iterations
        for (int j = 0; j < n - 1; j++) {
            for (int i = count; i < n; i++) {
                curr = i;

                if (sortMe.get(curr).getKey().compareTo(sortMe.get(smallest).getKey()) < 0) {
                    smallest = curr;
                }
            }

            temp = sortMe.get(count);
            sortMe.set(count, sortMe.get(smallest));
            sortMe.set(smallest, temp);
            count++;
            smallest = count;
        }

        return sortMe;
    }

    @Override
    public String toString() {
        return "SelectionSort";
    }

}
