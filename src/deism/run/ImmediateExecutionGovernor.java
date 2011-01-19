package deism.run;

/**
 * FastForward implementation of ExecutionGovernor
 * 
 * This implementation of the ExecutionGovernor interface will result in
 * immediate delivery of the events in a runloop. Use this class to e.g. replay
 * recorded event streams.
 */
public class ImmediateExecutionGovernor implements ExecutionGovernor {
    private long currentSimtime;

    @Override
    public void start(long timestamp) {
    }

    @Override
    public void stop(long timestamp) {
    }

    @Override
    public long suspend() {
        currentSimtime = Long.MAX_VALUE;
        return getCurrentSimtime();
    }
    
    @Override
    public long suspendUntil(long simtime) {
        currentSimtime = simtime;
        return getCurrentSimtime();
    }

    @Override
    public void resume() {
        /* Intentionally left empty */
    }
    
    @Override
    public void resume(long wakeupTime) {
        /* Intentionally left empty */
    }

    @Override
    public void join() {
        // intentionally left empty
    }

    @Override
    public long getCurrentSimtime() {
        return currentSimtime;
    }
}
