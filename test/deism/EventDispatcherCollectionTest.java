package deism;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventDispatcherCollectionTest {
    @Mock EventDispatcher firstDispatcher;
    @Mock EventDispatcher secondDispatcher;

    @Test
    public void eventDispatcherCollectionDispatchEvent() {
        final Event one = new Event(1);

        /* construct sources list */
        final EventDispatcher[] dispatchers = {
            firstDispatcher,
            secondDispatcher,
        };

        /* test EventSourceController */
        EventDispatcherCollection c = new EventDispatcherCollection(dispatchers);

        c.dispatchEvent(one);
        verify(firstDispatcher).dispatchEvent(one);
        verify(secondDispatcher).dispatchEvent(one);
    }
}