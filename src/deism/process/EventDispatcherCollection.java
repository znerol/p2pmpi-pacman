package deism.process;

import java.util.Arrays;

import deism.core.Event;
import deism.core.EventDispatcher;

public class EventDispatcherCollection implements EventDispatcher {
    private final Iterable<EventDispatcher> dispatchers;
    
    public EventDispatcherCollection(Iterable<EventDispatcher> dispatchers) {
        this.dispatchers = dispatchers;
    }
    
    public EventDispatcherCollection(EventDispatcher[] dispatchers) {
        this(Arrays.asList(dispatchers));
    }
    
    @Override
    public void dispatchEvent(Event event) {
        for (EventDispatcher dispatcher : dispatchers) {
            dispatcher.dispatchEvent(event);
        }
    }
}
