package deism;

/**
 * EventTimer implementation using the RealtimeClock
 * 
 * RealtimeEventTimer implements a mechanism to delay events until the timestamp
 * of an event is reached by means of a RealtimeClock instance.
 */
public class RealtimeEventTimer implements EventTimer {
    Clock clock;

    public RealtimeEventTimer(Clock clock) {
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
    public synchronized long waitForEvent(Event e) {
        long result = e.getSimtime();

        try {
            if (e == null) {
                this.wait();
            }
            else {
                long delay = clock.getRealtime(e.getSimtime()) -
                    clock.getRealtime();
                if (delay > 0) {
                    this.wait(delay);
                }
            }
        }
        catch (InterruptedException ex) {
            long currentSimtime = clock.getSimtime();
            result = Math.min(currentSimtime, e.getSimtime());
        }

        return result;
    }

    @Override
    public synchronized void wakeup() {
        this.notify();
    }
}
