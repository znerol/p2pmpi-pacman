package deism.core;

/**
 * Implementers of this interface are notified before the runloop gets
 * suspended. This is a good place to flush buffers of outgoing connections.
 */
public interface Flushable {
    /**
     * Called by the DefaultEventRunloop every time immediately before it is
     * suspended.
     */
    public void flush(long simtime);
}
