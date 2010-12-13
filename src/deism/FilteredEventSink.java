package deism;

public class FilteredEventSink implements EventSink {
    private final EventCondition filter;
    private final EventSink sink;

    public FilteredEventSink(EventCondition filter, EventSink sink) {
        this.filter = filter;
        this.sink = sink;
    }

    @Override
    public boolean offer(Event event) {
        boolean result = false;

        if (filter.match(event)) {
            result = sink.offer(event);
        }

        return result;
    }

    @Override
    public void remove(Event event) {
        if (filter.match(event)) {
            sink.remove(event);
        }
    }

    @Override
    public void start(long startSimtime) {
        sink.start(startSimtime);
    }

    @Override
    public void stop() {
        sink.stop();
    }
}
