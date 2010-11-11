package deism;

/**
 * Simple realtime implementation of EventLoop
 * 
 * This EventRunloop implementation respects the simtime property of the events
 * supplied by the event source such that if an event for a future point in
 * time is received, execution is delayed adequately.
 * 
 * FastForwardRunloop verifies that events delivered by the event source arrive
 * in the proper order i.e. with increasing timestamps. Otherwise an
 * EventSourceOrderException is thrown.
 */
public class FastForwardRunloop implements EventRunloop {
	private boolean stop = false;
	private EventMatcher terminationCondition = null;
	private EventMonitor monitor;
	private long lastsimtime = 0;
	
	public FastForwardRunloop(EventMonitor monitor,
			EventMatcher terminationCondition) {
		if (monitor == null) {
			throw new IllegalArgumentException(
					"FastForwardRunloop cannot operate without an event monitor");
		}
		this.monitor = monitor;
		this.terminationCondition = terminationCondition;
	}
	
	/**
	 * Wait for Event timestamp
	 * 
	 * Suspend execution until the events timestamp has been reached. if the
	 * events timestamp lies in the past, the method returns immediately.
	 * Suspend indefinitely if event is null.
	 * 
	 * Use wakeup() to resume before the timeout is reached.
	 * 
	 * @param e
	 * @throws InterruptedException
	 */
	
	@Override
	public void run(EventSource source, EventDispatcher disp)
	throws EventSourceOrderException {
		while (!stop) {
			Event peekEvent = source.peek();
			
			if (terminationCondition.match(peekEvent)) {
				break;
			}
			
			/*
			 * Suspend execution until its time to handle the event.
			 */
			boolean timeoutExpired = monitor.waitForEvent(peekEvent);
			if (!timeoutExpired) {
				/* 
				 * If wait was interrupted someone called wakeup(). We have to
				 * check the loop condition and peek again on the source.
				 */
				continue;
			}
			
			if (lastsimtime > peekEvent.getSimtime()) {
				throw new EventSourceOrderException(
						"Event source returns events out of sequence");
			}
			lastsimtime = peekEvent.getSimtime();
			
			/*
			 * This is moderately ugly. We have to remove the peek event and
			 * we really want to be sure that this was actually the same like
			 * the one we peeked before. Otherwise it could indicate a bug in
			 * the EventSource or some concurrency issue.
			 */
			Event polledEvent = source.poll();
			assert peekEvent == polledEvent;
			
			disp.dispatchEvent(polledEvent);
		}
	}

	@Override
	public void wakeup() {
		monitor.wakeup();
	}

	@Override
	public void stop() {
		stop = true;
		wakeup();
	}
}
