package deism;

import java.util.ArrayDeque;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.core.Event;
import deism.core.EventCondition;
import deism.core.EventDispatcher;
import deism.core.EventSource;
import deism.run.EventRunloopRecoveryStrategy;
import deism.run.ExecutionGovernor;
import deism.run.DefaultEventRunloop;
import deism.run.TimewarpRunloopRecoveryStrategy;
import deism.stateful.DefaultTimewarpDiscreteEventProcess;
import deism.stateful.StateHistory;
import deism.stateful.TimewarpEventSource;
import deism.stateful.TimewarpEventSourceAdapter;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TimewarpRunloopTest {
    @Mock
    EventDispatcher eventDispatcher;
    @Mock
    ExecutionGovernor governor;
    @Mock
    EventCondition terminationCondition;
    @Mock
    EventCondition snapshotCondition;

    List<StateHistory<Long>> stateObjects;
    EventRunloopRecoveryStrategy recoveryStrategy;
    DefaultEventRunloop runloop;
    
    ArrayDeque<Event> eventQueue;
    EventSource simpleEventSource;
    TimewarpEventSource eventSource;
    DefaultTimewarpDiscreteEventProcess process;
    
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
        };
        eventSource = new TimewarpEventSourceAdapter(simpleEventSource);

        process = new DefaultTimewarpDiscreteEventProcess();
        process.addEventSource(eventSource);
        process.addStatefulObject(eventSource);
        process.addEventDispatcher(eventDispatcher);

        recoveryStrategy = new TimewarpRunloopRecoveryStrategy(process);
        runloop = new DefaultEventRunloop(governor, terminationCondition,
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

        runloop.run(process);

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

        runloop.run(process);

        // All events must have been delivered properly. However event four
        // will be emitted twice.
        verify(eventDispatcher).dispatchEvent(one);
        verify(eventDispatcher).dispatchEvent(two);
        verify(eventDispatcher).dispatchEvent(three);
        verify(eventDispatcher, times(2)).dispatchEvent(four);
        verify(eventDispatcher).dispatchEvent(five);
    }    
}
