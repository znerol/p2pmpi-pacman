package deism.process;

import java.util.ArrayList;
import java.util.List;

import deism.core.Event;
import deism.core.EventDispatcher;
import deism.core.EventSink;
import deism.core.EventSource;

/**
 * A basic {@link DiscreteEventProcess} supporting multiple sources, dispatchers
 * and sinks.
 */
public class DefaultDiscreteEventProcess implements DiscreteEventProcess {
    private final List<EventSource> sourceList = new ArrayList<EventSource>();
    private final List<EventSink> sinkList = new ArrayList<EventSink>();
    private final List<EventDispatcher> dispatcherList = new ArrayList<EventDispatcher>();

    private final EventSourceCollection source = new EventSourceCollection(
            sourceList);
    private final EventSinkCollection sink = new EventSinkCollection(sinkList);
    private final EventDispatcherCollection dispatcher = new EventDispatcherCollection(
            dispatcherList);

    public void addEventSource(EventSource source) {
        sourceList.add(source);
    }

    public void addEventSink(EventSink sink) {
        sinkList.add(sink);
    }

    public void addEventDispatcher(EventDispatcher dispatcher) {
        dispatcherList.add(dispatcher);
    }

    @Override
    public Event peek(long currentSimtime) {
        return source.peek(currentSimtime);
    }

    @Override
    public void remove(Event event) {
        source.remove(event);
    }

    @Override
    public void offer(Event event) {
        sink.offer(event);
    }

    @Override
    public void dispatchEvent(Event event) {
        dispatcher.dispatchEvent(event);
    }
}
