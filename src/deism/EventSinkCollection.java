package deism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventSinkCollection implements EventSink {
    private Iterable<EventSink> eventSinks;
    private List<EventSink> currentSinks = new ArrayList<EventSink>();

    public EventSinkCollection(Iterable<EventSink> eventSinks) {
        this.eventSinks = eventSinks;
    }

    public EventSinkCollection(EventSink[] eventSinks) {
        this(Arrays.asList(eventSinks));
    }

    @Override
    public boolean offer(Event event) {
        assert(event != null);
        assert(currentSinks.size() == 0);

        boolean result = false;
        for (EventSink sink : eventSinks) {
            boolean remember = sink.offer(event);
            if (remember) {
                currentSinks.add(sink);
                result = true;
            }
        }

        return result;
    }

    @Override
    public void remove(Event event) {
        assert(currentSinks.size() > 0);

        for (EventSink sink : currentSinks) {
            sink.remove(event);
        }

        currentSinks.clear();
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
