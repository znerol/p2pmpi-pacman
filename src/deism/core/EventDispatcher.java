package deism.core;

/**
 * Interface for EventDispatcher classes
 * 
 * An EventDispatcher acts upon the events. Most of the time this will change
 * the state of the simulation. An event is only dispatched if the local virtual
 * time advanced to its timestamp. An EventDispatcher will never see an
 * Anti-Event.
 */
public interface EventDispatcher {
    void dispatchEvent(Event e);
}
