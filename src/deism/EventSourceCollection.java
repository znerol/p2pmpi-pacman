package deism;

import java.util.Arrays;

/**
 * Special EventSource aggregating the events from multiple EventSources
 */
public class EventSourceCollection implements EventSource {
    private Iterable<EventSource> eventSources;
    private EventSource currentSource;

    public EventSourceCollection(Iterable<EventSource> eventSources) {
        this.eventSources = eventSources;
        currentSource = null;
    }

    public EventSourceCollection(EventSource[] eventSources) {
        this(Arrays.asList(eventSources));
    }
    
    @Override
    public void compute(long currentSimtime) {
        Event peekEvent = null;
        currentSource = null;

        for (EventSource source : eventSources) {
            source.compute(currentSimtime);
            
            Event candidate = source.peek();
            if (candidate == null) {
                continue;
            }

            if (peekEvent == null || candidate.compareTo(peekEvent) < 0) {
                peekEvent = candidate;
                currentSource = source;
            }
        }
    }
    
    @Override
    public Event peek() {
        Event e = null;
        
        if (currentSource != null) {
            e = currentSource.peek();
        }
        
        return e;
    }

    @Override
    public Event poll() {
        Event e = null;
        
        if (currentSource != null) {
            e = currentSource.poll();
        }
        
        return e;
    }

    @Override
    public void offer(Event event) {
        if (currentSource != null) {
            currentSource.offer(event);
        }
    }
}
