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

        Event peek = c.peek(0);
        assertNull(peek);

        Event poll = c.poll(0);
        assertNull(poll);
    }
    
    @Test
    public void collectionWithoutSourcesArray() {
        final EventSource[] sources = {};
        EventSourceCollection c = new EventSourceCollection(sources);
        
        Event peek = c.peek(0);
        assertNull(peek);

        Event poll = c.poll(0);
        assertNull(poll);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void collectionWithoutAnyIterableSources() {
        new EventSourceCollection((Iterable<EventSource>)null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void collectionWithoutAnySourcesInArray() {
        new EventSourceCollection((EventSource[])null);
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
        final PriorityQueue<Event> firstSourceEvents = new PriorityQueue<Event>();
        firstSourceEvents.add(one);
        firstSourceEvents.add(two);
        firstSourceEvents.add(four);

        final EventSource firstSource = new EventSource(){
            @Override
            public Event peek(long currentSimtime) {
                return firstSourceEvents.peek();
            }
            @Override
            public Event poll(long currentSimtime) {
                return firstSourceEvents.poll();
            }
        };

        /* construct second event queue */
        final PriorityQueue<Event> secondSourceEvents = new PriorityQueue<Event>();
        secondSourceEvents.add(three);

        final EventSource secondSource = new EventSource(){
            @Override
            public Event peek(long currentSimtime) {
                return secondSourceEvents.peek();
            }
            @Override
            public Event poll(long currentSimtime) {
                return secondSourceEvents.poll();
            }
        };

        /* construct sources list */
        final ArrayList<EventSource> sources = new ArrayList<EventSource>();
        sources.add(firstSource);
        sources.add(secondSource);

        /* test EventSourceController */
        EventSourceCollection c = new EventSourceCollection(sources);

        /* verify that events are returned in the expected order */
        assertEquals(one, c.poll(0));
        assertEquals(two, c.poll(0));
        assertEquals(three, c.poll(0));
        assertEquals(four, c.poll(0));
        assertEquals(null, c.poll(0));
    }
}
