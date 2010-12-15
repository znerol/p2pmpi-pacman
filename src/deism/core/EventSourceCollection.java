package deism.core;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Special EventSource aggregating the events from multiple EventSources
 */
public class EventSourceCollection implements EventSource {
    protected Iterable<EventSource> eventSources;
    private EventSource currentSource;

    public EventSourceCollection(Iterable<EventSource> eventSources) {
        this.eventSources = eventSources;
    }

    public EventSourceCollection(EventSource[] eventSources) {
        this(Arrays.asList(eventSources));
    }

    @Override
    public Event peek(long currentSimtime) {
        Map<EventSource, Event> candidates =
            new LinkedHashMap<EventSource, Event>();
        Event result = null;

        // poll all event sources and knock up the candidates map.
        for (EventSource source : eventSources) {
            Event candidateEvent = source.peek(currentSimtime);
            if (candidateEvent != null) {
                candidates.put(source, candidateEvent);
            }
        }

        // find the event with the least timestamp
        currentSource = null;
        for (EventSource source : candidates.keySet()) {
            Event candidateEvent = candidates.get(source);
            if (result == null || candidateEvent.compareTo(result) < 0) {
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
