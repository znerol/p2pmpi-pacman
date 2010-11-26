package deism;

import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Special EventSource aggregating the events from multiple EventSources
 */
public class EventSourceCollection implements EventSource {
    private Iterable<EventSource> eventSources;
    private EventSource currentSource;

    public EventSourceCollection(Iterable<EventSource> eventSources) {
        this.eventSources = eventSources;
    }

    public EventSourceCollection(EventSource[] eventSources) {
        this(Arrays.asList(eventSources));
    }
    
    @Override
    public synchronized void compute(long currentSimtime) {
        for (EventSource source : eventSources) {
            source.compute(currentSimtime);
        }
    }
    
    @Override
    public synchronized Event receive() {
        SortedMap<Event, EventSource> eventsAndSources =
            new TreeMap<Event, EventSource>();
        
        currentSource = null;
        
        for (EventSource source : eventSources) {
            Event event = source.receive();
            if (event != null) {
                eventsAndSources.put(event, source);
            }
        }

        Event result = null;
        Iterator<Event> it = eventsAndSources.keySet().iterator();
        if (it.hasNext()) {
            result = it.next();
            currentSource = eventsAndSources.get(result);
            while (it.hasNext()) {
                Event event = it.next();
                eventsAndSources.get(event).reject(event);
            }
        }
        
        return result;
    }

    @Override
    public synchronized void reject(Event event) {
        assert(currentSource != null);
        currentSource.reject(event);
    }
}
