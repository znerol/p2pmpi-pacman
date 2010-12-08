package deism;

import java.util.List;

public class TimewarpEventSourceAdapter
        extends AbstractStateHistory<Long, Event>
        implements TimewarpEventSource {

    private final EventQueue<Event> pending = new EventPriorityQueue<Event>();
    private final EventSource source;
    private Event lastEventFromSource;
    
    public TimewarpEventSourceAdapter(EventSource orig) {
        source = orig;
    }

    @Override
    public void start(long startSimtime) {
        source.start(startSimtime);
    }

    @Override
    public void stop() {
        source.stop();
    }
    
    @Override
    public Event peek(long currentSimtime) {
        if (pending.peek() == null) {
            lastEventFromSource = source.peek(currentSimtime);
            if (lastEventFromSource != null) {
                pending.offer(lastEventFromSource);
            }
        }

        return pending.peek();
    }

    @Override
    public void remove(Event event) {
        if (event == lastEventFromSource) {
            source.remove(event);
        }
        pending.remove(event);
        pushHistory(event);
    }

    @Override
    public void revertHistory(List<Event> tail) {
        this.pending.addAll(tail);
    }
}
