package deism.adapter;

import deism.core.Event;
import deism.core.EventSource;
import deism.core.StatelessEventGenerator;

/**
 * Adapter class which turns a StatelessEventGenerator into an EventSource.
 */
public class EventSourceStatelessGeneratorAdapter implements EventSource {
    private final StatelessEventGenerator generator;

    public EventSourceStatelessGeneratorAdapter(StatelessEventGenerator generator) {
        this.generator = generator;
    }

    @Override
    public Event peek(long currentSimtime) {
        return generator.peek(currentSimtime);
    }

    @Override
    public void remove(Event event) {
        // Left blank intentionally
    }
}
