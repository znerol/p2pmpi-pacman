package deism;

/**
 * FastForward implementation of EventMonitor
 * 
 * This implementation of the EventMonitor interface will result in immediate
 * delivery of the events in a runloop. Use this class to e.g. replay recorded
 * event streams.
 */
public class NoDelayEventMonitor implements EventMonitor {
	@Override
	public boolean waitForEvent(Event e) {
		return true;
	}

	@Override
	public void wakeup() {
		/* Intentionally left empty */
	}
}
