package deism.core;

/**
 * Adapter class which turns a StatelessEventGenerator into an EventSource.
 */
public class EventSourceStatelessGeneratorAdapter implements EventSource {
    private final StatelessEventGenerator generator;

    public EventSourceStatelessGeneratorAdapter(
            StatelessEventGenerator generator) {
        this.generator = generator;
    }

    @Override
    public Event peek(long currentSimtime) {
        return generator.peek(currentSimtime);
    }

    @Override
    public void remove(Event event) {
    }

    @Override
    public void start(long startSimtime) {
    }

    @Override
    public void stop() {
    }
}
