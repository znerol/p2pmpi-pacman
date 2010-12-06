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
     * Retrieves the next without removing it from the EventSource.
     * 
     * @param currentSimtime
     *            current timestamp in simulation time units
     * @return The next event or null
     */
    Event peek(long currentSimtime);

    /**
     * Accept and remove the given event from this EventSource. EventRunloop
     * guarantees that the event is the last event returned by a preceding call
     * to peek.
     */
    void remove(Event event);

    /**
     * Prepare the EventSource (spawn threads etc.)
     */
    void start(long startSimtime);

    /**
     * Cleanup EventSource (terminate threads etc.)
     */
    void stop();
}
