package deism;

import java.util.ArrayList;
import java.util.PriorityQueue;

import org.junit.Test;
import static org.junit.Assert.*;

public class EventSourceCollectionTest {	
	/**
	 * EventSourceCollection.peek and EventSourceCollection.poll must return
	 * null if list of EventSources is empty.
	 */
	@Test
	public void collectionWithoutSources() {
		final ArrayList<EventSource> sources = new ArrayList<EventSource>();
		EventSourceCollection c = new EventSourceCollection(sources);
		
		Event peek = c.peek();
		assertNull(peek);
		
		Event poll = c.poll();
		assertNull(poll);
	}
	
	/**
	 * EventSourceCollection.peek and EventSourceCollection.poll must return
	 * events in ascending timestamp-order from any source.
	 */
	@Test
	public void collectionPeekPolltWithTwoSources() {
		final Event one = new Event(0);
		final Event two = new Event(1);
		final Event three = new Event(2);
		final Event four = new Event(3);
		
		/* construct first event queue */
		final PriorityQueue<Event> firstSourceEvents =
			new PriorityQueue<Event>();
		firstSourceEvents.add(one);
		firstSourceEvents.add(two);
		firstSourceEvents.add(four);
		
		final EventSource firstSource =
			new QueueEventSource(firstSourceEvents);
		
		/* construct second event queue */
		final PriorityQueue<Event> secondSourceEvents =
			new PriorityQueue<Event>();
		secondSourceEvents.add(three);
		
		final EventSource secondSource =
			new QueueEventSource(secondSourceEvents);
		
		/* construct sources list */
		final ArrayList<EventSource> sources = new ArrayList<EventSource>();
		sources.add(firstSource);
		sources.add(secondSource);
		
		/* test EventSourceController */
		EventSourceCollection c = new EventSourceCollection(sources);
		
		/* verify that events are returned in the expected order */
		assertEquals(one, c.poll());
		assertEquals(two, c.poll());
		assertEquals(three, c.poll());
		assertEquals(four, c.poll());
		assertEquals(null, c.poll());
	}
}
