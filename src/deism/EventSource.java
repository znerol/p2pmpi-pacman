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
     * Retrieves the next event removing it from the EventSource
     * 
     * @param currentSimtime current timestamp in simulation time units
     * @return The next event or null
     */
    Event receive(long currentSimtime);

    /**
     * Accept the last event received from this EventSource
     */
    void accept(Event event);

    /**
     * Put back the last event received from this EventSource
     */
    void reject(Event event);

    /**
     * Prepare the EventSource (spawn threads etc.)
     */
    void start(long startSimtime);

    /**
     * Cleanup EventSource (terminate threads etc.)
     */
    void stop();
}
