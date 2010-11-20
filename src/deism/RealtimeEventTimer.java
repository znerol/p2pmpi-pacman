package deism;

/**
 * EventTimer implementation using the RealtimeClock
 * 
 * RealtimeEventTimer implements a mechanism to delay events until the timestamp
 * of an event is reached by means of a RealtimeClock instance.
 */
public class RealtimeEventTimer implements EventTimer {
    Clock clock;
    long wakeupTime;

    public RealtimeEventTimer(Clock clock) {
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
        wakeupTime = Long.MAX_VALUE;
        
        try {
            if (e == null) {
                this.wait();
            }
            else {
                wakeupTime = e.getSimtime();
                long delay = clock.getRealtime(wakeupTime)-clock.getRealtime();
                if (delay > 0) {
                    this.wait(delay);
                }
            }
        }
        catch (InterruptedException ex) {
            // ignored intentionally
        }

        return wakeupTime;
    }

    @Override
    public synchronized void wakeup() {
        wakeup(clock.getSimtime());
    }
    
    @Override
    public synchronized void wakeup(long wakeupTime) {
        this.wakeupTime = Math.min(this.wakeupTime, wakeupTime);
        this.notify();
    }
}
