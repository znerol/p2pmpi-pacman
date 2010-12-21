package deism.adapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.adapter.FilteredEventSink;
import deism.core.Event;
import deism.core.EventCondition;
import deism.core.EventSink;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FilteredEventSinkTest {
    @Mock
    private EventCondition filter;
    @Mock
    private EventSink sink;
    
    private FilteredEventSink filteredEventSink;

    @Before
    public void setUp() {
        filteredEventSink = new FilteredEventSink(filter, sink);
    }

    @Test
    public void testAllowAllFilter() {
        Event one = new Event(1L);
        Event two = new Event(2L);

        when(filter.match(any(Event.class))).thenReturn(true);

        filteredEventSink.offer(one);
        filteredEventSink.offer(two);
        filteredEventSink.offer(two.inverseEvent());

        verify(filter).match(one);
        verify(filter).match(two);
        verify(filter).match(two.inverseEvent());
        verify(sink).offer(one);
        verify(sink).offer(two);
        verify(sink).offer(two.inverseEvent());

        verifyNoMoreInteractions(filter);
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void testFilterSomeEvents() {
        Event one = new Event(1L);
        Event two = new Event(2L);

        when(filter.match(one)).thenReturn(true);
        when(filter.match(one.inverseEvent())).thenReturn(true);
        when(filter.match(two)).thenReturn(false);
        when(filter.match(two.inverseEvent())).thenReturn(false);

        filteredEventSink.offer(one);
        filteredEventSink.offer(one.inverseEvent());
        filteredEventSink.offer(two);
        filteredEventSink.offer(two.inverseEvent());

        verify(filter).match(one);
        verify(filter).match(one.inverseEvent());
        verify(filter).match(two);
        verify(filter).match(two.inverseEvent());
        verify(sink).offer(one);
        verify(sink).offer(one.inverseEvent());

        verifyNoMoreInteractions(filter);
        verifyNoMoreInteractions(sink);
    }
}
