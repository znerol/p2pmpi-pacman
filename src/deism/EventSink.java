package deism;

/**
 * EventSink implementations will get the next event from EventSource before
 * thread suspension.
 */
public interface EventSink {
    /**
     * The next event which will most likely be handled by an EventDispatcher.
     *
     * @param event
     * @return true if EventSink took notice of the event, false otherwise
     */
    boolean offer(Event event);

    /**
     * Remove the event offered to this EventSink before, most likely because
     * an event with a smaller timestamp has arrived during governor.suspend.
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
