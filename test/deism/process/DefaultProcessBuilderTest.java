package deism.process;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.adapter.EventSourceStatelessGeneratorAdapter;
import deism.adapter.FilteredEventSink;
import deism.core.Event;
import deism.core.EventCondition;
import deism.core.EventDispatcher;
import deism.core.EventSink;
import deism.core.EventSource;
import deism.core.Stateful;
import deism.core.StatefulEventGenerator;
import deism.core.StatelessEventGenerator;
import deism.process.DefaultDiscreteEventProcess;
import deism.process.DefaultProcessBuilder;
import deism.process.DiscreteEventProcess;
import deism.run.Service;
import deism.stateful.TimewarpEventSinkAdapter;
import deism.stateful.TimewarpEventSourceAdapter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultProcessBuilderTest {

    @Mock
    private DefaultDiscreteEventProcess process;
    @Mock
    private Service service;

    private DefaultProcessBuilder builder;

    @Before
    public void setUp() {
        builder = new DefaultProcessBuilder(process, service);
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

        verify(service).register(source);
        verifyNoMoreInteractions(service);
    }

    /**
     * Verify that a stateful but not state aware source is wrapped into a
     * TimewarpEventSourceAdapter before it is added to the process.
     */
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

        verifyNoMoreInteractions(process);

        verify(service).register(source);
        verify(service).register(addedSource);
        verifyNoMoreInteractions(service);
    }

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

        verifyNoMoreInteractions(process);

        // We know the generator and the source but don't have the adapter.
        // So we simply check that the generator and the source got registered
        // and require 3 calls overall.
        verify(service).register(generator);
        verify(service).register(addedSource);
        verify(service, times(3)).register(any());
        verifyNoMoreInteractions(service);
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

        verify(service).register(generator);
        verify(service).register(addedSource);
        verifyNoMoreInteractions(service);
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

        verify(service).register(sink);
        verifyNoMoreInteractions(service);
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

        verify(service).register(sink);
        verify(service).register(addedSink);
        verifyNoMoreInteractions(service);
    }

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

        verifyNoMoreInteractions(process);

        verify(service).register(sink);
        verify(service).register(addedSink);
        verifyNoMoreInteractions(service);
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

        verify(service).register(dispatcher);
        verifyNoMoreInteractions(service);
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

        verify(service).register(dummy);
        verifyNoMoreInteractions(service);
    }

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

        verify(service).register(dummy);

        // source
        ArgumentCaptor<EventSource> sourceArgument =
                ArgumentCaptor.forClass(EventSource.class);
        verify(process).addEventSource(sourceArgument.capture());

        EventSource addedSource = sourceArgument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof TimewarpEventSourceAdapter);
        verify(service).register(addedSource);

        // sink
        ArgumentCaptor<EventSink> sinkArgument =
                ArgumentCaptor.forClass(EventSink.class);
        verify(process).addEventSink(sinkArgument.capture());

        EventSink addedSink = sinkArgument.getValue();
        assertNotNull(addedSink);
        assertTrue(addedSink instanceof TimewarpEventSinkAdapter);
        verify(service).register(addedSink);

        // dispatcher
        verify(process).addEventDispatcher(dummy);

        verifyNoMoreInteractions(process);
        verifyNoMoreInteractions(service);
    }
}
