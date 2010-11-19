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
    public long waitForEvent(Event e) {
        return e.getSimtime();
    }

    @Override
    public void wakeup() {
        /* Intentionally left empty */
    }
    
    @Override
    public void wakeup(long wakeupTime) {
        /* Intentionally left empty */
    }
}
