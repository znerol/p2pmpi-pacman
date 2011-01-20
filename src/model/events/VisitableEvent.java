package model.events;

import deism.core.Event;

/**
 * Marks event as visitable by a {@link model.events.EventVisitor}
 */
@SuppressWarnings("serial")
public abstract class VisitableEvent extends Event {
    public VisitableEvent(long simtime) {
        super(simtime);
    }

    public VisitableEvent(long simtime, boolean antimessage) {
        super(simtime, antimessage);
    }

    /**
     * Accepts event visitor
     * 
     * @param visitor
     *            visitor to accept
     */
    public abstract void accept(EventVisitor visitor);
}
