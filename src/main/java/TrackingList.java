import java.util.AbstractList;
import java.util.List;
import java.util.function.BiConsumer;

// Wraps any List and fires a callback on every set() call.
// Requires zero changes to any sorting algorithm.
public class TrackingList<T> extends AbstractList<T> {

    private final List<T> delegate;
    private final BiConsumer<Integer, T> onWrite;

    public TrackingList(List<T> delegate, BiConsumer<Integer, T> onWrite) {
        this.delegate = delegate;
        this.onWrite  = onWrite;
    }

    @Override
    public T get(int index) {
        return delegate.get(index);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public T set(int index, T element) {
        T previous = delegate.set(index, element); // perform the actual write
        onWrite.accept(index, element);             // notify visualiser
        return previous;
    }
}