package deism;

/**
 * EventSource implementations provide the EventRunloop with events
 * 
 * Typical implementations of EventSource include realtime input from the user
 * via keyboard, network events received from another instance, simulation
 * models or previously recorded event streams.
 */
public interface EventSource {
    /**
     * Retrieves, but does not remove, the next event.
     * 
     * @return The next event or null
     */
    Event peek(long currentSimtime);

    /**
     * Retrieves and removes the next event.
     * 
     * @return The next event or null
     */
    Event poll(long currentSimtime);
}
