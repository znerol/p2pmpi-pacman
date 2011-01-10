package deism.tqgvt;

import deism.core.Event;

public class WrappedEvent extends Event {
    private static final long serialVersionUID = -3282152432695603591L;
    private final Event event;
    private final long tq;

    public WrappedEvent(Event event, long tq) {
        super(event.getSimtime());
        this.event = event;
        this.tq = tq;
    }

    public Event getEvent() {
        return event;
    }

    public long getTq() {
        return tq;
    }
}
