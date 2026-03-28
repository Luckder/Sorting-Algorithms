import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public abstract class Sort<T extends Comparable<T>> {

    public abstract List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list);

    public boolean isSorted(List<SimpleEntry<T, Integer>> list) {
        List<SimpleEntry<T, Integer>> defense = List.copyOf(list);
        int  n = defense.size();

        for (int i = 1; i < n; i++) {
            if (defense.get(i).getKey().compareTo(defense.get(i - 1).getKey()) < 0) {
                return false;
            }
        }

        return true;
    }

    public boolean isStable(List<SimpleEntry<T, Integer>> list) {
        List<SimpleEntry<T, Integer>> defense = List.copyOf(list);
        int n = defense.size();

        for (int i = 1; i< n; i++) {
            if (defense.get(i).getKey().compareTo(defense.get(i - 1).getKey()) == 0) {
                return defense.get(i - 1).getValue() < defense.get(i).getValue();
            }
        }

        return true;
    }

}
