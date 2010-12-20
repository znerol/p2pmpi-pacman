package deism;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.adapter.EventSourceStatefulGeneratorAdapter;
import deism.adapter.EventSourceStatelessGeneratorAdapter;
import deism.adapter.FilteredEventSink;
import deism.adapter.ThreadedEventSinkRunner;
import deism.adapter.ThreadedEventSourceRunner;
import deism.core.Blocking;
import deism.core.Event;
import deism.core.EventCondition;
import deism.core.EventDispatcher;
import deism.core.EventExporter;
import deism.core.EventImporter;
import deism.core.EventSink;
import deism.core.EventSource;
import deism.core.Startable;
import deism.core.StatefulEventGenerator;
import deism.core.StatelessEventGenerator;
import deism.process.DefaultDiscreteEventProcess;
import deism.process.DefaultProcessBuilder;
import deism.process.DiscreteEventProcess;

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

    private DefaultProcessBuilder builder;

    @Before
    public void setUp() {
        builder = new DefaultProcessBuilder(process, null, importer, exporter);
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
     * Verify that a blocking source is wrapped into a ThreadedEventSourceRunner
     * and that this runner is added to the process as an EventSource and as a
     * Startable.
     */
    @Test
    public void testAddBlockingSource() {
        // We cannot mock here because we'd loose annotations
        @Blocking
        final class BlockingEventSource implements EventSource {
            @Override
            public Event peek(long currentSimtime) {
                return null;
            }

            @Override
            public void remove(Event event) {
            }
        }

        BlockingEventSource blockingSource = new BlockingEventSource();
        builder.add(blockingSource);

        ArgumentCaptor<EventSource> argument = ArgumentCaptor
                .forClass(EventSource.class);
        verify(process).addEventSource(argument.capture());

        EventSource addedSource = argument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof ThreadedEventSourceRunner);

        verify(process).addStartable((Startable) addedSource);
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
        verify(process).addStartable(startableSource);
        verifyNoMoreInteractions(process);
    }

    /**
     * Verify that a blocking startable source will be wrapped into a
     * ThreadedEventSourceRunner and that it also gets added as startable.
     */
    @Test
    public void testAddBlockingStartableSource() {
        // We cannot mock here because we'd loose annotations
        @Blocking
        final class BlockingStartableSource implements EventSource, Startable {
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

        BlockingStartableSource blockingStartableSource = new BlockingStartableSource();
        builder.add(blockingStartableSource);

        verify(process).addStartable(blockingStartableSource);

        ArgumentCaptor<EventSource> argument = ArgumentCaptor
                .forClass(EventSource.class);
        verify(process).addEventSource(argument.capture());

        EventSource addedSource = argument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof ThreadedEventSourceRunner);

        verify(process).addStartable((Startable) addedSource);
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
    public void testAddBlockingStatefulEventGenerator() {
        // We cannot mock here because we'd loose annotations
        @Blocking
        final class BlockingStatefulEventGenerator implements
                StatefulEventGenerator {
            @Override
            public Event poll() {
                return null;
            }
        }

        BlockingStatefulEventGenerator generator = new BlockingStatefulEventGenerator();
        builder.add(generator);

        ArgumentCaptor<EventSource> argument = ArgumentCaptor
                .forClass(EventSource.class);
        verify(process).addEventSource(argument.capture());

        EventSource addedSource = argument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof ThreadedEventSourceRunner);

        verify(process).addStartable((Startable) addedSource);
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

        verify(process).addStartable(generator);

        ArgumentCaptor<EventSource> argument = ArgumentCaptor
                .forClass(EventSource.class);
        verify(process).addEventSource(argument.capture());

        EventSource addedSource = argument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof EventSourceStatefulGeneratorAdapter);

        verifyNoMoreInteractions(process);
    }

    @Test
    public void testAddBlockingStartableStatefulEventGenerator() {
        // We cannot mock here because we'd loose annotations
        @Blocking
        final class BlockingStartableStatefulEventGenerator implements
                Startable, StatefulEventGenerator {
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

        BlockingStartableStatefulEventGenerator generator = new BlockingStartableStatefulEventGenerator();
        builder.add(generator);

        verify(process).addStartable(generator);

        ArgumentCaptor<EventSource> argument = ArgumentCaptor
                .forClass(EventSource.class);
        verify(process).addEventSource(argument.capture());

        EventSource addedSource = argument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof ThreadedEventSourceRunner);

        verify(process).addStartable((Startable) addedSource);
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
    public void testAddBlockingStatelessEventGenerator() {
        // We cannot mock here because we'd loose annotations
        @Blocking
        final class BlockingStatelessEventGenerator implements
                StatelessEventGenerator {
            @Override
            public Event peek(long simtime) {
                return null;
            }
        }

        BlockingStatelessEventGenerator generator = new BlockingStatelessEventGenerator();
        builder.add(generator);

        ArgumentCaptor<EventSource> argument = ArgumentCaptor
                .forClass(EventSource.class);
        verify(process).addEventSource(argument.capture());

        EventSource addedSource = argument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof ThreadedEventSourceRunner);

        verify(process).addStartable((Startable) addedSource);
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

        verify(process).addStartable(generator);

        ArgumentCaptor<EventSource> argument = ArgumentCaptor
                .forClass(EventSource.class);
        verify(process).addEventSource(argument.capture());

        EventSource addedSource = argument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof EventSourceStatelessGeneratorAdapter);

        verifyNoMoreInteractions(process);
    }

    @Test
    public void testAddBlockingStartableStatelessEventGenerator() {
        // We cannot mock here because we'd loose annotations
        @Blocking
        final class BlockingStartableStatelessEventGenerator implements
                Startable, StatelessEventGenerator {
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

        BlockingStartableStatelessEventGenerator generator = new BlockingStartableStatelessEventGenerator();
        builder.add(generator);

        verify(process).addStartable(generator);

        ArgumentCaptor<EventSource> argument = ArgumentCaptor
                .forClass(EventSource.class);
        verify(process).addEventSource(argument.capture());

        EventSource addedSource = argument.getValue();
        assertNotNull(addedSource);
        assertTrue(addedSource instanceof ThreadedEventSourceRunner);

        verify(process).addStartable((Startable) addedSource);
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
    public void testAddBlockingEventSink() {
        @Blocking
        final class Sink implements EventSink {
            @Override
            public void offer(Event event) {
            }
        }

        Sink sink = new Sink();
        builder.add(sink);

        ArgumentCaptor<EventSink> argument = ArgumentCaptor
                .forClass(EventSink.class);
        verify(process).addEventSink(argument.capture());

        EventSink addedSink = argument.getValue();
        assertNotNull(addedSink);
        assertTrue(addedSink instanceof ThreadedEventSinkRunner);

        verify(process).addStartable((Startable) addedSink);
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
        verify(process).addStartable(sink);
        verifyNoMoreInteractions(process);
    }

    @Test
    public void testAddBlockingStartableEventSink() {
        @Blocking
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

        verify(process).addStartable(sink);

        ArgumentCaptor<EventSink> argument = ArgumentCaptor
                .forClass(EventSink.class);
        verify(process).addEventSink(argument.capture());

        EventSink addedSink = argument.getValue();
        assertNotNull(addedSink);
        assertTrue(addedSink instanceof ThreadedEventSinkRunner);

        verify(process).addStartable((Startable) addedSink);
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
        verify(process).addStartable(dispatcher);
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
        verify(process).addStartable(dummy);
        verifyNoMoreInteractions(process);
    }
}
