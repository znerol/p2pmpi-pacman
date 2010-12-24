package deism.stateful;

import java.util.ArrayDeque;

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
import deism.process.DefaultDiscreteEventProcess;
import deism.run.MessageCenter;
import deism.run.ExecutionGovernor;
import deism.run.LvtListener;
import deism.run.Runloop;
import deism.run.Service;
import deism.run.StateController;
import deism.run.StateHistoryController;
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
    @Mock
    MessageCenter messageCenter;
    @Mock
    LvtListener lvtListener;

    Service service = new Service();
    StateController stateController;
    Runloop runloop;
    
    ArrayDeque<Event> eventQueue;
    EventSource simpleEventSource;
    TimewarpEventSource eventSource;
    DefaultDiscreteEventProcess process;
    
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

        process = new DefaultDiscreteEventProcess();
        process.addEventSource(eventSource);
        service.addStatefulObject(eventSource);
        process.addEventDispatcher(eventDispatcher);

        stateController = new StateHistoryController();
        stateController.setStateObject(service);
        runloop = new Runloop(governor, terminationCondition, stateController,
                snapshotCondition, messageCenter, lvtListener, service);
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
