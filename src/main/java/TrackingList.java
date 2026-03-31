import java.util.AbstractList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TrackingList<T> extends AbstractList<T> {

    private final List<T> delegate;
    private final BiConsumer<Integer, T> onWrite;
    private final Consumer<Integer> onRead;

    public TrackingList(List<T> delegate, BiConsumer<Integer, T> onWrite, Consumer<Integer> onRead) {
        this.delegate = delegate;
        this.onWrite  = onWrite;
        this.onRead   = onRead;
    }

    @Override
    public T get(int index) {
        onRead.accept(index); // Fires the Yellow "READ" frame
        return delegate.get(index);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public T set(int index, T element) {
        T previous = delegate.set(index, element);
        onWrite.accept(index, element); // Fires the Red "WRITE" frame
        return previous;
    }
}