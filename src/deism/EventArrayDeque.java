package deism;

import java.util.ArrayDeque;

/**
 * An EventQueue implementation based on ArrayDeque
 */
public class EventArrayDeque extends ArrayDeque<Event> implements EventQueue {
    /**
     * Generated serial version UID to satisfy Serializable
     */
    private static final long serialVersionUID = -4563103082381595730L;

    @Override
    public void addFirst(Event event) {
        Event inverseEvent = event.inverseEvent();
        boolean inverseExisted = remove(inverseEvent);

        if (!inverseExisted) {
            super.addFirst(event);
        }
    }

    @Override
    public void addLast(Event event) {
        Event inverseEvent = event.inverseEvent();
        boolean inverseExisted = remove(inverseEvent);

        if (!inverseExisted) {
            super.addLast(event);
        }
    }
}
