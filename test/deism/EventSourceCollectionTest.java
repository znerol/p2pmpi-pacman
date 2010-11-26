package deism;

import java.util.ArrayList;
import java.util.PriorityQueue;

import org.junit.Test;
import static org.junit.Assert.*;

public class EventSourceCollectionTest {
    /**
     * EventSourceCollection.receive must return null if list of EventSources
     * is empty.
     */
    @Test
    public void collectionWithoutSources() {
        final ArrayList<EventSource> sources = new ArrayList<EventSource>();
        EventSourceCollection c = new EventSourceCollection(sources);

        Event event = c.receive(0);
        assertNull(event);
    }
    
    @Test
    public void collectionWithoutSourcesArray() {
        final EventSource[] sources = {};
        EventSourceCollection c = new EventSourceCollection(sources);
        
        Event event = c.receive(0);
        assertNull(event);
    }
    
    /**
     * EventSourceCollection.peek and EventSourceCollection.receive must return
     * events in ascending timestamp-order from any source.
     */
    @Test
    public void collectionReceiveWithTwoSources() {
        final Event one = new Event(1);
        final Event two = new Event(2);
        final Event three = new Event(3);
        final Event four = new Event(4);

        /* construct first event queue */
        final PriorityQueue<Event> firstSourceEvents = new PriorityQueue<Event>();
        firstSourceEvents.add(one);
        firstSourceEvents.add(two);
        firstSourceEvents.add(four);

        final EventSource firstSource = new EventSource(){
            @Override
            public Event receive(long currentSimtime) {
                return firstSourceEvents.poll();
            }
            @Override
            public void reject(Event event) {
                firstSourceEvents.offer(event);
            }
        };

        /* construct second event queue */
        final PriorityQueue<Event> secondSourceEvents = new PriorityQueue<Event>();
        secondSourceEvents.add(three);

        final EventSource secondSource = new EventSource(){
            @Override
            public Event receive(long currentSimtime) {
                return secondSourceEvents.poll();
            }
            @Override
            public void reject(Event event) {
                secondSourceEvents.offer(event);
            }
        };

        /* construct sources list */
        final ArrayList<EventSource> sources = new ArrayList<EventSource>();
        sources.add(firstSource);
        sources.add(secondSource);

        /* test EventSourceController */
        EventSourceCollection c = new EventSourceCollection(sources);

        /* verify that events are returned in the expected order */
        assertEquals(one, c.receive(0));
        assertEquals(two, c.receive(1));
        assertEquals(three, c.receive(2));
        assertEquals(four, c.receive(3));
        assertEquals(null, c.receive(4));
    }
}
