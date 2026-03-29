package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public interface Sortable<T extends Comparable<T>> {

    List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list);

}
