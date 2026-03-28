import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public class CosmicSort<T extends Comparable<T>> extends Sort<T> {

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        List<SimpleEntry<T, Integer>> sortMe = new ArrayList<>(list);

        while(!isSorted(sortMe)) {
            try {
                // Waits to give time for cosmic rays to flip bits in memory
                Thread.sleep(5 * 1000); // 5 Seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return sortMe;
    }

    @Override
    public String toString() {
        return "CosmicSort";
    }

}