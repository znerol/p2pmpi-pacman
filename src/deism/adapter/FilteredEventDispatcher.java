package deism.adapter;

import deism.core.Event;
import deism.core.EventCondition;
import deism.core.EventDispatcher;

/**
 * Adapter class which passes only the events matching a given {@link
 * deism.core.EventCondition} to the adapted {@link deism.core.EventDispatcher}.
 */
public class FilteredEventDispatcher implements EventDispatcher {
    private final EventCondition condition;
    private final EventDispatcher dispatcher;
    
    public FilteredEventDispatcher(EventCondition condition, EventDispatcher dispatcher) {
        this.condition = condition;
        this.dispatcher = dispatcher;
    }

    @Override
    public void dispatchEvent(Event event) {
        if (condition.match(event)) {
            dispatcher.dispatchEvent(event);
        }
    }
}
