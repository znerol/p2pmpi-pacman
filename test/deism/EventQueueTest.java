package deism;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class EventQueueTest {
    private final EventQueue[] queues = {
            new EventArrayDeque(),
            new EventPriorityQueue(),
    };

    @Test
    public void testOfferOneEvent() {
        final Event event = new Event(7L);

        for (EventQueue q : queues) {
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

        for (EventQueue q : queues) {
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

        for (EventQueue q : queues) {
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

        for (EventQueue q : queues) {
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

        for (EventQueue q : queues) {
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

        for (EventQueue q : queues) {
            assertEquals(0, q.size());
            q.addAll(Arrays.asList(events));
            assertEquals(1, q.size());
            q.clear();
            assertEquals(0, q.size());
        }
    }
}
