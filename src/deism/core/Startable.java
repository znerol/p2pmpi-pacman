package deism.core;

/**
 * Startable objects get notified before a runloop starts and after it
 * terminates.
 */
public interface Startable {
    /**
     * Hook called before the runloop peeks on the first event source.
     * @param simtime timestamp in simulation time units
     */
    public void start(long simtime);

    /**
     * Hook called after the last event was dispatched.
     * @param simtime timestamp in simulation time units
     */
    public void stop(long simtime);

    /**
     * Called after stop in order to give threaded instances a chance to join
     */
    public void join();
}
