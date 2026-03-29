import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import algorithms.*;
import java.util.*;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class SortingTest {

    private final List<Sort<? extends Comparable<?>>> algorithms = load();

    private List<Sort<? extends Comparable<?>>> load() {
        List<Sort<? extends Comparable<?>>> list = new ArrayList<>();

        try (ScanResult scan = new ClassGraph()
                .enableClassInfo()
                .acceptPackages("algorithms")
                .scan()) {

            for (ClassInfo info : scan.getSubclasses(Sort.class)) {
                if (!info.isAbstract()) {
                    @SuppressWarnings("unchecked")
                    Sort<Integer> instance = (Sort<Integer>) info.loadClass()
                            .getDeclaredConstructor()
                            .newInstance();
                    list.add(instance);
                }
            }

            return list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sorting algorithms", e);
        }
    }

    @Test
    void sortTestOne() {
        @SuppressWarnings("unchecked")
        Sort<Integer> countingSort = (Sort<Integer>) this.algorithms.stream()
                .filter(alg -> alg.toString().equals("CountingSort"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("CountingSort not found"));

        assertTrue(countingSort.isSorted(countingSort.sort(Main.makeIntegerList(100001))), "List has to be sorted!");
    }

}