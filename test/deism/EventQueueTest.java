package deism;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class EventQueueTest {
    private List<EventQueue<Event>> queues = new ArrayList<EventQueue<Event>>();

    @Before
    public void setUp() {
        queues.add(new EventArrayDeque<Event>());
        queues.add(new EventPriorityQueue<Event>());
    }

    @Test
    public void testOfferOneEvent() {
        final Event event = new Event(7L);

        for (EventQueue<Event> q : queues) {
            assertEquals(0, q.size());
            q.offer(event);
            assertEquals(1, q.size());
            q.remove(event);
            assertEquals(0, q.size());
        }
    }

    @Test
    public void testOfferEventAndAntimessage() {
        final Event event = new Event(7L);
        final Event inverseEvent = event.inverseEvent();

        for (EventQueue<Event> q : queues) {
            assertEquals(0, q.size());
            q.offer(event);
            assertEquals(1, q.size());
            q.offer(inverseEvent);
            assertEquals(0, q.size());
        }
    }

    @Test
    public void testAddEvent() {
        final Event event = new Event(7L);

        for (EventQueue<Event> q : queues) {
            assertEquals(0, q.size());
            q.add(event);
            assertEquals(1, q.size());
            q.remove(event);
            assertEquals(q.toString(), 0, q.size());
        }
    }

    @Test
    public void testAddEventAndAntimessage() {
        final Event event = new Event(7L);
        final Event inverseEvent = event.inverseEvent();

        for (EventQueue<Event> q : queues) {
            assertEquals(0, q.size());
            q.add(event);
            assertEquals(1, q.size());
            q.add(inverseEvent);
            assertEquals(0, q.size());
        }
    }

    @Test
    public void testAddAll() {
        final Event[] events = {
                new Event(7L),
                new Event(8L),
        };

        for (EventQueue<Event> q : queues) {
            assertEquals(0, q.size());
            q.addAll(Arrays.asList(events));
            assertEquals(2, q.size());
            q.clear();
            assertEquals(0, q.size());
        }
    }

    @Test
    public void testAddAllOneAntimessage() {
        final Event[] events = {
                new Event(7L, true),
                new Event(8L),
                new Event(7L, false),
        };

        for (EventQueue<Event> q : queues) {
            assertEquals(0, q.size());
            q.addAll(Arrays.asList(events));
            assertEquals(1, q.size());
            q.clear();
            assertEquals(0, q.size());
        }
    }
}
