package deism;

/**
 * FastForward implementation of EventTimer
 * 
 * This implementation of the EventTimer interface will result in immediate
 * delivery of the events in a runloop. Use this class to e.g. replay recorded
 * event streams.
 */
public class NoDelayEventTimer implements EventTimer {
	@Override
	public boolean waitForEvent(Event e) {
		return true;
	}

	@Override
	public void wakeup() {
		/* Intentionally left empty */
	}
}
