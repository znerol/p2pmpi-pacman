package model.events;

import deism.core.Event;

@SuppressWarnings("serial")
public abstract class VisitableEvent extends Event {
    public VisitableEvent(long simtime) {
        super(simtime);
    }
    public VisitableEvent(long simtime, boolean antimessage) {
        super(simtime, antimessage);
    }

    public abstract void accept(EventVisitor visitor);
}
