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
        if (pendingEventsAvailable()) {
            return pending.peek();
        }
        else {
            return source.peek();
        }
    }

    @Override
    public Event poll() {
        if (pendingEventsAvailable()) {
            return pending.poll();
        }
        else {
            return source.poll();
        }
    }

    @Override
    public void addPending(List<Event> pending) {
        this.pending.addAll(pending);
    }

    @Override
    public boolean pendingEventsAvailable() {
        return (pending.size() > 0);
    }
}
