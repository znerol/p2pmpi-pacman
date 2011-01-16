package deism.ipc.async;

/**
 * Interface for single blocking receive operations
 * 
 * @param <T> Type of message
 */
public interface BlockingReceiveOperation<T> {
    public T receive() throws InterruptedException;
}
