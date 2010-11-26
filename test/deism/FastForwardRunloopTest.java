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
    ExecutionGovernor governor;
    @Mock
    EventCondition terminationCondition;
    @Mock
    EventRunloopRecoveryStrategy recoveryStrategy;
    @Mock
    EventCondition snapshotCondition;

    /**
     * FastForwardRunloop.run must return immediately when EventSource.peek
     * terminationCondition is met.
     */
    @Test
    public void runNoEvent() {
        final FastForwardRunloop r = new FastForwardRunloop(governor,
                terminationCondition, recoveryStrategy, snapshotCondition);

        when(eventSource.receive()).thenReturn(null);
        when(terminationCondition.match(null)).thenReturn(true);

        r.run(eventSource, eventDispatcher);

        verify(eventSource).receive();
        verifyZeroInteractions(eventDispatcher);
        verifyZeroInteractions(governor);
        verify(terminationCondition).match(null);
    }
    
    /**
     * FastForwardRunloop.run must execute EventDispatcher.dispatchEvent for
     * each event returned by EventSource.receive
     */
    @Test
    public void runSomeEvents() {
        final Event one = new Event(1);
        final Event two = new Event(2);
        final Event term = new Event(3);
        final FastForwardRunloop r = new FastForwardRunloop(governor,
                terminationCondition, recoveryStrategy, snapshotCondition);

        /*
         * On each call to receive() eventSource will return event one, then
         * two and finally null.
         */
        when(eventSource.receive()).thenReturn(one, two, term, null);
        when(governor.suspendUntil(1)).thenReturn(1L);
        when(governor.suspendUntil(2)).thenReturn(2L);
        when(terminationCondition.match(one)).thenReturn(false);
        when(terminationCondition.match(two)).thenReturn(false);
        when(terminationCondition.match(term)).thenReturn(true);

        r.run(eventSource, eventDispatcher);
        
        verify(eventSource).compute(0);
        verify(eventSource).compute(1);
        verify(eventSource).compute(2);
        verify(eventSource, times(3)).receive();
        verify(eventDispatcher).dispatchEvent(one);
        verify(eventDispatcher).dispatchEvent(two);
        verify(governor).suspendUntil(1);
        verify(governor).suspendUntil(2);
        verify(terminationCondition).match(one);
        verify(terminationCondition).match(two);
        verify(terminationCondition).match(term);
    }

    /**
     * After a call to FastForwardRunloop.stop, no more events must be received
     * from EventSource and delivered to EventDispatcher
     */
    @Test
    public void runSomeEventsStopAfterSecond() {
        final Event one = new Event(1);
        final Event two = new Event(2);
        final Event three = new Event(3);
        final Event term = new Event(4);

        final FastForwardRunloop r = new FastForwardRunloop(governor,
                terminationCondition, recoveryStrategy, snapshotCondition);

        when(eventSource.receive()).thenReturn(one, two, three, term, null);
        when(governor.suspendUntil(1)).thenReturn(1L);
        when(governor.suspendUntil(2)).thenReturn(2L);
        when(governor.suspendUntil(3)).thenReturn(3L);
        when(governor.suspendUntil(4)).thenReturn(4L);
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

        /* We expect receive being called two times (for Events one and two) */
        verify(eventSource).compute(0);
        verify(eventSource).compute(1);
        verify(eventSource, times(2)).receive();
        verify(eventDispatcher).dispatchEvent(one);
        verify(eventDispatcher).dispatchEvent(two);
        verify(governor).suspendUntil(1);
        verify(governor).suspendUntil(2);
        verify(terminationCondition).match(one);
        verify(terminationCondition).match(two);
    }
    
    @Test
    public void testReevaluateWhenTimeoutNotReached()
    {
        final Event one = new Event(2);
        final Event term = new Event(3);

        final FastForwardRunloop r = new FastForwardRunloop(governor,
                terminationCondition, recoveryStrategy, snapshotCondition);

        when(eventSource.receive()).thenReturn(one, one, term, null);
        when(governor.suspendUntil(2)).thenReturn(1L, 2L);
        when(governor.suspendUntil(3)).thenReturn(3L);
        
        when(terminationCondition.match(one)).thenReturn(false);
        when(terminationCondition.match(term)).thenReturn(true);
        
        r.run(eventSource, eventDispatcher);

        verify(eventSource).compute(0);
        verify(eventSource).compute(1);
        verify(eventSource).compute(2);
        // We expect receive being called three times (one, one, term) */
        verify(eventSource, times(3)).receive();
        // After the first attempt to deliver one, runloop must try to put it 
        // back into the source.
        verify(eventSource).reject(one);
        
        verify(eventDispatcher).dispatchEvent(one);
        verify(governor, times(2)).suspendUntil(2);
        verify(terminationCondition, times(2)).match(one);
        verify(terminationCondition).match(term);
    }
    /**
     * If an EventSource returns events which are not ordered by ascending
     * timestamp we expect FastWordwardRunloop to throw an exception.
     */
    @Test(expected = StateHistoryException.class)
    public void runSourceWithWrongEventOrder() {
        final Event one = new Event(1);
        final Event two = new Event(2);
        final FastForwardRunloop r = new FastForwardRunloop(governor,
                terminationCondition, recoveryStrategy, snapshotCondition);

        /*
         * Simulate event source which returns events in the wrong order.
         */
        when(eventSource.receive()).thenReturn(two, one, null);
        
        when(governor.suspendUntil(1)).thenReturn(1L);
        when(governor.suspendUntil(2)).thenReturn(2L);
        when(terminationCondition.match(two)).thenReturn(false);
        when(terminationCondition.match(one)).thenReturn(false);

        /* throw a state history exception whenever rollback is called */
        doThrow(new StateHistoryException("")).when(
                recoveryStrategy).rollback(anyLong());

        r.run(eventSource, eventDispatcher);

        verify(eventSource).compute(0);
        verify(eventSource).compute(2);
        
        /* Event two must have been delivered properly */
        verify(eventSource).receive();
        verify(eventDispatcher).dispatchEvent(two);
        verify(governor).suspendUntil(1);
        verify(governor).suspendUntil(2);
        verify(terminationCondition).match(two);
        verify(terminationCondition).match(one);
    }
}
