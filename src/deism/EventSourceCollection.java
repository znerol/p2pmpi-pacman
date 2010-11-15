package deism;

import java.util.Arrays;

/**
 * Special EventSource aggregating the events from multiple EventSources
 */
public class EventSourceCollection implements EventSource {
    private Iterable<EventSource> eventSources;

    public EventSourceCollection(Iterable<EventSource> eventSources) {
        if (eventSources == null) {
            throw new IllegalArgumentException(
                    "EventSourceCollection cannot operate without a list of event sources");
        }
        this.eventSources = eventSources;
    }

    public EventSourceCollection(EventSource[] eventSources) {
        if (eventSources == null) {
            throw new IllegalArgumentException(
                    "EventSourceCollection cannot operate without a list of event sources");
        }
        this.eventSources = Arrays.asList(eventSources);
    }

    @Override
    public Event peek() {
        return this.getPeekEventAndSource().getEvent();
    }

    @Override
    public Event poll() {
        EventAndSource eas = this.getPeekEventAndSource();
        EventSource es = eas.getEventSource();
        if (es == null) {
            return null;
        }

        return es.poll();
    }

    /**
     * Find event with the smallest timestamp and return it together with its
     * event source.
     * 
     * @return event and event source
     */
    private EventAndSource getPeekEventAndSource() {
        Event peekEvent = null;
        EventSource peekSource = null;

        for (EventSource s : eventSources) {
            Event candidate = s.peek();
            if (candidate == null) {
                continue;
            }

            if (peekEvent == null || candidate.compareTo(peekEvent) < 0) {
                peekEvent = candidate;
                peekSource = s;
            }
        }

        return new EventAndSource(peekEvent, peekSource);
    }

    /**
     * Private helper class encapsulating an event and the corresponding source
     */
    private class EventAndSource {
        private final Event event;
        private final EventSource eventSource;

        public EventAndSource(Event e, EventSource s) {
            event = e;
            eventSource = s;
        }

        public Event getEvent() {
            return event;
        }

        public EventSource getEventSource() {
            return eventSource;
        }
    }
}
