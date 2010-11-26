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
    public Event receive(long currentSimtime) {
        Event event;
        
        if (pending.size() > 0) {
            event = pending.poll();
        }
        else {
            event = source.receive(currentSimtime);
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
}
