package deism.run;

import java.util.ArrayList;
import java.util.List;

import deism.core.Event;
import deism.core.EventDispatcher;
import deism.core.EventDispatcherCollection;
import deism.core.EventSink;
import deism.core.EventSinkCollection;
import deism.core.EventSource;
import deism.core.EventSourceCollection;

public class DefaultDiscreteEventProcess implements DiscreteEventProcess, Startable {
    private final List<EventSource> sourceList = new ArrayList<EventSource>();
    private final List<EventSink> sinkList = new ArrayList<EventSink>();
    private final List<EventDispatcher> dispatcherList =
        new ArrayList<EventDispatcher>();
    private final List<Startable> startableList = new ArrayList<Startable>();

    private final EventSourceCollection source =
        new EventSourceCollection(sourceList);
    private final EventSinkCollection sink = new EventSinkCollection(sinkList);
    private final EventDispatcherCollection dispatcher = 
        new EventDispatcherCollection(dispatcherList);

    public void addEventSource(EventSource source) {
        sourceList.add(source);
        if (source instanceof Startable) {
            addStartable((Startable)source);
        }
    }

    public void removeEventSource(EventSource source) {
        sourceList.remove(source);
        if (source instanceof Startable) {
            removeStartable((Startable)source);
        }
    }

    public void addEventSink(EventSink sink) {
        sinkList.add(sink);
        if (sink instanceof Startable) {
            addStartable((Startable)sink);
        }
    }

    public void removeEventSink(EventSink sink) {
        sinkList.remove(sink);
        if (sink instanceof Startable) {
            removeStartable((Startable)sink);
        }
    }

    public void addEventDispatcher(EventDispatcher dispatcher) {
        dispatcherList.add(dispatcher);
        if (dispatcher instanceof Startable) {
            addStartable((Startable)dispatcher);
        }
    }

    public void removeEventDispatcher(EventDispatcher dispatcher) {
        dispatcherList.remove(dispatcher);
        if (dispatcher instanceof Startable) {
            removeStartable((Startable)dispatcher);
        }
    }

    public void addStartable(Startable startable) {
        startableList.add(startable);
    }

    public void removeStartable(Startable startable) {
        startableList.remove(startable);
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

    @Override
    public void start(long simtime) {
        for (Startable startable : startableList) {
            startable.start(simtime);
        }
    }

    @Override
    public void stop(long simtime) {
        for (Startable startable : startableList) {
            startable.stop(simtime);
        }
    }
}
