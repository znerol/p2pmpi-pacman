package deism.core;

/**
 * Adapter class which turns a StatefulEventGenerator into an EventSource
 */
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
        assert(event == currentEvent);
        currentEvent = null;
    }

    @Override
    public void start(long startSimtime) {
    }

    @Override
    public void stop() {
    }
}
