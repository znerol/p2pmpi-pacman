package deism;

/**
 * Implementations of event timer must provide a way to suspend the current
 * thread until delivery of the event is save i.e. the timestamp of the event is
 * reached.
 */
public interface EventTimer {

    /**
     * Wait until the timestamp of the given event is reached. Returns false if
     * wakeup was called before the timestamp is reached.
     * 
     * @param e
     *            event to wait for.
     * @return true if timeout expired, false if timer was notified before.
     */
    boolean waitForEvent(Event e);

    /**
     * Interrupt waitForEvent method.
     */
    void wakeup();
}
