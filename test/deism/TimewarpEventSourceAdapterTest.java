package deism;

import static org.junit.Assert.*;

import java.util.ArrayDeque;
import java.util.Queue;

import org.junit.Test;

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

        @Override
        public void start(long startSimtime) {
        }

        @Override
        public void stop() {
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
    public void testEventsFollowedByAntimessages() {
        final Event one = new Event(1);
        final Event antione = new Event(1, true);
        final Event two = new Event(2);

        events.offer(one);
        events.offer(antione);
        events.offer(two);

        source.save(-1L);

        Event event;
        event = source.peek(0);
        assertEquals(one, event);
        source.remove(event);

        event = source.peek(1);
        assertEquals(antione, event);
        source.rollback(-1L);

        event = source.peek(0);
        assertEquals(two, event);
        source.remove(event);

        event = source.peek(2);
        assertNull(event);
    }

    @Test
    public void testAntimessageImmediatelyFollowedByEvent() {
        final Event antione = new Event(1, true);
        final Event one = new Event(1);
        final Event two = new Event(2);

        events.offer(antione);
        events.offer(one);
        events.offer(two);

        Event event;
        event = source.peek(0);
        assertEquals(two, event);
        source.remove(event);

        event = source.peek(2);
        assertNull(event);
    }

//    @Test
//    public void testAntimessageFollowedByEvent() {
//        final Event antione = new Event(1, true);
//        final Event one = new Event(1);
//        final Event two = new Event(2);
//
//        events.offer(antione);
//        events.offer(two);
//        events.offer(one);
//
//        Event event;
//        event = source.peek(0);
//        assertEquals(two, event);
//        source.remove(event);
//
//        event = source.peek(2);
//        assertNull(event);
//    }
}
