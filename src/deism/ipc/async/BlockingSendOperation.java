package deism.ipc.async;

/**
 * Interface for single blocking send operations
 *
 * @param <T> Type of message
 */
public interface BlockingSendOperation<T> {
    public void send(T item) throws InterruptedException;
}
