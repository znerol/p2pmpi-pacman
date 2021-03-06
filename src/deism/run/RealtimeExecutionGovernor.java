package deism.run;

import org.apache.log4j.Logger;

/**
 * ExecutionGovernor implementation using the RealtimeClock
 * 
 * RealtimeExecutionGovernor implements a mechanism to delay events until the
 * timestamp of an event is reached by means of a RealtimeClock instance.
 */
public class RealtimeExecutionGovernor implements ExecutionGovernor {
    private Timebase simulationTimebase;
    private Timebase systemTimebase;
    private SystemTimeProxy systemTime;
    private long wakeupTime;
    private final static Logger logger = Logger
            .getLogger(RealtimeExecutionGovernor.class);

    public RealtimeExecutionGovernor(double scale) {
        simulationTimebase = new Timebase(scale);
        systemTimebase = new Timebase();
        systemTime = new SystemTimeProxy();
    }

    @Override
    public synchronized void start(long simtime) {
        simulationTimebase.setTimebase(simtime);
        long systime = systemTime.get();
        systemTimebase.setTimebase(systime);
        logger.debug("Governor start at simtime " + simtime + " systime "
                + systime);
    }

    @Override
    public synchronized void stop(long simtime) {
        logger.debug("Governor stop");
    }

    @Override
    public void join() {
        // intentionally left empty
    }

    /**
     * Delays execution of current thread until something calls resume.
     */
    @Override
    public synchronized long suspend() {
        wakeupTime = Long.MAX_VALUE;

        try {
            logger.debug("Wait indefinitely");
            this.wait();
        }
        catch (InterruptedException ex) {
            logger.debug(ex);
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
     * Pleaso note: the timestamp returned by this method does not necessarily
     * lie in the future. It is fairly possible that some EventSource woke up
     * the governor using {@link #resume(long wakeupTime)} with a timestamp
     * which is smaller than the one we're waiting for here.
     * 
     * @return the timestamp reached representing the new local virtual time
     */
    @Override
    public synchronized long suspendUntil(long simtime) {
        wakeupTime = simtime;

        long delay =
                simulationTimebase.convert(wakeupTime, systemTimebase)
                        - systemTime.get();
        try {
            if (delay > 0) {
                logger.debug("Wait for " + delay + " milliseconds");
                this.wait(delay);
            }
        }
        catch (InterruptedException ex) {
            logger.debug(ex);
            // ignored intentionally
        }

        return wakeupTime;
    }

    @Override
    public synchronized void resume() {
        this.wakeupTime = Math.min(this.wakeupTime, getCurrentSimtime());
        logger.debug("Resume at " + this.wakeupTime);
        this.notify();
    }

    @Override
    public synchronized void resume(long wakeupTime) {
        this.wakeupTime = Math.min(this.wakeupTime, wakeupTime);
        this.wakeupTime = Math.min(this.wakeupTime, getCurrentSimtime());
        logger.debug("Resume at " + this.wakeupTime
                + " with given maximum wakeup time " + wakeupTime);
        this.notify();
    }

    @Override
    public synchronized long getCurrentSimtime() {
        return systemTimebase.convert(systemTime.get(), simulationTimebase);
    }
}
