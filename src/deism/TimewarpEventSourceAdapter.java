package deism;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class TimewarpEventSourceAdapter
        extends AbstractStateHistory<Long, Event>
        implements TimewarpEventSource {

    private final Queue<Event> pending = new PriorityQueue<Event>();
    private final EventSource source;
    private boolean pollOnPending;
    
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
    public Event receive() {
        Event event;
        
        pollOnPending = pendingEventsAvailable();
        if (pollOnPending) {
            event = pending.poll();
        }
        else {
            event = source.receive();
        }
        
        addToHistory(event);
        return event;
    }

    @Override
    public void reject(Event event) {
        pending.offer(event);
        removeFromHistory(event);
    }

    @Override
    public void addPending(List<Event> pending) {
        this.pending.addAll(pending);
    }

    public boolean pendingEventsAvailable() {
        return (pending.size() > 0);
    }
}
