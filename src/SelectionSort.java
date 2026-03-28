import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public class SelectionSort<T extends Comparable<T>> extends Sort<T> {

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        List<SimpleEntry<T, Integer>> sortMe = List.copyOf(list);
        int n = sortMe.size();

        int smallest = 0;
        int curr;
        SimpleEntry<T, Integer> temp;
        int count = 0;

        while (!isSorted(sortMe)) {
            for (int i = count; i < n; i++) {
                curr = i;

                if (sortMe.get(curr).getKey().compareTo(sortMe.get(smallest).getKey()) < 0) {
                    smallest = curr;
                }
            }

            temp = sortMe.get(count);
            sortMe.set(count, smallest);
            smallest = temp;

            count++;
        }

        return sortMe;
    }

}
