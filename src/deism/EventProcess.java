package deism;

public interface EventProcess {
    /**
     * Return the event source of this process
     */
    public EventSource getEventSource();
    
    /**
     * Return the event dispatcher of this process
     */
    public EventDispatcher getEventDispatcher();
}
