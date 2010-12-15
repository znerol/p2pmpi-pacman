package deism.core;

/**
 * Interface for EventDispatcher classes
 * 
 * An EventDispatcher is responsible to match and deliver events to event
 * handlers.
 */
public interface EventDispatcher {
    void dispatchEvent(Event e);
}
