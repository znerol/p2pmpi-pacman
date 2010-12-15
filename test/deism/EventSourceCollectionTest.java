package deism;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.core.Event;
import deism.core.EventSource;
import deism.process.EventSourceCollection;

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

        Event event = c.peek(0);
        assertNull(event);
    }
    
    @Test
    public void collectionWithoutSourcesArray() {
        final EventSource[] sources = {};
        EventSourceCollection c = new EventSourceCollection(sources);
        
        Event event = c.peek(0);
        assertNull(event);
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

        when(firstSource.peek(0)).thenReturn(one);
        when(firstSource.peek(1)).thenReturn(two);
        when(firstSource.peek(2)).thenReturn(four);
        when(firstSource.peek(3)).thenReturn(four);
        when(firstSource.peek(4)).thenReturn(four);
        when(firstSource.peek(5)).thenReturn(null);

        when(secondSource.peek(0)).thenReturn(three);
        when(secondSource.peek(1)).thenReturn(three);
        when(secondSource.peek(2)).thenReturn(three);
        when(secondSource.peek(3)).thenReturn(null);
        when(secondSource.peek(4)).thenReturn(null);
        when(secondSource.peek(5)).thenReturn(null);

        /* construct sources list */
        final ArrayList<EventSource> sources = new ArrayList<EventSource>();
        sources.add(firstSource);
        sources.add(secondSource);

        /* test EventSourceController */
        EventSourceCollection c = new EventSourceCollection(sources);

        /* verify that events are returned in the expected order */
        assertEquals(one, c.peek(0));
        c.remove(one);
        assertEquals(two, c.peek(1));
        c.remove(two);
        assertEquals(three, c.peek(2));
        c.remove(three);
        assertEquals(four, c.peek(3));
        assertEquals(four, c.peek(4));
        c.remove(four);
        assertEquals(null, c.peek(5));
        
        // verify that both of the sources were polled 5 times
        verify(firstSource).peek(0);
        verify(firstSource).peek(1);
        verify(firstSource).peek(2);
        verify(firstSource).peek(3);
        verify(firstSource).peek(4);
        verify(firstSource).peek(5);
        verify(secondSource).peek(0);
        verify(secondSource).peek(1);
        verify(secondSource).peek(2);
        verify(secondSource).peek(3);
        verify(secondSource).peek(4);
        verify(secondSource).peek(5);

        // verify accept and reject
        verify(firstSource).remove(one);
        verify(firstSource).remove(two);
        verify(firstSource).remove(four);
        verify(secondSource).remove(three);
    }
}
