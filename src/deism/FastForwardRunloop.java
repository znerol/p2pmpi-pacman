package deism;

/**
 * Simple realtime implementation of EventLoop
 * 
 * This EventRunloop implementation respects the simtime property of the events
 * supplied by the event source such that if an event for a future point in time
 * is received, execution is delayed adequately.
 * 
 * FastForwardRunloop verifies that events delivered by the event source arrive
 * in the proper order i.e. with increasing timestamps. Otherwise an
 * EventSourceOrderException is thrown.
 */
public class FastForwardRunloop implements EventRunloop {
    private boolean stop = false;
    private EventCondition terminationCondition = null;
    private ExecutionGovernor governor;
    private long currentSimtime = 0;
    private EventRunloopRecoveryStrategy recoveryStrategy;
    private EventCondition snapshotCondition;

    public FastForwardRunloop(ExecutionGovernor governor,
            EventCondition terminationCondition,
            EventRunloopRecoveryStrategy recoveryStrategy,
            EventCondition snapshotCondition) {
        this.governor = governor;
        this.terminationCondition = terminationCondition;
        this.recoveryStrategy = recoveryStrategy;
        this.snapshotCondition = snapshotCondition;
    }

    /**
     * Wait for Event timestamp
     * 
     * Suspend execution until the events timestamp has been reached. if the
     * events timestamp lies in the past, the method returns immediately.
     * Suspend indefinitely if event is null.
     * 
     * Use wakeup to resume before the timeout is reached.
     * 
     * @param e
     * @throws InterruptedException
     */

    @Override
    public void run(EventSource source, EventDispatcher disp)
            throws EventSourceOrderException {

        long lastSimtime = currentSimtime;
        
        // support rollback to before very first event
        recoveryStrategy.save(currentSimtime - 1);
        
        while (!stop) {
            Event peekEvent = source.receive(currentSimtime);

            if (terminationCondition.match(peekEvent)) {
                break;
            }

            /*
             * Suspend execution until its time to handle the event.
             */
            long newSimtime;
            if (peekEvent != null) {
                newSimtime = governor.suspendUntil(peekEvent.getSimtime());
            }
            else {
                newSimtime = governor.suspend();
            }

            currentSimtime = newSimtime;

            if (peekEvent == null) {
                continue;
            }
            
            if (newSimtime < peekEvent.getSimtime()) {
                // Restart and reevaluate loop conditions and current event
                // when the current simulation time is less than that of the
                // next event.
                source.reject(peekEvent);
                continue;
            }

            if (currentSimtime < lastSimtime) {
                source.reject(peekEvent);
                recoveryStrategy.rollback(currentSimtime);
                lastSimtime = currentSimtime;
                continue;
            }

            disp.dispatchEvent(peekEvent);
            
            if (snapshotCondition.match(peekEvent)) {
                recoveryStrategy.save(currentSimtime);
            }
            
            lastSimtime = currentSimtime;
        }
    }

    @Override
    public void wakeup() {
        governor.resume();
    }

    @Override
    public void stop() {
        stop = true;
        wakeup();
    }
}
