package deism;

/**
 * Implementations of execution governors must provide a way to suspend the
 * current thread until the given timestamp is reached.
 */
public interface ExecutionGovernor extends Cloneable {
    /**
     * Suspend execution until something called resume.
     * 
     * @return Simulation time reached
     */
    long suspend();
    
    /**
     * Wait until the given timestamp is reached. Return the simulation time
     * reached which must not be greater than the given timestamp.
     * @param simtime Timestamp in simulation time units
     * 
     * @return Simulation time reached
     */
    long suspendUntil(long simtime);

    /**
     * Interrupt suspend or suspendUntil method immediately.
     */
    void resume();
    
    /**
     * Interrupt suspend or suspendUntil method immediately with the given
     * simulation timestamp. The timestamp will be returned by suspend or
     * suspendUntil.
     */
    void resume(long wakeupTime);

    public Object clone() throws CloneNotSupportedException;
}