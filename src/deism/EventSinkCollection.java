package deism;

import java.util.Arrays;

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

    @Override
    public void start(long startSimtime) {
        for (EventSink sink : eventSinks) {
            sink.start(startSimtime);
        }
    }

    @Override
    public void stop() {
        for (EventSink sink : eventSinks) {
            sink.stop();
        }
    }
}
