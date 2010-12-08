package deism;

import java.util.PriorityQueue;

/**
 * An EventQueue implementation based on PriorityQueue.
 */
public class EventPriorityQueue<T extends Event>
        extends PriorityQueue<T> implements EventQueue<T> {
    /**
     * Generated serial version UID to satisfy Serializable
     */
    private static final long serialVersionUID = -6256051188629725623L;

    @Override
    public boolean offer(T event) {
        Event inverseEvent = event.inverseEvent();
        boolean inverseExisted = remove(inverseEvent);

        if (inverseExisted) {
            return true;
        }
        else {
            return super.offer(event);
        }
    }
}
