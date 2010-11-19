package deism;

/**
 * Implementations of event timer must provide a way to suspend the current
 * thread until delivery of the event is save i.e. the timestamp of the event is
 * reached.
 */
public interface EventTimer {

    /**
     * Wait until the timestamp of the given event is reached. Return the
     * simulation time reached which must not be greater than simtime of e.
     * 
     * @param e
     *            event to wait for.
     * @return true if timeout expired, false if timer was notified before.
     */
    long waitForEvent(Event e);

    /**
     * Interrupt waitForEvent method.
     */
    void wakeup();
}
