package deism;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FastForwardRunloopTest {
    @Mock
    EventSource eventSource;
    @Mock
    EventDispatcher eventDispatcher;
    @Mock
    EventTimer eventTimer;
    @Mock
    EventMatcher terminationCondition;

    /**
     * FastForwardRunloop.run must return immediately when EventSource.peek
     * terminationCondition is met.
     */
    @Test
    public void runNoEvent() {
        final FastForwardRunloop r = new FastForwardRunloop(eventTimer,
                terminationCondition);

        when(eventSource.peek()).thenReturn(null);
        when(terminationCondition.match(null)).thenReturn(true);

        r.run(eventSource, eventDispatcher);

        verify(eventSource).peek();
        verifyZeroInteractions(eventDispatcher);
        verifyZeroInteractions(eventTimer);
        verify(terminationCondition).match(null);
    }
    
    /**
     * FastForwardRunloop.run must execute EventDispatcher.dispatchEvent for
     * each event returned by EventSource.poll
     */
    @Test
    public void runSomeEvents() throws InterruptedException {
        final Event one = new Event(1);
        final Event two = new Event(2);
        final Event term = new Event(3);
        final FastForwardRunloop r = new FastForwardRunloop(eventTimer,
                terminationCondition);

        /*
         * On each call to poll() eventSource will return event one, then two
         * and finally null.
         */
        when(eventSource.peek()).thenReturn(one, two, term, null);
        when(eventSource.poll()).thenReturn(one, two, term, null);
        when(eventTimer.waitForEvent(one)).thenReturn(1L);
        when(eventTimer.waitForEvent(two)).thenReturn(2L);
        when(terminationCondition.match(one)).thenReturn(false);
        when(terminationCondition.match(two)).thenReturn(false);
        when(terminationCondition.match(term)).thenReturn(true);

        r.run(eventSource, eventDispatcher);
        
        verify(eventSource).compute(0);
        verify(eventSource).compute(1);
        verify(eventSource).compute(2);
        verify(eventSource, times(3)).peek();
        verify(eventSource, times(2)).poll();
        verify(eventDispatcher).dispatchEvent(one);
        verify(eventDispatcher).dispatchEvent(two);
        verify(eventTimer).waitForEvent(one);
        verify(eventTimer).waitForEvent(two);
        verify(terminationCondition).match(one);
        verify(terminationCondition).match(two);
        verify(terminationCondition).match(term);
    }

    /**
     * After a call to FastForwardRunloop.stop, no more events must be polled
     * from EventSource and delivered to EventDispatcher
     */
    @Test
    public void runSomeEventsStopAfterSecond() {
        final Event one = new Event(1);
        final Event two = new Event(2);
        final Event three = new Event(3);
        final Event term = new Event(4);

        final FastForwardRunloop r = new FastForwardRunloop(eventTimer,
                terminationCondition);

        when(eventSource.peek()).thenReturn(one, two, three, term, null);
        when(eventSource.poll()).thenReturn(one, two, three, term, null);
        when(eventTimer.waitForEvent(one)).thenReturn(1L);
        when(eventTimer.waitForEvent(two)).thenReturn(2L);
        when(eventTimer.waitForEvent(three)).thenReturn(3L);
        when(eventTimer.waitForEvent(term)).thenReturn(4L);
        when(terminationCondition.match(one)).thenReturn(false);
        when(terminationCondition.match(two)).thenReturn(false);
        when(terminationCondition.match(three)).thenReturn(false);
        when(terminationCondition.match(term)).thenReturn(true);

        /* call r.stop() when eventDispatcher.dispatchEvent(two) is called */
        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                r.stop();
                return null;
            }
        }).when(eventDispatcher).dispatchEvent(two);

        r.run(eventSource, eventDispatcher);

        /* We expect poll being called two times (for Events one and two) */
        verify(eventSource).compute(0);
        verify(eventSource).compute(1);
        verify(eventSource, times(2)).peek();
        verify(eventSource, times(2)).poll();
        verify(eventDispatcher).dispatchEvent(one);
        verify(eventDispatcher).dispatchEvent(two);
        verify(eventTimer).waitForEvent(one);
        verify(eventTimer).waitForEvent(two);
        verify(terminationCondition).match(one);
        verify(terminationCondition).match(two);
    }
    
    @Test
    public void testReevaluateWhenTimeoutNotReached()
    {
        final Event one = new Event(2);
        final Event term = new Event(3);

        final FastForwardRunloop r = new FastForwardRunloop(eventTimer,
                terminationCondition);

        when(eventSource.peek()).thenReturn(one, one, term, null);
        when(eventSource.poll()).thenReturn(one, one, term, null);
        when(eventTimer.waitForEvent(one)).thenReturn(1L, 2L);
        when(eventTimer.waitForEvent(term)).thenReturn(3L);
        
        when(terminationCondition.match(one)).thenReturn(false);
        when(terminationCondition.match(term)).thenReturn(true);
        
        r.run(eventSource, eventDispatcher);

        verify(eventSource).compute(0);
        verify(eventSource).compute(1);
        verify(eventSource).compute(2);
        // We expect poll being called three times (one, one, term) */
        verify(eventSource, times(3)).peek();
        // We expect poll being called only once (the second time for one)
        verify(eventSource).poll();
        
        verify(eventDispatcher).dispatchEvent(one);
        verify(eventTimer, times(2)).waitForEvent(one);
        verify(terminationCondition, times(2)).match(one);
        verify(terminationCondition).match(term);
    }
    /**
     * If an EventSource returns events which are not ordered by ascending
     * timestamp we expect FastWordwardRunloop to throw an exception.
     */
    @Test(expected = EventSourceOrderException.class)
    public void runSourceWithWrongEventOrder() {
        final Event one = new Event(1);
        final Event two = new Event(2);
        final FastForwardRunloop r = new FastForwardRunloop(eventTimer,
                terminationCondition);

        /*
         * Simulate event source which returns events in the wrong order.
         */
        when(eventSource.peek()).thenReturn(two, one, null);
        when(eventSource.poll()).thenReturn(two, one, null);
        
        when(eventTimer.waitForEvent(two)).thenReturn(2L);
        when(eventTimer.waitForEvent(one)).thenReturn(1L);
        when(terminationCondition.match(two)).thenReturn(false);
        when(terminationCondition.match(one)).thenReturn(false);

        r.run(eventSource, eventDispatcher);

        verify(eventSource).compute(0);
        verify(eventSource).compute(2);
        
        /* Event two must have been delivered properly */
        verify(eventSource, times(2)).peek();
        verify(eventSource).poll();
        verify(eventDispatcher).dispatchEvent(two);
        verify(eventTimer).waitForEvent(two);
        verify(eventTimer).waitForEvent(one);
        verify(terminationCondition).match(two);
        verify(terminationCondition).match(one);
    }
}
