package deism.stateful;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import deism.core.Event;
import deism.core.EventSink;
import deism.core.Flushable;

/**
 * Adapter class for simple {@link deism.core.Stateful} {@link EventSink}
 * classes which do not implement {@link StateHistory} on their own.
 * 
 * TimewarpEventSinkAdapter specifically is responsible for:
 * <ul>
 * <li>annihilating pending events and matching anti-events</li>
 * <li>ensuring that events already offered to the original sink are not offered
 * again after a rollback and the following replay of the simulation</li>
 * <li>ensuring that anti-events are offered to the original sink after a
 * rollback and the following replay of the simulation for events which don't
 * exist in the rewritten simulation history</li>
 * </ul>
 * 
 * Note: {@link #flush(long)} is only called if the simulation is actually
 * progressing ensuring that events and anti-events may be canceled after a
 * rollback and before the normal simulation path is taken again.
 */
public class TimewarpEventSinkAdapter extends AbstractStateHistory<Long, Event>
        implements EventSink, Flushable {
    private final EventSink sink;
    private final Queue<Event> pending = new ArrayDeque<Event>();
    private final Queue<Event> pendingAnti = new ArrayDeque<Event>();
    private final Queue<Event> out = new ArrayDeque<Event>();

    public TimewarpEventSinkAdapter(EventSink sink) {
        this.sink = sink;
    }

    @Override
    public void offer(Event event) {
        if (event.isAntimessage()) {
            pendingAnti.add(event);
        }
        else {
            pending.add(event);
        }
    }

    @Override
    public void flush(long simtime) {
        for (Iterator<Event> i = pending.iterator(); i.hasNext();) {
            Event event = i.next();
            if (pendingAnti.remove(event.inverseEvent())) {
                i.remove();
            }
            else if (event.getSimtime() <= simtime) {
                out.offer(event);
                i.remove();
            }
        }

        for (Iterator<Event> i = pendingAnti.iterator(); i.hasNext();) {
            Event event = i.next();
            if (event.getSimtime() <= simtime) {
                out.offer(event);
                i.remove();
            }
        }

        for (Iterator<Event> i = out.iterator(); i.hasNext();) {
            Event event = i.next();
            sink.offer(event);
            pushHistory(event);
            i.remove();
        }
    }

    @Override
    public void revertHistory(List<Event> tail) {
        for (Event event : tail) {
            if (event.isAntimessage()) {
                pending.add(event.inverseEvent());
            }
            else {
                pendingAnti.add(event.inverseEvent());
            }
        }
    }
}
