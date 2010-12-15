package deism.core;

public class FilteredEventSink implements EventSink {
    private final EventCondition filter;
    private final EventSink sink;

    public FilteredEventSink(EventCondition filter, EventSink sink) {
        this.filter = filter;
        this.sink = sink;
    }

    @Override
    public void offer(Event event) {
        if (filter.match(event)) {
            sink.offer(event);
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
