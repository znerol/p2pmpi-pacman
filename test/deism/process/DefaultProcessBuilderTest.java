package deism.process;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.adapter.EventSourceStatefulGeneratorAdapter;
import deism.adapter.EventSourceStatelessGeneratorAdapter;
import deism.adapter.FilteredEventSink;
import deism.core.Event;
import deism.core.EventCondition;
import deism.core.EventDispatcher;
import deism.core.EventSink;
import deism.core.EventSource;
import deism.core.Startable;
import deism.core.StatefulEventGenerator;
import deism.core.StatelessEventGenerator;
import deism.ipc.base.EventExporter;
import deism.ipc.base.EventImporter;
import deism.process.DefaultDiscreteEventProcess;
import deism.process.DefaultProcessBuilder;
import deism.process.DiscreteEventProcess;
import deism.run.Service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultProcessBuilderTest {

    @Mock
    private DefaultDiscreteEventProcess process;
    @Mock
    private EventImporter importer;
    @Mock
    private EventExporter exporter;
    @Mock
    private Service service;

    private DefaultProcessBuilder builder;

    @Before
    public void setUp() {
        builder = new DefaultProcessBuilder(process, importer, exporter, service);
    }

    @Mock
    private EventSource source;

    // event sources
    @Test
    public void testAddEventSource() {
        builder.add(source);

        verify(process).addEventSource(source);
        verifyNoMoreInteractions(process);
    }

    /**
     * Verify that a startable source will be added once as an EventSource and
     * once as a Startable to the process.
     */
    @Test
    public void testAddStartableSource() {
        // We cannot mock here because we'd loose annotations
        final class StartableSource implements EventSource, Startable {
            @Override
            public Event peek(long currentSimtime) {
                return null;
            }

            @Override
            public void remove(Event event) {
            }

            @Override
            public void start(long simtime) {
            }

            @Override
            public void stop(long simtime) {
            }
        }

        StartableSource startableSource = new StartableSource();
        builder.add(startableSource);

        verify(process).addEventSource(startableSource);
        verify(service).addStartable(startableSource);
        verifyNoMoreInteractions(process);
    }

    @Mock
    private StatefulEventGenerator statefulGenerator;

    @Test
    public void testAddStatefulEventGenerator() {
        builder.add(statefulGenerator);

        ArgumentCaptor<EventSource> argument = ArgumentCaptor
                .forClass(EventSource.class);
        verify(process).addEventSource(argument.capture());

        EventSource addedSource = argument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof EventSourceStatefulGeneratorAdapter);

        verifyNoMoreInteractions(process);
    }

    @Test
    public void testAddStartableStatefulEventGenerator() {
        // We cannot mock here because we'd loose annotations
        final class StartableStatefulEventGenerator implements Startable,
                StatefulEventGenerator {
            @Override
            public Event poll() {
                return null;
            }

            @Override
            public void start(long simtime) {
            }

            @Override
            public void stop(long simtime) {
            }
        }

        StartableStatefulEventGenerator generator = new StartableStatefulEventGenerator();
        builder.add(generator);

        verify(service).addStartable(generator);

        ArgumentCaptor<EventSource> argument = ArgumentCaptor
                .forClass(EventSource.class);
        verify(process).addEventSource(argument.capture());

        EventSource addedSource = argument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof EventSourceStatefulGeneratorAdapter);

        verifyNoMoreInteractions(process);
    }

    @Mock
    private StatelessEventGenerator statelessGenerator;

    @Test
    public void testAddStatelessEventGenerator() {
        builder.add(statelessGenerator);

        ArgumentCaptor<EventSource> argument = ArgumentCaptor
                .forClass(EventSource.class);
        verify(process).addEventSource(argument.capture());

        EventSource addedSource = argument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof EventSourceStatelessGeneratorAdapter);

        verifyNoMoreInteractions(process);
    }

    @Test
    public void testAddStartableStatelessEventGenerator() {
        // We cannot mock here because we'd loose annotations
        final class StartableStatelessEventGenerator implements Startable,
                StatelessEventGenerator {
            @Override
            public Event peek(long simtime) {
                return null;
            }

            @Override
            public void start(long simtime) {
            }

            @Override
            public void stop(long simtime) {
            }
        }

        StartableStatelessEventGenerator generator = new StartableStatelessEventGenerator();
        builder.add(generator);

        verify(service).addStartable(generator);

        ArgumentCaptor<EventSource> argument = ArgumentCaptor
                .forClass(EventSource.class);
        verify(process).addEventSource(argument.capture());

        EventSource addedSource = argument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof EventSourceStatelessGeneratorAdapter);

        verifyNoMoreInteractions(process);
    }

    @Test
    public void testAddEventSink() {
        final class Sink implements EventSink {
            @Override
            public void offer(Event event) {
            }
        }

        Sink sink = new Sink();
        builder.add(sink);

        verify(process).addEventSink(sink);
        verifyNoMoreInteractions(process);
    }

    @Test
    public void testAddEventSinkWithFilter() {
        final class Sink implements EventSink {
            @Override
            public void offer(Event event) {
            }
        }

        final class Condition implements EventCondition {
            @Override
            public boolean match(Event e) {
                return false;
            }
        }

        Sink sink = new Sink();
        Condition condition = new Condition();
        builder.add(sink, condition);

        ArgumentCaptor<EventSink> argument = ArgumentCaptor
                .forClass(EventSink.class);
        verify(process).addEventSink(argument.capture());

        EventSink addedSink = argument.getValue();
        assertNotNull(addedSink);
        assertTrue(addedSink instanceof FilteredEventSink);

        verifyNoMoreInteractions(process);
    }

    @Test
    public void testAddStartableEventSink() {
        final class Sink implements EventSink, Startable {
            @Override
            public void offer(Event event) {
            }

            @Override
            public void start(long simtime) {
            }

            @Override
            public void stop(long simtime) {
            }
        }

        Sink sink = new Sink();
        builder.add(sink);

        verify(process).addEventSink(sink);
        verify(service).addStartable(sink);
        verifyNoMoreInteractions(process);
    }

    @Test
    public void testAddDispatcher() {
        final class Dispatcher implements EventDispatcher {
            @Override
            public void dispatchEvent(Event e) {
            }
        }

        Dispatcher dispatcher = new Dispatcher();
        builder.add(dispatcher);
        verify(process).addEventDispatcher(dispatcher);
        verifyNoMoreInteractions(process);
    }

    @Test
    public void testAddStartableDispatcher() {
        final class Dispatcher implements Startable, EventDispatcher {
            @Override
            public void dispatchEvent(Event e) {
            }

            @Override
            public void start(long simtime) {
            }

            @Override
            public void stop(long simtime) {
            }
        }

        Dispatcher dispatcher = new Dispatcher();
        builder.add(dispatcher);
        verify(process).addEventDispatcher(dispatcher);
        verify(service).addStartable(dispatcher);
        verifyNoMoreInteractions(process);
    }

    @Test
    public void testAddProcess() {
        final class Process implements DiscreteEventProcess {
            @Override
            public void dispatchEvent(Event e) {
            }

            public void offer(Event event) {
            }

            @Override
            public Event peek(long currentSimtime) {
                return null;
            }

            public void remove(Event event) {
            }
        }

        Process dummy = new Process();
        builder.add(dummy);

        verify(process).addEventSource(dummy);
        verify(process).addEventSink(dummy);
        verify(process).addEventDispatcher(dummy);
        verifyNoMoreInteractions(process);
    }

    @Test
    public void testAddStartableProcess() {
        final class Process implements Startable, DiscreteEventProcess {
            @Override
            public void dispatchEvent(Event e) {
            }

            @Override
            public void offer(Event event) {
            }

            @Override
            public Event peek(long currentSimtime) {
                return null;
            }

            @Override
            public void remove(Event event) {
            }

            @Override
            public void start(long simtime) {
            }

            @Override
            public void stop(long simtime) {
            }
        }

        Process dummy = new Process();
        builder.add(dummy);

        verify(process).addEventSource(dummy);
        verify(process).addEventSink(dummy);
        verify(process).addEventDispatcher(dummy);
        verify(service).addStartable(dummy);
        verifyNoMoreInteractions(process);
    }
}
