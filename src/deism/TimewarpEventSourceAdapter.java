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
        while (pending.peek() == null && source.peek(currentSimtime) != null) {
            Event event = null;

            // poll antimessages until we get a normal event or null
            for (event = source.peek(currentSimtime);
                    event != null && event.isAntimessage();
                    event = source.peek(currentSimtime)) {
                source.remove(event);
                pending.offer(event);
            }

            lastEventFromSource = event;
            if (lastEventFromSource != null) {
                Event inverseEvent = event.inverseEvent();
                if (pending.contains(inverseEvent)) {
                    pending.remove(inverseEvent);
                    source.remove(inverseEvent);
                    lastEventFromSource = null;
                }
                else {
                    pending.offer(lastEventFromSource);
                }
            }
        }

        Event result = pending.peek();
        // An EventSource may not emit antimessages when no corresponding Event
        // was emitted before.
        if (result != null && result.isAntimessage()
                && !containedInHistory(result.inverseEvent())) {
            result = null;
        }

        return result;
    }

    @Override
    public void remove(Event event) {
        if (event == lastEventFromSource) {
            source.remove(event);
            lastEventFromSource = null;
        }
        assert(lastEventFromSource == null);

        pending.remove(event);
        pushHistory(event);
    }

    @Override
    public void revertHistory(List<Event> tail) {
        this.pending.addAll(tail);
    }
}
