package deism.stateful;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import deism.core.Event;
import deism.core.EventSource;

/**
 * Adapter class for simple {@link deism.core.Stateful} {@link EventSource}
 * classes which do not implement {@link StateHistory} on their own.
 * 
 * TimewarpEventSourceAdapter specifically is responsible for:
 * <ul>
 * <li>annihilating pending events and matching anti-events</li>
 * <li>ensuring that events delivered to the runloop via {@link #peek(long)} get
 * delivered again after a rollback and the following replay of the simulation</li>
 * </ul>
 */
public class TimewarpEventSourceAdapter extends
        AbstractStateHistory<Long, Event> implements EventSource {

    private final Queue<Event> pending = new PriorityQueue<Event>();
    private final Queue<Event> pendingAnti = new PriorityQueue<Event>();
    private final EventSource source;

    public TimewarpEventSourceAdapter(EventSource orig) {
        source = orig;
    }

    @Override
    public Event peek(long currentSimtime) {
        Event event;

        while ((event = source.peek(currentSimtime)) != null) {
            Event inverseEvent = event.inverseEvent();
            if (event.isAntimessage()) {
                // This event is an antimessage. If it matches a message in the
                // pending queue the two annihilate. Otherwise the antimessage
                // is added to the antimessage pending queue for later
                // processing.
                if (!pending.remove(inverseEvent)) {
                    pendingAnti.offer(event);
                }
                source.remove(event);
            }
            else if (pendingAnti.remove(inverseEvent)) {
                // Just silently absorb the event if we have a matching
                // antimessage pending.
                source.remove(event);
            }
            else if (pending.peek() == null) {
                // If the pending queue is empty we just put the event into
                // the queue.
                pending.offer(event);
                source.remove(event);
            }
            else if (event.compareTo(pending.peek()) < 0) {
                // If the pending queue already contains events, we only
                // want to add this event to the queue if it makes it is newer
                // than peek.
                pending.offer(event);
                source.remove(event);
            }
            else {
                break;
            }
        }

        Event pendingPeek = pending.peek();
        assert (pendingPeek == null || pendingPeek.isAntimessage() == false);
        Event pendingAntiPeek = pendingAnti.peek();
        assert (pendingAntiPeek == null || pendingAntiPeek.isAntimessage() == true);
        Event result = null;

        if (pendingPeek != null && pendingAntiPeek != null) {
            result =
                    pendingPeek.compareTo(pendingAntiPeek) >= 0 ? pendingAntiPeek
                            : pendingPeek;
        }
        else if (pendingPeek != null) {
            result = pendingPeek;
        }
        else if (pendingAntiPeek != null) {
            result = pendingAntiPeek;
        }

        return result;
    }

    @Override
    public void remove(Event event) {
        boolean result;
        if (event.isAntimessage()) {
            result = pendingAnti.remove(event);
            assert (result);
        }
        else {
            result = pending.remove(event);
            assert (result);
        }
        pushHistory(event);
    }

    @Override
    public void revertHistory(List<Event> tail) {
        for (Event event : tail) {
            Event inverseEvent = event.inverseEvent();
            if (event.isAntimessage()) {
                if (!pending.remove(inverseEvent)) {
                    pendingAnti.add(event);
                }
            }
            else {
                if (!pendingAnti.remove(inverseEvent)) {
                    pending.add(event);
                }
            }
        }
    }
}
