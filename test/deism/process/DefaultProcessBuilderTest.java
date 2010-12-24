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
import deism.core.Flushable;
import deism.core.Startable;
import deism.core.Stateful;
import deism.core.StatefulEventGenerator;
import deism.core.StatelessEventGenerator;
import deism.ipc.base.EventExporter;
import deism.ipc.base.EventImporter;
import deism.process.DefaultDiscreteEventProcess;
import deism.process.DefaultProcessBuilder;
import deism.process.DiscreteEventProcess;
import deism.run.Service;
import deism.stateful.StateHistory;
import deism.stateful.StateHistoryException;
import deism.stateful.TimewarpEventSinkAdapter;
import deism.stateful.TimewarpEventSourceAdapter;

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

    @Test
    public void testAddEventSource() {
        final class Source implements EventSource {
            @Override
            public Event peek(long currentSimtime) {
                return null;
            }

            @Override
            public void remove(Event event) {
            }
        }

        Source source = new Source();
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

    @Test
    public void testAddStateAwareEventSource() {
        final class Source implements EventSource, StateHistory<Long> {
            @Override
            public Event peek(long currentSimtime) {
                return null;
            }

            @Override
            public void remove(Event event) {
            }

            @Override
            public void save(Long key) throws StateHistoryException {
            }

            @Override
            public void commit(Long key) throws StateHistoryException {
            }

            @Override
            public void rollback(Long key) throws StateHistoryException {
            }
        }

        Source source = new Source();
        builder.add(source);

        verify(process).addEventSource(source);
        verify(service).addStatefulObject(source);
        verifyNoMoreInteractions(process);
    }

    /**
     * Verify that a stateful but not state aware source is wrapped into a
     * TimewarpEventSourceAdapter before it is added to the process.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testAddStatefulSource() {
        // We cannot mock here because we'd loose annotations
        @Stateful
        final class Source implements EventSource {
            @Override
            public Event peek(long currentSimtime) {
                return null;
            }

            @Override
            public void remove(Event event) {
            }
        }

        Source source = new Source();
        builder.add(source);

        ArgumentCaptor<EventSource> argument =
                ArgumentCaptor.forClass(EventSource.class);
        verify(process).addEventSource(argument.capture());

        EventSource addedSource = argument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof TimewarpEventSourceAdapter);

        verify(service).addStatefulObject((StateHistory<Long>) addedSource);
        verifyNoMoreInteractions(process);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddStatefulEventGenerator() {
        @Stateful
        final class Generator implements StatefulEventGenerator {
            @Override
            public Event poll() {
                return null;
            }
        }

        Generator generator = new Generator();
        builder.add(generator);

        ArgumentCaptor<EventSource> argument =
                ArgumentCaptor.forClass(EventSource.class);
        verify(process).addEventSource(argument.capture());

        EventSource addedSource = argument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof TimewarpEventSourceAdapter);

        verify(service).addStatefulObject((StateHistory<Long>) addedSource);
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

    @Test
    public void testAddStatelessEventGenerator() {
        final class Generator implements StatelessEventGenerator {
            @Override
            public Event peek(long simtime) {
                return null;
            }
        }

        Generator generator = new Generator();
        builder.add(generator);

        ArgumentCaptor<EventSource> argument =
                ArgumentCaptor.forClass(EventSource.class);
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

    @SuppressWarnings("unchecked")
    @Test
    public void testAddStatefulEventSink() {
        @Stateful
        final class Sink implements EventSink {
            @Override
            public void offer(Event event) {
            }
        }

        Sink sink = new Sink();
        builder.add(sink);

        ArgumentCaptor<EventSink> argument =
                ArgumentCaptor.forClass(EventSink.class);
        verify(process).addEventSink(argument.capture());

        EventSink addedSink = argument.getValue();
        assertNotNull(addedSink);
        assertTrue(addedSink instanceof TimewarpEventSinkAdapter);

        verify(service).addStatefulObject((StateHistory<Long>) addedSink);
        verify(process).addFlushable((Flushable) addedSink);
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

    @Test
    public void testStateAwareProcess() {
        @Stateful
        final class Process implements DiscreteEventProcess, StateHistory<Long> {

            @Override
            public Event peek(long currentSimtime) {
                return null;
            }

            @Override
            public void remove(Event event) {
            }

            @Override
            public void offer(Event event) {
            }

            @Override
            public void dispatchEvent(Event e) {
            }

            @Override
            public void save(Long key) throws StateHistoryException {
            }

            @Override
            public void commit(Long key) throws StateHistoryException {
            }

            @Override
            public void rollback(Long key) throws StateHistoryException {
            }
        }

        Process dummy = new Process();
        builder.add(dummy);

        verify(process).addEventSource(dummy);
        verify(process).addEventSink(dummy);
        verify(process).addEventDispatcher(dummy);
        verify(service).addStatefulObject(dummy);
        verifyNoMoreInteractions(process);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddStatefulProcess() {
        @Stateful
        final class Process implements DiscreteEventProcess {
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
        }

        Process dummy = new Process();
        builder.add(dummy);

        // source
        ArgumentCaptor<EventSource> sourceArgument =
                ArgumentCaptor.forClass(EventSource.class);
        verify(process).addEventSource(sourceArgument.capture());

        EventSource addedSource = sourceArgument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof TimewarpEventSourceAdapter);
        verify(service).addStatefulObject((StateHistory<Long>) addedSource);

        // sink
        ArgumentCaptor<EventSink> sinkArgument =
                ArgumentCaptor.forClass(EventSink.class);
        verify(process).addEventSink(sinkArgument.capture());

        EventSink addedSink = sinkArgument.getValue();
        assertNotNull(addedSink);
        assertTrue(addedSink instanceof TimewarpEventSinkAdapter);
        verify(service).addStatefulObject((StateHistory<Long>) addedSink);
        verify(process).addFlushable((Flushable) addedSink);

        // dispatcher
        verify(process).addEventDispatcher(dummy);

        verifyNoMoreInteractions(process);
    }
}
