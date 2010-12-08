package deism;

import java.util.Queue;

/**
 * A Queue specifically designed to buffer Events. Beside basic Collection and
 * Queue operations, the EventQueue implementations ensure that Events
 * annihilate with corresponding antimessages when added.
 *
 * Implementations of EventQueue must pass EventQueueTest.
 */
public interface EventQueue extends Queue<Event> {
}
