package deism;

import java.util.Queue;

/**
 * Simple EventSource using a Queue of Events
 * 
 * This implementation of EventSource is a simple wrapper around a
 * java.util.Queue.
 */
public class QueueEventSource implements EventSource {
	private Queue<Event> events;
	
	public QueueEventSource(Queue<Event> events) {
		if (events == null) {
			throw new IllegalArgumentException(
					"QueueEventSource cannot operate without a queue of events");
		}
		this.events = events;
	}
	
	@Override
	public Event peek() {
		return events.peek();
	}

	@Override
	public Event poll() {
		return events.poll();
	}
}
