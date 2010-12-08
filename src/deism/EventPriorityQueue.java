package deism;

import java.util.PriorityQueue;

/**
 * An EventQueue implementation based on PriorityQueue.
 */
public class EventPriorityQueue
        extends PriorityQueue<Event> implements EventQueue {
    /**
     * Generated serial version UID to satisfy Serializable
     */
    private static final long serialVersionUID = -6256051188629725623L;

    @Override
    public boolean offer(Event event) {
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
