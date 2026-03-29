package algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

// Author: David Chan (Luckder)

public interface SwapEventListener<T extends Comparable<T>> {
    void onSwap(List<SimpleEntry<T, Integer>> list, int i, int j);
    // void onCompare(int i, int j); // optional
}