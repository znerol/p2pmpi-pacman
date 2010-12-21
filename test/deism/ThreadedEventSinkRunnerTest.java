package deism;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import deism.core.Event;
import deism.core.EventSink;
import deism.core.Startable;
import deism.legacy.ThreadedEventSinkRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ThreadedEventSinkRunnerTest {
    private interface StartableEventSink extends EventSink, Startable {
    }

    @Mock
    private StartableEventSink sink;
    private ThreadedEventSinkRunner threadedSink;

    @Before
    public void setUp() {
        threadedSink = new ThreadedEventSinkRunner(sink);
    }

    @Test
    public void testStartStopOfferOneEvent() {
        final Event event = new Event(1L);

        // stop the thread when it tries to offer the first event to the sink
        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                threadedSink.stop(0L);
                return null;
            }
        }).when(sink).offer(event);

        threadedSink.start(0L);
        try {
            Thread.sleep(10);
        }
        catch (InterruptedException e) {
            // bad luck...
        }
        threadedSink.offer(event);

        verify(sink, timeout(100)).start(0L);
        verify(sink, timeout(100)).stop(0L);
        verify(sink, timeout(100)).offer(event);
        verifyNoMoreInteractions(sink);
    }
}
