package deism;

/**
 * EventSink implementations will get the next event from EventSource before
 * thread suspension.
 */
public interface EventSink {
    /**
     * The next event which will most likely be handled by an EventDispatcher.
     * An EventSink must also accept antimessages for each event offered before.
     *
     * @param event
     * @return true if EventSink took notice of the event, false otherwise
     */
    void offer(Event event);

    /**
     * Prepare the EventSource (spawn threads etc.)
     */
    void start(long startSimtime);

    /**
     * Cleanup EventSource (terminate threads etc.)
     */
    void stop();
}
