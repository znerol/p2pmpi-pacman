package model;

/**
 * Listener interface for dispatched events. Informs other sprites that a event
 * got dispatched. All sprites that predicted a event in the future and are
 * after the dispatched event may have to update their prediction. This helps
 * the sprites to prevent multiple calculations.
 */
public interface DispatchedListener {
    /**
     * Action on event dispatching of other sprites
     * 
     * @param event
     *            dispatched event
     */
    public void eventDispatched(EventDispatchedEvent event);
}
