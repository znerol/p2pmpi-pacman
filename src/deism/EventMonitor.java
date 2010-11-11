package deism;

/**
 * Implementations of event monitor must provide a way to suspend the current
 * thread until delivery of the event is save i.e. the timestamp of the event
 * is reached.
 */
public interface EventMonitor {
	
	/**
	 * Wait until the timestamp of the given event is reached. Returns false
	 * if wakeup was called before the timestamp is reached.
	 * 
	 * @param e event to monitor.
	 * @return true if timeout expired, false if monitor was notified before.
	 */
	boolean waitForEvent(Event e);
	void wakeup();
}
