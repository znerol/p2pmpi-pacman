package deism.adapter;

import deism.core.Event;
import deism.core.EventCondition;
import deism.core.EventSink;

/**
 * Adapter class which offers only the events matching a given {@link
 * deism.core.EventCondition} to the adapted {@link deism.core.EventSource}.
 */
public class FilteredEventSink implements EventSink{
    private final EventCondition filter;
    private final EventSink sink;

    public FilteredEventSink(EventCondition filter, EventSink sink) {
        this.filter = filter;
        this.sink = sink;
    }

    @Override
    public void offer(Event event) {
        if (filter.match(event)) {
            sink.offer(event);
        }
    }
}
