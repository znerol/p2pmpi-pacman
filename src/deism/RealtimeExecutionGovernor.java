package deism;

/**
 * ExecutionGovernor implementation using the RealtimeClock
 * 
 * RealtimeExecutionGovernor implements a mechanism to delay events until the
 * timestamp of an event is reached by means of a RealtimeClock instance.
 */
public class RealtimeExecutionGovernor implements ExecutionGovernor {
    Clock clock;
    long wakeupTime;

    public RealtimeExecutionGovernor(Clock clock) {
        this.clock = clock;
    }
    
    /**
     * Delays execution of current thread until something calls resume.
     */
    @Override
    public synchronized long suspend() {
        wakeupTime = Long.MAX_VALUE;
        
        try {
            this.wait();
        }
        catch (InterruptedException ex) {
            // ignored intentionally
        }
        
        return wakeupTime;
    }

    /**
     * Delays execution of current thread until timestamp of the given event is
     * reached. If the event is null the thread is suspended indefinitely. If
     * the timestamp of the event lies in the past, this method returns
     * immediately.
     * 
     * @result true if the timeout was reached, false if the method was
     *         interrupted by a call to resume.
     */
    @Override
    public synchronized long suspendUntil(long simtime) {
        wakeupTime = simtime;
        
        try {
            long delay = clock.getRealtime(wakeupTime)-clock.getRealtime();
            if (delay > 0) {
                this.wait(delay);
            }
        }
        catch (InterruptedException ex) {
            // ignored intentionally
        }

        return wakeupTime;
    }

    @Override
    public synchronized void resume() {
        this.wakeupTime = Math.min(this.wakeupTime, clock.getSimtime());
        this.notify();
    }
    
    @Override
    public synchronized void resume(long wakeupTime) {
        this.wakeupTime = Math.min(this.wakeupTime, wakeupTime);
        this.wakeupTime = Math.min(this.wakeupTime, clock.getSimtime());
        this.notify();
    }
}
