package model.events;

/**
 * Visitor for events. Every sprite state has to implement this visitor and on
 * dispatch, the event will change the sprite's state.
 */
public interface EventVisitor {
    /**
     * Visits {@link model.events.DirectionEvent}
     * 
     * @param event
     *            Current event
     */
    public void visit(DirectionEvent event);

    /**
     * Visits {@link model.events.CollisionEvent}
     * 
     * @param event
     *            Current event
     */
    public void visit(CollisionEvent event);

    /**
     * Visits {@link model.events.ChangeViewEvent}
     * 
     * @param event
     *            Current event
     */
    public void visit(ChangeViewEvent event);

    /**
     * Visits {@link model.events.EnterJunctionEvent}
     * 
     * @param event
     *            Current event
     */
    public void visit(EnterJunctionEvent event);
}
