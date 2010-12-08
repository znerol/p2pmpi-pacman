package deism;

import java.util.ArrayDeque;

/**
 * An EventQueue implementation based on ArrayDeque
 */
public class EventArrayDeque<T extends Event>
        extends ArrayDeque<T> implements EventQueue<T> {
    /**
     * Generated serial version UID to satisfy Serializable
     */
    private static final long serialVersionUID = -4563103082381595730L;

    @Override
    public void addFirst(T event) {
        Event inverseEvent = event.inverseEvent();
        boolean inverseExisted = remove(inverseEvent);

        if (!inverseExisted) {
            super.addFirst(event);
        }
    }

    @Override
    public void addLast(T event) {
        Event inverseEvent = event.inverseEvent();
        boolean inverseExisted = remove(inverseEvent);

        if (!inverseExisted) {
            super.addLast(event);
        }
    }
}
