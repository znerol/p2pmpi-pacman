package deism.run;


/**
 * EventRunloop implementations iterate thru events from an eventSource
 */
public interface EventRunloop {
    /**
     * Loops thru all events from the event source and delegates them to the
     * dispatcher.
     * 
     * @param process the DES process to run
     */
    void run(DiscreteEventProcess process);

    /**
     * Signals the EventLoop to stop processing Events and return from the run
     * method;
     */
    void stop();
}
