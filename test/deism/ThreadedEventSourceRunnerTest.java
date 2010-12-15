package deism;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import deism.core.Event;
import deism.core.EventSource;
import deism.run.ExecutionGovernor;
import deism.run.Startable;
import deism.run.ThreadedEventSourceRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ThreadedEventSourceRunnerTest {
    private interface StartableEventSource extends EventSource, Startable {
    }

    @Mock
    private StartableEventSource source;
    @Mock
    private ExecutionGovernor governor;

    private ThreadedEventSourceRunner threadedSource;

    @Before
    public void setUp() {
        threadedSource = new ThreadedEventSourceRunner(governor, source);
    }

    @Test
    public void testStartStop() {
        // stop the thread when it tries to get the first event from the
        // original event source
        when(source.peek(0L)).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                threadedSource.stop(0L);
                return null;
            }
        });

        threadedSource.start(0L);

        verify(source, timeout(100)).start(0L);
        verify(source, timeout(100)).stop(0L);
    }

    @Test
    public void testGetOneEvent() {
        Event event = new Event(1L);

        when(source.peek(0L)).thenReturn(event);

        // stop the thread when it tries to notify the governor of the newly
        // arrived event
        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                threadedSource.stop(1L);
                return null;
            }
        }).when(governor).resume(1L);

        threadedSource.start(0L);

        verify(source, timeout(100)).peek(0L);
        verify(source, timeout(100)).remove(event);

        Event result;

        // ensure that we actually get the events from the event source
        result = threadedSource.peek(0L);
        assertEquals(event, result);

        threadedSource.remove(result);
        result = threadedSource.peek(1L);
        assertEquals(null, result);
    }
}
