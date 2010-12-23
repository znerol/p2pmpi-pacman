package deism.run;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import deism.core.Event;
import deism.core.EventCondition;
import deism.ipc.base.Handler;
import deism.ipc.base.Message;
import deism.process.DiscreteEventProcess;
import deism.run.Runloop;
import deism.run.StateController;
import deism.run.ExecutionGovernor;
import deism.stateful.StateHistoryException;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RunloopTest {
    @Mock
    DiscreteEventProcess process;
    @Mock
    ExecutionGovernor governor;
    @Mock
    EventCondition terminationCondition;
    @Mock
    StateController stateController;
    @Mock
    EventCondition snapshotCondition;
    @Mock
    IpcEndpoint endpoint;
    @Mock
    Handler<Message> ipcHandler;
    @Mock
    LvtListener lvtListener;

    /**
     * FastForwardRunloop.run must return immediately when EventSource.peek
     * terminationCondition is met.
     */
    @Test
    public void runNoEvent() {
        final Runloop r = new Runloop(governor, terminationCondition,
                stateController, snapshotCondition, endpoint, ipcHandler,
                lvtListener);

        when(process.peek(0)).thenReturn(null);
        when(terminationCondition.match(null)).thenReturn(true);

        r.run(process);

        verify(process).peek(0);
        verifyZeroInteractions(process);
        verify(governor).start(0);
        verify(governor).stop(0);
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
        final Runloop r = new Runloop(governor, terminationCondition,
                stateController, snapshotCondition, endpoint, ipcHandler,
                lvtListener);

        /*
         * On each call to receive() process will return event one, then
         * two and finally null.
         */
        when(process.peek(0)).thenReturn(one);
        when(process.peek(1)).thenReturn(two);
        when(process.peek(2)).thenReturn(null);
        when(governor.suspendUntil(1)).thenReturn(1L);
        when(governor.suspendUntil(2)).thenReturn(2L);
        when(terminationCondition.match(one)).thenReturn(false);
        when(terminationCondition.match(two)).thenReturn(false);
        when(terminationCondition.match(null)).thenReturn(true);

        r.run(process);
        
        verify(process).peek(0);
        verify(process).peek(1);
        verify(process).peek(2);
        verify(process).offer(one);
        verify(process).offer(two);
        verify(process).dispatchEvent(one);
        verify(process).dispatchEvent(two);
        verify(governor).suspendUntil(1);
        verify(governor).suspendUntil(2);
        when(terminationCondition.match((Event)isNotNull())).thenReturn(false);
        when(terminationCondition.match(null)).thenReturn(true);
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

        final Runloop r = new Runloop(governor, terminationCondition,
                stateController, snapshotCondition, endpoint, ipcHandler,
                lvtListener);

        when(process.peek(0)).thenReturn(one);
        when(process.peek(1)).thenReturn(two);
        when(process.peek(2)).thenReturn(three);
        when(process.peek(3)).thenReturn(null);
        when(governor.suspendUntil(1)).thenReturn(1L);
        when(governor.suspendUntil(2)).thenReturn(2L);
        when(governor.suspendUntil(3)).thenReturn(3L);
        when(terminationCondition.match((Event)isNotNull())).thenReturn(false);
        when(terminationCondition.match(null)).thenReturn(true);

        /* call r.stop() when process.dispatchEvent(two) is called */
        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                r.stop();
                return null;
            }
        }).when(process).dispatchEvent(two);

        r.run(process);

        /* We expect receive being called two times (for Events one and two) */
        verify(process).peek(0);
        verify(process).peek(1);
        verify(process).offer(one);
        verify(process).offer(two);
        verify(process).dispatchEvent(one);
        verify(process).dispatchEvent(two);
        verify(governor).suspendUntil(1);
        verify(governor).suspendUntil(2);
        verify(terminationCondition).match(one);
        verify(terminationCondition).match(two);
    }
    
    @Test
    public void testReevaluateWhenTimeoutNotReached()
    {
        final Event one = new Event(2);

        final Runloop r = new Runloop(governor, terminationCondition,
                stateController, snapshotCondition, endpoint, ipcHandler,
                lvtListener);

        when(process.peek(0)).thenReturn(one);
        when(process.peek(1)).thenReturn(one);
        when(process.peek(2)).thenReturn(null);
        when(governor.suspendUntil(2)).thenReturn(1L, 2L);
        when(governor.suspendUntil(3)).thenReturn(3L);
        when(terminationCondition.match((Event)isNotNull())).thenReturn(false);
        when(terminationCondition.match(null)).thenReturn(true);

        r.run(process);

        // We expect receive being called three times (one, one, term) */
        verify(process).peek(0);
        verify(process).peek(1);
        verify(process).peek(2);

        verify(process, times(2)).offer(one);
        verify(process).offer(one.inverseEvent());
        verify(process).dispatchEvent(one);
        verify(governor, times(2)).suspendUntil(2);
        verify(terminationCondition, times(2)).match(one);
        verify(terminationCondition).match(null);
    }
    /**
     * If an EventSource returns events which are not ordered by ascending
     * timestamp we expect FastWordwardRunloop to try a rollback()
     */
    @Test(expected = StateHistoryException.class)
    public void runSourceWithWrongEventOrder() {
        final Event one = new Event(1);
        final Event two = new Event(2);
        final Runloop r = new Runloop(governor, terminationCondition,
                stateController, snapshotCondition, endpoint, ipcHandler,
                lvtListener);

        /*
         * Simulate event source which returns events in the wrong order.
         */
        when(process.peek(0)).thenReturn(two);
        when(process.peek(2)).thenReturn(one);
        when(process.peek(3)).thenReturn(null);
        
        when(governor.suspendUntil(1)).thenReturn(1L);
        when(governor.suspendUntil(2)).thenReturn(2L);
        when(terminationCondition.match(two)).thenReturn(false);
        when(terminationCondition.match(one)).thenReturn(false);

        /* throw a state history exception whenever rollback is called */
        doThrow(new StateHistoryException("")).when(
                stateController).rollback(anyLong());

        r.run(process);

        verify(process).peek(0);
        verify(process).peek(2);
        verify(process).offer(two);
        verify(process).offer(one);
        verify(process).offer(one.inverseEvent());
        
        /* Event two must have been delivered properly */
        verify(process).dispatchEvent(two);
        verify(governor).suspendUntil(1);
        verify(governor).suspendUntil(2);
        verify(terminationCondition).match(two);
        verify(terminationCondition).match(one);
    }
}
