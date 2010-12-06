package deism;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventSourceCollectionTest {
    @Mock EventSource firstSource;
    @Mock EventSource secondSource;

    /**
     * EventSourceCollection.receive must return null if list of EventSources
     * is empty.
     */
    @Test
    public void collectionWithoutSources() {
        final ArrayList<EventSource> sources = new ArrayList<EventSource>();
        EventSourceCollection c = new EventSourceCollection(sources);

        Event event = c.receive(0);
        assertNull(event);
    }
    
    @Test
    public void collectionWithoutSourcesArray() {
        final EventSource[] sources = {};
        EventSourceCollection c = new EventSourceCollection(sources);
        
        Event event = c.receive(0);
        assertNull(event);
    }
    
    @Test
    public void collectionStartStop() {
        /* construct sources list */
        final EventSource[] sources = {
            firstSource,
            secondSource,
        };

        /* test EventSourceController */
        EventSourceCollection c = new EventSourceCollection(sources);

        c.start(0);
        verify(firstSource).start(0);
        verify(secondSource).start(0);

        c.stop();
        verify(firstSource).stop();
        verify(secondSource).stop();
    }
    
    /**
     * EventSourceCollection.peek and EventSourceCollection.receive must return
     * events in ascending timestamp-order from any source.
     */
    @Test
    public void collectionReceiveWithTwoSources() {
        final Event one = new Event(1);
        final Event two = new Event(2);
        final Event three = new Event(3);
        final Event four = new Event(5);

        when(firstSource.receive(0)).thenReturn(one);
        when(firstSource.receive(1)).thenReturn(two);
        when(firstSource.receive(2)).thenReturn(four);
        when(firstSource.receive(3)).thenReturn(four);
        when(firstSource.receive(4)).thenReturn(four);
        when(firstSource.receive(5)).thenReturn(null);

        when(secondSource.receive(0)).thenReturn(three);
        when(secondSource.receive(1)).thenReturn(three);
        when(secondSource.receive(2)).thenReturn(three);
        when(secondSource.receive(3)).thenReturn(null);
        when(secondSource.receive(4)).thenReturn(null);
        when(secondSource.receive(5)).thenReturn(null);

        /* construct sources list */
        final ArrayList<EventSource> sources = new ArrayList<EventSource>();
        sources.add(firstSource);
        sources.add(secondSource);

        /* test EventSourceController */
        EventSourceCollection c = new EventSourceCollection(sources);

        /* verify that events are returned in the expected order */
        assertEquals(one, c.receive(0));
        c.accept(one);
        assertEquals(two, c.receive(1));
        c.accept(two);
        assertEquals(three, c.receive(2));
        c.accept(three);
        assertEquals(four, c.receive(3));
        assertEquals(four, c.receive(4));
        c.accept(four);
        assertEquals(null, c.receive(5));
        
        // verify that both of the sources were polled 5 times
        verify(firstSource).receive(0);
        verify(firstSource).receive(1);
        verify(firstSource).receive(2);
        verify(firstSource).receive(3);
        verify(firstSource).receive(4);
        verify(firstSource).receive(5);
        verify(secondSource).receive(0);
        verify(secondSource).receive(1);
        verify(secondSource).receive(2);
        verify(secondSource).receive(3);
        verify(secondSource).receive(4);
        verify(secondSource).receive(5);

        // verify accept and reject
        verify(firstSource).accept(one);
        verify(firstSource).accept(two);
        verify(firstSource).accept(four);
        verify(secondSource).accept(three);
    }
}
