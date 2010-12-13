package deism;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventSinkCollectionTest {
    @Mock
    EventSink firstSink;
    @Mock
    EventSink secondSink;

    @Test
    public void collectionStartStop() {
        /* construct Sinks list */
        final EventSink[] sinks = { firstSink, secondSink, };

        /* test EventSinkController */
        EventSinkCollection c = new EventSinkCollection(sinks);

        c.start(0);
        verify(firstSink).start(0);
        verify(secondSink).start(0);

        c.stop();
        verify(firstSink).stop();
        verify(secondSink).stop();
    }

    /**
     * EventSinkCollection.offer must deliver an event to all sinks
     */
    @Test
    public void collectionOfferWithTwoSinks() {
        final Event one = new Event(1);
        final EventSink[] sinks = {firstSink, secondSink};

        /* test EventSinkController */
        EventSinkCollection c = new EventSinkCollection(sinks);

        c.offer(one);

        verify(firstSink).offer(one);
        verify(secondSink).offer(one);
        verifyNoMoreInteractions(firstSink);
        verifyNoMoreInteractions(secondSink);
    }
}
