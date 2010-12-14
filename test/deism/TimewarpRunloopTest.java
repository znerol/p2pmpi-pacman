package deism;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TimewarpRunloopTest {
    @Mock
    EventDispatcher eventDispatcher;
    @Mock
    EventSink eventSink;
    @Mock
    ExecutionGovernor governor;
    @Mock
    EventCondition terminationCondition;
    @Mock
    EventCondition snapshotCondition;

    List<StateHistory<Long>> stateObjects;
    EventRunloopRecoveryStrategy recoveryStrategy;
    FastForwardRunloop runloop;
    
    ArrayDeque<Event> eventQueue;
    EventSource simpleEventSource;
    TimewarpEventSource eventSource;
    
    @Before
    public void setUp() {
        BasicConfigurator.configure();
        eventQueue = new ArrayDeque<Event>();
        
        simpleEventSource = new EventSource() {
            @Override
            public Event peek(long currentSimtime) {
                return eventQueue.peek();
            }

            @Override
            public void remove(Event event) {
                eventQueue.remove(event);
            }

            @Override
            public void start(long startSimtime) {
            }

            @Override
            public void stop() {
            }
        };
        eventSource = new TimewarpEventSourceAdapter(simpleEventSource);
        
        stateObjects = new ArrayList<StateHistory<Long>>();
        stateObjects.add(eventSource);
        recoveryStrategy = new TimewarpRunloopRecoveryStrategy(stateObjects);
        runloop = new FastForwardRunloop(governor, terminationCondition,
                recoveryStrategy, snapshotCondition);
    }

    @Test
    public void runSourceWithWrongEventOrderRollbackToLoopStart() {
        final Event one = new Event(1);
        final Event two = new Event(2);
        final Event three = new Event(3);

        eventQueue.add(two);
        eventQueue.add(three);
        eventQueue.add(one);
        
        /*
         * Simulate event source which returns events in the wrong order.
         */
        when(governor.suspendUntil(1)).thenReturn(1L);
        when(governor.suspendUntil(2)).thenReturn(2L);
        when(governor.suspendUntil(3)).thenReturn(3L);
        when(terminationCondition.match((Event)isNotNull())).thenReturn(false);
        when(terminationCondition.match(null)).thenReturn(true);
        when(snapshotCondition.match((Event)isNotNull())).thenReturn(true);

        runloop.run(eventSource, eventSink, eventDispatcher);

        // All events must have been delivered properly. However event four
        // will be emitted twice.
        verify(eventDispatcher).dispatchEvent(one);
        verify(eventDispatcher, times(2)).dispatchEvent(two);
        verify(eventDispatcher).dispatchEvent(three);
    }    

    @Test
    public void runSourceWithWrongEventOrder() {
        final Event one = new Event(1);
        final Event two = new Event(2);
        final Event three = new Event(3);
        final Event four = new Event(4);
        final Event five = new Event(5);

        eventQueue.add(one);
        eventQueue.add(two);
        eventQueue.add(four);
        eventQueue.add(five);
        eventQueue.add(three);
        
        /*
         * Simulate event source which returns events in the wrong order.
         */
        when(governor.suspendUntil(1)).thenReturn(1L);
        when(governor.suspendUntil(2)).thenReturn(2L);
        when(governor.suspendUntil(3)).thenReturn(3L);
        when(governor.suspendUntil(4)).thenReturn(4L);
        when(governor.suspendUntil(5)).thenReturn(5L);
        when(terminationCondition.match((Event)isNotNull())).thenReturn(false);
        when(terminationCondition.match(null)).thenReturn(true);
        when(snapshotCondition.match((Event)isNotNull())).thenReturn(true);

        runloop.run(eventSource, eventSink, eventDispatcher);

        // All events must have been delivered properly. However event four
        // will be emitted twice.
        verify(eventDispatcher).dispatchEvent(one);
        verify(eventDispatcher).dispatchEvent(two);
        verify(eventDispatcher).dispatchEvent(three);
        verify(eventDispatcher, times(2)).dispatchEvent(four);
        verify(eventDispatcher).dispatchEvent(five);
    }    
}
