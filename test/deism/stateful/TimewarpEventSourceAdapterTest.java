package deism.stateful;

import static org.junit.Assert.*;

import java.util.ArrayDeque;
import java.util.Queue;

import org.junit.Test;

import deism.core.Event;
import deism.core.EventSource;
import deism.stateful.TimewarpEventSource;
import deism.stateful.TimewarpEventSourceAdapter;

public class TimewarpEventSourceAdapterTest {
    private final Queue<Event> events = new ArrayDeque<Event>();
    private final EventSource simpleSource = new EventSource() {
        @Override
        public Event peek(long currentSimtime) {
            return events.peek();
        }

        @Override
        public void remove(Event event) {
            events.remove(event);
        }
    };
    private final TimewarpEventSource source =
        new TimewarpEventSourceAdapter(simpleSource);

    @Test
    public void testNoEvents() {
        Event result = simpleSource.peek(0);
        assertNull(result);
    }

    @Test
    public void testEventsInAscendingOrder() {
        final Event one = new Event(1);
        final Event two = new Event(2);

        events.offer(one);
        events.offer(two);

        Event event;
        event = source.peek(0);
        assertEquals(one, event);
        source.remove(event);

        event = source.peek(1);
        assertEquals(two, event);
        source.remove(event);

        event = source.peek(2);
        assertNull(event);
    }

    @Test
    public void testSimpleRollback() {
        final Event one = new Event(1);
        final Event two = new Event(2);

        events.offer(one);
        events.offer(two);

        source.save(-1L);
        
        Event event;
        event = source.peek(0);
        assertEquals(one, event);
        source.remove(event);

        event = source.peek(1);
        assertEquals(two, event);
        source.remove(event);

        source.rollback(-1L);

        event = source.peek(0);
        assertEquals(one, event);
        source.remove(event);

        event = source.peek(1);
        assertEquals(two, event);
        source.remove(event);

        event = source.peek(2);
        assertNull(event);
    }

    @Test
    public void testEventsFollowedByAntimessages() {
        final Event one = new Event(1);
        final Event antione = new Event(1, true);
        final Event two = new Event(2);

        events.offer(one);

        source.save(-1L);

        Event event;
        event = source.peek(0);
        assertEquals(one, event);
        source.remove(event);

        events.offer(antione);
        events.offer(two);

        event = source.peek(1);
        assertEquals(antione, event);
        source.remove(antione);

        source.rollback(-1L);

        event = source.peek(0);
        assertEquals(two, event);
        source.remove(event);

        event = source.peek(2);
        assertNull(event);
    }

    @Test
    public void testImmediatelyEventsFollowedByAntimessages() {
        final Event one = new Event(1);
        final Event antione = new Event(1, true);
        final Event two = new Event(2);

        events.offer(one);
        events.offer(antione);
        events.offer(two);

        Event event;

        event = source.peek(0);
        assertEquals(two, event);
        source.remove(event);

        event = source.peek(2);
        assertNull(event);
    }
}
