package deism.adapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.adapter.EventSourceStatelessGeneratorAdapter;
import deism.core.Event;
import deism.core.StatelessEventGenerator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventSourceStatelessGeneratorAdapterTest {
    @Mock
    private StatelessEventGenerator generator;
    private EventSourceStatelessGeneratorAdapter source;

    @Before
    public void setUp() {
        source = new EventSourceStatelessGeneratorAdapter(generator);
    }

    @Test
    public void testPeek() {
        Event one = new Event(1L);
        Event two = new Event(2L);

        when(generator.peek(0L)).thenReturn(one);
        when(generator.peek(1L)).thenReturn(two);
        when(generator.peek(2L)).thenReturn(null);

        Event result;

        result = source.peek(0);
        assertEquals(one, result);

        result = source.peek(1);
        assertEquals(two, result);

        result = source.peek(2);
        assertEquals(null, result);
    }

    @Test
    public void testPeekRemove() {
        Event one = new Event(1L);
        Event two = new Event(2L);

        when(generator.peek(0L)).thenReturn(one);
        when(generator.peek(1L)).thenReturn(two);
        when(generator.peek(2L)).thenReturn(null);

        Event result;

        // adapter must return the same results when remove is used
        result = source.peek(0);
        assertEquals(one, result);
        source.remove(result);

        result = source.peek(1);
        assertEquals(two, result);
        source.remove(result);

        result = source.peek(2);
        assertEquals(null, result);

        // even when called multiple times
        result = source.peek(0);
        assertEquals(one, result);
        source.remove(result);

        result = source.peek(1);
        assertEquals(two, result);
        source.remove(result);

        result = source.peek(2);
        assertEquals(null, result);
    }
}
