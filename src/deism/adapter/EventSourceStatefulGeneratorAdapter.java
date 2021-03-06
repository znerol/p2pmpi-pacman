package deism.adapter;

import deism.core.Event;
import deism.core.EventSource;
import deism.core.Stateful;
import deism.core.StatefulEventGenerator;

/**
 * Adapter class which turns a {@link deism.core.StatefulEventGenerator} into an
 * {@link deism.core.EventSource}.
 */
@Stateful
public class EventSourceStatefulGeneratorAdapter implements EventSource {
    private final StatefulEventGenerator generator;
    private Event currentEvent;

    public EventSourceStatefulGeneratorAdapter(StatefulEventGenerator generator) {
        this.generator = generator;
    }

    @Override
    public Event peek(long currentSimtime) {
        if (currentEvent == null) {
            currentEvent = generator.poll();
        }

        return currentEvent;
    }

    @Override
    public void remove(Event event) {
        assert (event == currentEvent);
        currentEvent = null;
    }
}
