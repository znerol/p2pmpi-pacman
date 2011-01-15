package deism.core;

/**
 * EventSink implementations get the next event from EventSource before the
 * governor is told to suspend the current runloop.
 */
public interface EventSink {
    /**
     * The next event which will most likely be handled by an EventDispatcher.
     * An EventSink must also accept and act on anti-Events for events offered
     * to it before.
     * 
     * @param event
     */
    void offer(Event event);
}
