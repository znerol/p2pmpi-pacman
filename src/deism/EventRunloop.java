package deism;

/**
 * EventRunloop implementations iterate thru events from an eventSource
 */
public interface EventRunloop {
	/**
	 * Loops thru all events from the event source and delegates them to the
	 * dispatcher.
	 * 
	 * @param eventSource
	 * @param eventDispatcher
	 */
	void run(EventSource eventSource, EventDispatcher eventDispatcher)
	throws EventSourceOrderException;
	
	/**
	 * Signals the EventLoop to continue processing events if the loop was
	 * suspended before.
	 */
	void wakeup();
	
	/**
	 * Signals the EventLoop to stop processing Events and return from the run
	 * method;
	 */
	void stop();
}
