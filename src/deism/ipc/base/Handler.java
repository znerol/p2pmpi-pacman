package deism.ipc.base;

/**
 * Handler for ipc messages.
 *
 * A handler takes action according to the received item.
 */
public interface Handler<T> {
    public void handle(T item);
}
