package deism;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    public void testStartStop() {
        filteredEventSink.start(7L);
        filteredEventSink.stop();

        verify(sink).start(7L);
        verify(sink).stop();

        verifyNoMoreInteractions(filter);
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void testAllowAllFilter() {
        Event one = new Event(1L);
        Event two = new Event(2L);

        when(filter.match(any(Event.class))).thenReturn(true);

        filteredEventSink.offer(one);
        filteredEventSink.offer(two);
        filteredEventSink.remove(two);

        verify(filter).match(one);
        verify(filter, times(2)).match(two);
        verify(sink).offer(one);
        verify(sink).offer(two);
        verify(sink).remove(two);

        verifyNoMoreInteractions(filter);
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void testFilterSomeEvents() {
        Event one = new Event(1L);
        Event two = new Event(2L);

        when(filter.match(one)).thenReturn(true);
        when(filter.match(two)).thenReturn(false);

        filteredEventSink.offer(one);
        filteredEventSink.remove(one);
        filteredEventSink.offer(two);
        filteredEventSink.remove(two);

        verify(filter, times(2)).match(one);
        verify(filter, times(2)).match(two);
        verify(sink).offer(one);
        verify(sink).remove(one);

        verifyNoMoreInteractions(filter);
        verifyNoMoreInteractions(sink);
    }
}
