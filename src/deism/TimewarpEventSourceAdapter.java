package deism;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class TimewarpEventSourceAdapter
        extends AbstractStateHistory<Long, Event>
        implements TimewarpEventSource {

    private final Queue<Event> pending = new PriorityQueue<Event>();
    private final EventSource source;
    
    public TimewarpEventSourceAdapter(EventSource orig) {
        source = orig;
    }
    
    @Override
    public void compute(long currentSimtime) {
        // only call compute on source when there are no pending events
        if (!pendingEventsAvailable()) {
            source.compute(currentSimtime);
        }
    }

    @Override
    public Event peek() {
        Event event;
        
        if (pendingEventsAvailable()) {
            event = pending.peek();
        }
        else {
            event = source.peek();
        }
        
        return event;
    }

    @Override
    public Event poll() {
        Event event;
        
        if (pendingEventsAvailable()) {
            event = pending.poll();
        }
        else {
            event = source.poll();
        }
        
        addToHistory(event);
        return event;
    }

    @Override
    public void offer(Event event) {
        removeFromHistory(event);
        this.pending.offer(event);
    }

    @Override
    public void addPending(List<Event> pending) {
        this.pending.addAll(pending);
    }

    public boolean pendingEventsAvailable() {
        return (pending.size() > 0);
    }
}
