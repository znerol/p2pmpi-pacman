package deism;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventSinkCollectionTest {
    @Mock
    EventSink firstSink;
    @Mock
    EventSink secondSink;

    /**
     * EventSinkCollection.offer must return false if list of EventSinks is
     * empty.
     */
    @Test
    public void collectionWithoutSinks() {
        final ArrayList<EventSink> Sinks = new ArrayList<EventSink>();
        EventSinkCollection c = new EventSinkCollection(Sinks);

        boolean accepted = c.offer(new Event(0));
        assertFalse(accepted);
    }

    @Test
    public void collectionWithoutSinksArray() {
        final EventSink[] Sinks = {};
        EventSinkCollection c = new EventSinkCollection(Sinks);

        boolean accepted = c.offer(new Event(0));
        assertFalse(accepted);
    }

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

    /**
     * EventSinkCollection.remove may only call remove on the sources whose
     * offer-method returned true before
     */
    @Test
    public void collectionOfferRemove() {
        final Event one = new Event(1);
        final Event two = new Event(2);
        final EventSink[] sinks = {firstSink, secondSink};

        when(firstSink.offer(one)).thenReturn(true);
        when(firstSink.offer(two)).thenReturn(false);
        when(secondSink.offer(one)).thenReturn(false);
        when(secondSink.offer(two)).thenReturn(true);

        EventSinkCollection c = new EventSinkCollection(sinks);

        boolean accepted;
        accepted = c.offer(one);
        assertTrue(accepted);
        c.remove(one);
        accepted = c.offer(two);
        assertTrue(accepted);
        c.remove(two);

        verify(firstSink).offer(one);
        verify(firstSink).offer(two);
        verify(secondSink).offer(one);
        verify(secondSink).offer(two);
        verify(firstSink).remove(one);
        verify(secondSink).remove(two);
    }
}
