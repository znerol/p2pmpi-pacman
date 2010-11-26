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
     * Compute current event if necessary.
     * 
     * This method is called before each cycle thru the runloop. An EventSource
     * may place logic to determine the event returned by subsequent calls
     * to peek and poll.
     */
    void compute(long currentSimtime);
    
    /**
     * Retrieves and removes the next event.
     * 
     * @return The next event or null
     */
    Event poll();
    
    /**
     * Put back the last event polled from this EventSource
     */
    void reject(Event event);
}
