package deism;

import java.util.Arrays;

public class TimewarpEventSourceCollection
        extends EventSourceCollection
        implements TimewarpEventSource {

    public TimewarpEventSourceCollection(Iterable<EventSource> eventSources) {
        super(eventSources);
        for (EventSource source : eventSources) {
            assert(source instanceof TimewarpEventSource);
        }
    }

    public TimewarpEventSourceCollection(EventSource[] eventSources) {
        this(Arrays.asList(eventSources));
    }

    @Override
    public void save(Long key) throws StateHistoryException {
        for (EventSource source : eventSources) {
            ((TimewarpEventSource)source).save(key);
        }
    }

    @Override
    public void commit(Long key) throws StateHistoryException {
        for (EventSource source : eventSources) {
            ((TimewarpEventSource)source).commit(key);
        }
    }

    @Override
    public void rollback(Long key) throws StateHistoryException {
        for (EventSource source : eventSources) {
            ((TimewarpEventSource)source).rollback(key);
        }
    }
}
