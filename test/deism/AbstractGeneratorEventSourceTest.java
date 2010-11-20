package deism;

import static org.junit.Assert.*;

import java.util.ArrayDeque;
import java.util.Queue;

import org.junit.Test;

public class AbstractGeneratorEventSourceTest {
    /**
     * An array of Events delivered by class under test
     */
    final Queue<Event> events = new ArrayDeque<Event>();
    
    /**
     * Simple test-only implementation of AbstractgeneratorEventSource
     * Pattern: Subclass and Override
     */
    final AbstractGeneraterorEventSource source =
        new AbstractGeneraterorEventSource() {
        @Override
        public Event nextEvent(long currentSimtime) {
            return events.poll();
        }
    };
    
    /**
     * Ensure that the same object is returned for consecutive calls to peek()
     */
    @Test
    public void testPeekEventSource() {
        Event e1 = new Event(1);
        events.add(e1);
        
        source.compute(0);
        assertEquals(e1, source.peek(0));
        source.compute(0);
        assertEquals(e1, source.peek(0));
        source.compute(0);
        assertEquals(e1, source.peek(0));
    }
    
    /**
     * Ensure that poll() returns each object exactly once.
     */
    @Test
    public void testPollEventSourceOnce() {
        Event e1 = new Event(1);
        Event e2 = new Event(2);
        events.add(e1);
        events.add(e2);
        
        source.compute(0);
        assertEquals(e1, source.poll(0));
        source.compute(0);
        assertEquals(e2, source.poll(0));
        source.compute(0);
        assertEquals(null, source.poll(0));
    }
    
    /**
     * Ensure that getLastEventSimtime() returns the timestamp of the last
     * event returned by nextEvent().
     */
    @Test
    public void testGetLastEventSimtime() {
        Event e1 = new Event(42);
        events.add(e1);
        
        assertEquals(0, source.getLastEventSimtime());
        
        source.compute(0);
        assertEquals(e1, source.poll(0));
        assertEquals(42, source.getLastEventSimtime());

        source.compute(42);
        assertEquals(null, source.poll(0));
        assertEquals(42, source.getLastEventSimtime());
    }
}
