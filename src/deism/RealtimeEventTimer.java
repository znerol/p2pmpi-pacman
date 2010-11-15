package deism;

/**
 * EventTimer implementation using the RealtimeClock
 * 
 * RealtimeEventTimer implements a mechanism to delay events until the timestamp
 * of an event is reached by means of a RealtimeClock instance.
 */
public class RealtimeEventTimer implements EventTimer {
    RealtimeClock clock;

    public RealtimeEventTimer(RealtimeClock clock) {
        if (clock == null) {
            throw new IllegalArgumentException(
                    "RealtimeRunloop cannot operate without a clock");
        }
        this.clock = clock;
    }

    /**
     * Delays execution of current thread until timestamp of the given event is
     * reached. If the event is null the thread is suspended indefinitely. If
     * the timestamp of the event lies in the past, this method returns
     * immediately.
     * 
     * @param e
     *            the event we're waiting for
     * @result true if the timeout was reached, false if the method was
     *         interrupted by a call to wakeup.
     */
    @Override
    public synchronized boolean waitForEvent(Event e) {
        boolean result = true;

        try {
            if (e == null) {
                this.wait();
            }
            else {
                long delay = clock.getRealtimeDifference(e.getSimtime()
                        - clock.getSimtime());
                if (delay > 0) {
                    this.wait(delay);
                }
            }
        }
        catch (InterruptedException ex) {
            result = false;
        }

        return result;
    }

    @Override
    public void wakeup() {
        this.notify();
    }
}
