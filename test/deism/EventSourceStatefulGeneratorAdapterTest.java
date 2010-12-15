package deism;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.adapter.EventSourceStatefulGeneratorAdapter;
import deism.core.Event;
import deism.core.StatefulEventGenerator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventSourceStatefulGeneratorAdapterTest {
    @Mock
    private StatefulEventGenerator generator;
    private EventSourceStatefulGeneratorAdapter source;

    @Before
    public void setUp() {
        source = new EventSourceStatefulGeneratorAdapter(generator);
    }

    @Test
    public void testPeek() {
        Event one = new Event(1L);
        Event two = new Event(2L);

        when(generator.poll()).thenReturn(one, two, null);

        Event result;

        result = source.peek(0);
        assertEquals(one, result);

        result = source.peek(1);
        assertEquals(one, result);

        result = source.peek(2);
        assertEquals(one, result);
    }

    @Test
    public void testPeekRemove() {
        Event one = new Event(1L);
        Event two = new Event(2L);

        when(generator.poll()).thenReturn(one, two, null);

        Event result;

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
