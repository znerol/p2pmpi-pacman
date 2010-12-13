package deism;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class TimewarpEventSinkAdapter extends AbstractStateHistory<Long, Event>
        implements EventSink {
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

        flush(event.getSimtime());
    }

    private void flush(long simtime) {
        for (Iterator<Event> i = pending.iterator(); i.hasNext(); ) {
            Event event = i.next();
            if (pendingAnti.remove(event.inverseEvent())) {
                i.remove();
            }
            else if (event.getSimtime() <= simtime) {
                out.offer(event);
                i.remove();
            }
        }

        for (Iterator<Event> i = pendingAnti.iterator(); i.hasNext(); ) {
            Event event = i.next();
            if (event.getSimtime() <= simtime) {
                out.offer(event);
                i.remove();
            }
        }

        for (Iterator<Event> i = out.iterator(); i.hasNext(); ) {
            Event event = i.next();
            sink.offer(event);
            pushHistory(event);
            i.remove();
        }
    }

    @Override
    public void start(long startSimtime) {
        sink.start(startSimtime);
    }

    @Override
    public void stop() {
        flush(Long.MAX_VALUE);
        sink.stop();
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
