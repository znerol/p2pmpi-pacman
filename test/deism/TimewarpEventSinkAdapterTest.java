package deism;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.core.Event;
import deism.core.EventSink;
import deism.stateful.TimewarpEventSinkAdapter;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TimewarpEventSinkAdapterTest {
    @Mock
    private EventSink sink;
    
    private TimewarpEventSinkAdapter sinkAdapter;

    @Before
    public void setUp() {
        sinkAdapter = new TimewarpEventSinkAdapter(sink);
    }

    @Test
    public void testSimpleOffer() {
        Event one = new Event(1L);

        sinkAdapter.offer(one);

        verify(sink).offer(one);

        verifyNoMoreInteractions(sink);
    }

    @Test
    public void testInSequenceEventsWithInvertedOffersReduced() {
        Event one = new Event(1L);
        Event two = new Event(2L);
        Event three = new Event(3L);

        sinkAdapter.offer(one);
        sinkAdapter.offer(one.inverseEvent());
        sinkAdapter.offer(two);
        sinkAdapter.offer(three);
        sinkAdapter.offer(three.inverseEvent());

        verify(sink).offer(one);
        verify(sink).offer(one.inverseEvent());
        verify(sink).offer(two);
        verify(sink).offer(three);
        verify(sink).offer(three.inverseEvent());

        verifyNoMoreInteractions(sink);
    }

    @Test
    public void testOutOfOrderSequenceWithRollback() {
        Event one = new Event(1L);
        Event two = new Event(2L);
        Event three = new Event(3L);

        sinkAdapter.save(0L);
        sinkAdapter.offer(one);
        sinkAdapter.save(1L);
        sinkAdapter.offer(three);
        sinkAdapter.save(3L);
        sinkAdapter.offer(two);

        sinkAdapter.rollback(0L);

        sinkAdapter.offer(one);
        sinkAdapter.save(1L);
        sinkAdapter.offer(two);
        sinkAdapter.save(2L);
        sinkAdapter.offer(three);
        sinkAdapter.save(3L);

        verify(sink).offer(one);
        verify(sink).offer(three);
        verify(sink).offer(two);

        verifyNoMoreInteractions(sink);
    }

    @Test
    public void testInvertBeforeRollback() {
        Event one = new Event(1L);
        Event two = new Event(2L);
        Event three = new Event(3L);

        sinkAdapter.save(0L);
        sinkAdapter.offer(one);
        sinkAdapter.offer(one.inverseEvent());
        sinkAdapter.offer(three);
        sinkAdapter.save(3L);
        sinkAdapter.offer(two);

        sinkAdapter.rollback(0L);

        sinkAdapter.offer(two);
        sinkAdapter.save(2L);
        sinkAdapter.offer(three);
        sinkAdapter.save(3L);

        verify(sink).offer(one);
        verify(sink).offer(one.inverseEvent());
        // rollback
        verify(sink).offer(three);
        verify(sink).offer(two);

        verifyNoMoreInteractions(sink);
    }
}
