import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public abstract class Sort<T extends Comparable<T>> {

    protected abstract List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list);

    protected boolean isSorted(List<SimpleEntry<T, Integer>> list) {
        List<SimpleEntry<T, Integer>> defense = List.copyOf(list);
        int  n = defense.size();

        for (int i = 1; i < n; i++) {
            if (defense.get(i).getKey().compareTo(defense.get(i - 1).getKey()) < 0) {
                return false;
            }
        }

        return true;
    }

    protected boolean isStable(List<SimpleEntry<T, Integer>> list) {
        List<SimpleEntry<T, Integer>> defense = List.copyOf(list);
        int n = defense.size();

        for (int i = 1; i< n; i++) {
            if (defense.get(i).getKey().compareTo(defense.get(i - 1).getKey()) == 0) {
                if (defense.get(i - 1).getValue() > defense.get(i).getValue()) {
                    return false;
                }
            }
        }

        return true;
    }

    protected void swap(List<SimpleEntry<T, Integer>> list, int i, int j) {
        SimpleEntry<T, Integer> temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

     @Override
     public abstract String toString();

}
