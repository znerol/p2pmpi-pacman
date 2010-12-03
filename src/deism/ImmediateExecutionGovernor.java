package deism;

/**
 * FastForward implementation of ExecutionGovernor
 * 
 * This implementation of the ExecutionGovernor interface will result in
 * immediate delivery of the events in a runloop. Use this class to e.g. replay
 * recorded event streams.
 */
public class ImmediateExecutionGovernor implements ExecutionGovernor {
    @Override
    public void start(long timestamp) {
    }

    @Override
    public void stop() {
    }

    @Override
    public long suspend() {
        return Long.MAX_VALUE;
    }
    
    @Override
    public long suspendUntil(long simtime) {
        return simtime;
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
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
