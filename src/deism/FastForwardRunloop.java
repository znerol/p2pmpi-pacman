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
    private EventMatcher terminationCondition = null;
    private ExecutionGovernor governor;
    private long currentSimtime = 0;
    private EventRunloopRecoveryStrategy recoveryStrategy;

    public FastForwardRunloop(ExecutionGovernor governor,
            EventMatcher terminationCondition,
            EventRunloopRecoveryStrategy recoveryStrategy) {
        this.governor = governor;
        this.terminationCondition = terminationCondition;
        this.recoveryStrategy = recoveryStrategy;
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

        while (!stop) {
            source.compute(currentSimtime);
            
            Event peekEvent = source.peek();

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

            if (peekEvent == null || newSimtime < peekEvent.getSimtime()) {
                // Restart and reevaluate loop conditions and current event
                // when the current simulation time is less than that of the
                // next event.
                continue;
            }

            if (recoveryStrategy.shouldRollback(peekEvent)) {
                recoveryStrategy.rollback(currentSimtime);
                continue;
            }

            /*
             * This is moderately ugly. We have to remove the peek event and we
             * really want to be sure that this was actually the same like the
             * one we peeked before. Otherwise it could indicate a bug in the
             * EventSource or some concurrency issue.
             */
            Event polledEvent = source.poll();
            assert peekEvent == polledEvent;

            disp.dispatchEvent(polledEvent);
            
            if (recoveryStrategy.shouldSave(polledEvent)) {
                recoveryStrategy.save(currentSimtime);
            }
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
