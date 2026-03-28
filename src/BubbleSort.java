import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public class BubbleSort<T extends Comparable<T>> extends Sort<T> {

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        List<SimpleEntry<T, Integer>> sortMe = List.copyOf(list);
        int n = list.size();

        return List.of();
    }

}
