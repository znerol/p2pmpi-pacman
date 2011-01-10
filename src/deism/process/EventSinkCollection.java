package deism.process;

import java.util.Arrays;

import deism.core.Event;
import deism.core.EventSink;

public class EventSinkCollection implements EventSink {
    private Iterable<EventSink> eventSinks;

    public EventSinkCollection(Iterable<EventSink> eventSinks) {
        this.eventSinks = eventSinks;
    }

    public EventSinkCollection(EventSink[] eventSinks) {
        this(Arrays.asList(eventSinks));
    }

    @Override
    public void offer(Event event) {
        assert(event != null);

        for (EventSink sink : eventSinks) {
            sink.offer(event);
        }
    }
}
