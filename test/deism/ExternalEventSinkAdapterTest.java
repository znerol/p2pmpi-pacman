package deism;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.adapter.ExternalEventSinkAdapter;
import deism.core.Event;
import deism.core.EventExporter;
import deism.core.EventSink;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExternalEventSinkAdapterTest {
    @Mock
    private EventSink sink;
    @Mock
    private EventExporter exporter;

    private ExternalEventSinkAdapter adapter;

    @Before
    public void setUp() {
        adapter = new ExternalEventSinkAdapter(sink, exporter);
    }

    @Test
    public void testNullEventFromExporter() {
        Event event = new Event(1);

        when(exporter.pack(any(Event.class))).thenReturn(null);

        adapter.offer(event);

        verify(exporter).pack(event);
        verifyNoMoreInteractions(sink);
        verifyNoMoreInteractions(exporter);
    }

    @Test
    public void testSomeEvents() {
        final Event one = new Event(1);
        final Event two = new Event(2);

        final Event oneLocal = new Event(3);
        final Event twoLocal = new Event(4);

        when(exporter.pack(oneLocal)).thenReturn(one);
        when(exporter.pack(twoLocal)).thenReturn(two);

        adapter.offer(oneLocal);
        adapter.offer(twoLocal);

        verify(sink).offer(one);
        verify(sink).offer(two);
        verifyNoMoreInteractions(sink);

        verify(exporter).pack(oneLocal);
        verify(exporter).pack(twoLocal);
        verifyNoMoreInteractions(exporter);
    }
}
