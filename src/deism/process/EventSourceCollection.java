package deism.process;

import java.util.Arrays;

import deism.core.Event;
import deism.core.EventSource;

/**
 * Special EventSource aggregating the events from multiple EventSources
 */
public class EventSourceCollection implements EventSource {
    protected final Iterable<EventSource> eventSources;
    private EventSource currentSource;

    public EventSourceCollection(Iterable<EventSource> eventSources) {
        this.eventSources = eventSources;
    }

    public EventSourceCollection(EventSource[] eventSources) {
        this(Arrays.asList(eventSources));
    }

    @Override
    public Event peek(long currentSimtime) {
        Event result = null;

        // find the event and source with the smallest timestamp
        currentSource = null;
        for (EventSource source : eventSources) {
            Event candidateEvent = source.peek(currentSimtime);
            if (candidateEvent == null) {
                continue;
            }
            else if (result == null || candidateEvent.compareTo(result) < 0) {
                result = candidateEvent;
                currentSource = source;
            }
        }

        return result;
    }

    @Override
    public void remove(Event event) {
        assert(currentSource != null);
        currentSource.remove(event);
    }
}
