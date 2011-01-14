package deism.ipc.base;

/**
 * Destination for ipc messages.
 *
 * Typically an endpoint queues the item for later processing or sending to
 * another thread or host. This function must not block.
 */
public interface Endpoint<T> {
    public void send(T item);
}
