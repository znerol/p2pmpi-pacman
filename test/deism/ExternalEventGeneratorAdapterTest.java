package deism;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.adapter.ExternalEventGeneratorAdapter;
import deism.core.Event;
import deism.core.EventImporter;
import deism.core.StatefulEventGenerator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExternalEventGeneratorAdapterTest {
    @Mock
    private StatefulEventGenerator generator;
    @Mock
    private EventImporter importer;

    private ExternalEventGeneratorAdapter adapter;

    @Before
    public void setUp() {
        adapter = new ExternalEventGeneratorAdapter(generator, importer);
    }

    @Test
    public void testNullEvent() {
        when(generator.poll()).thenReturn(null);

        Event event = adapter.poll();
        assertEquals(null, event);

        verify(generator).poll();
        verifyNoMoreInteractions(generator);
        verifyNoMoreInteractions(importer);
    }

    @Test
    public void testEventEventFromImporter() {
        Event event = new Event(1);

        when(generator.poll()).thenReturn(event);
        when(importer.unpack(any(Event.class))).thenReturn(null);

        Event result = adapter.poll();
        assertEquals(null, result);

        verify(generator).poll();
        verify(importer).unpack(event);
        verifyNoMoreInteractions(generator);
        verifyNoMoreInteractions(importer);
    }

    @Test
    public void testSomeEvents() {
        final Event one = new Event(1);
        final Event two = new Event(2);

        final Event oneLocal = new Event(3);
        final Event twoLocal = new Event(4);

        when(generator.poll()).thenReturn(one, two, null);
        when(importer.unpack(one)).thenReturn(oneLocal);
        when(importer.unpack(two)).thenReturn(twoLocal);

        Event result;

        result = adapter.poll();
        assertEquals(oneLocal, result);

        result = adapter.poll();
        assertEquals(twoLocal, result);

        verify(generator, times(2)).poll();
        verifyNoMoreInteractions(generator);

        verify(importer).unpack(one);
        verify(importer).unpack(two);
        verifyNoMoreInteractions(importer);
    }
}
