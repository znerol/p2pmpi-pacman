package deism.stateful;

import java.util.ArrayList;
import java.util.List;

import deism.core.EventDispatcher;
import deism.core.EventSink;
import deism.core.EventSource;
import deism.run.DefaultDiscreteEventProcess;

public class DefaultTimewarpDiscreteEventProcess extends
        DefaultDiscreteEventProcess implements TimewarpDiscreteEventProcess {
    private final List<StateHistory<Long>> statefulObjects =
        new ArrayList<StateHistory<Long>>();

    public void addStatefulObject(StateHistory<Long> statefulObject) {
        statefulObjects.add(statefulObject);
    }

    public void removeStatefulObject(StateHistory<Long> statefulObject) {
        statefulObjects.remove(statefulObject);
    }

    @Override
    public void addEventSource(EventSource source) {
        super.addEventSource(source);
        if (source instanceof TimewarpEventSource) {
            addStatefulObject((TimewarpEventSource)source);
        }
    }

    @Override
    public void removeEventSource(EventSource source) {
        super.removeEventSource(source);
        if (source instanceof TimewarpEventSource) {
            removeStatefulObject((TimewarpEventSource)source);
        }
    }

    @Override
    public void addEventSink(EventSink sink) {
        super.addEventSink(sink);
        if (sink instanceof TimewarpEventSink) {
            addStatefulObject((TimewarpEventSink)sink);
        }
    }

    @Override
    public void removeEventSink(EventSink sink) {
        super.removeEventSink(sink);
        if (sink instanceof TimewarpEventSink) {
            removeStatefulObject((TimewarpEventSink)sink);
        }
    }

    @Override
    public void addEventDispatcher(EventDispatcher dispatcher) {
        super.addEventDispatcher(dispatcher);
        if (dispatcher instanceof TimewarpEventDispatcher) {
            addStatefulObject((TimewarpEventDispatcher)dispatcher);
        }
    }

    @Override
    public void removeEventDispatcher(EventDispatcher dispatcher) {
        super.removeEventDispatcher(dispatcher);
        if (dispatcher instanceof TimewarpEventDispatcher) {
            removeStatefulObject((TimewarpEventDispatcher)dispatcher);
        }
    }

    @Override
    public void save(Long key) throws StateHistoryException {
        for (StateHistory<Long> stateObject : statefulObjects) {
            stateObject.save(key);
        }
    }

    @Override
    public void commit(Long key) throws StateHistoryException {
        for (StateHistory<Long> stateObject : statefulObjects) {
            stateObject.commit(key);
        }
    }

    @Override
    public void rollback(Long key) throws StateHistoryException {
        for (StateHistory<Long> stateObject : statefulObjects) {
            stateObject.rollback(key);
        }
    }
}
