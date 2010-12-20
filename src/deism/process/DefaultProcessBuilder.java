package deism.process;

import org.apache.log4j.Logger;

import deism.adapter.EventSourceStatefulGeneratorAdapter;
import deism.adapter.EventSourceStatelessGeneratorAdapter;
import deism.adapter.ExternalEventGeneratorAdapter;
import deism.adapter.ExternalEventSinkAdapter;
import deism.adapter.FilteredEventSink;
import deism.adapter.ThreadedEventSinkRunner;
import deism.adapter.ThreadedEventSourceRunner;
import deism.core.Blocking;
import deism.core.EventCondition;
import deism.core.EventDispatcher;
import deism.core.EventExporter;
import deism.core.EventImporter;
import deism.core.EventSink;
import deism.core.EventSource;
import deism.core.External;
import deism.core.Flushable;
import deism.core.Startable;
import deism.core.StatefulEventGenerator;
import deism.core.StatelessEventGenerator;
import deism.run.ExecutionGovernor;

public class DefaultProcessBuilder {
    private final DefaultDiscreteEventProcess process;
    private final ExecutionGovernor governor;
    private final EventImporter importer;
    private final EventExporter exporter;
    private final static Logger logger = Logger.getLogger(DefaultProcessBuilder.class);

    public DefaultProcessBuilder(DefaultDiscreteEventProcess process,
            ExecutionGovernor governor, EventImporter importer, EventExporter exporter) {
        this.process = process;
        this.governor = governor;
        this.importer = importer;
        this.exporter = exporter;
    }

    /**
     * Register secondary interfaces of the given object with the process.
     * 
     * @param object
     */
    protected void register(Object object) {
        if (object instanceof Startable) {
            logger.debug("Register startable " + object);
            process.addStartable((Startable) object);
        }
        if (object instanceof Flushable) {
            logger.debug("Register flushable " + object);
            process.addFlushable((Flushable) object);
        }
    }

    /**
     * Adapt the given EventSource according to the properties of the adaptee.
     * If the adaptee is Blocking, a ThreadedEventSourceRunner is wrapped around
     * the source.
     * 
     * @param source
     *            the event source to decorate if necessary
     * @param adaptee
     *            the original object, possibly equal to source
     * @return
     */
    protected EventSource decorate(EventSource source, Object adaptee) {
        EventSource result = source;
        if (adaptee.getClass().isAnnotationPresent(Blocking.class)) {
            logger.debug("Decorate blocking " + adaptee + " with worker thread");
            result = new ThreadedEventSourceRunner(governor, source);
            register(result);
        }

        return result;
    }

    /**
     * Return an EventSource wrapping the given generator
     */
    protected EventSource adapt(StatefulEventGenerator generator) {
        logger.debug("Adapt " + generator + " to EventSource");
        EventSource result = new EventSourceStatefulGeneratorAdapter(generator);
        register(result);
        return result;
    }

    /**
     * Wrap the given generator into an ExternalEventGeneratorAdapter registered
     * with the builders importer if @External annotation is present on adaptee.
     */
    protected StatefulEventGenerator decorate(StatefulEventGenerator generator, Object adaptee) {
        StatefulEventGenerator result = generator;
        if (adaptee.getClass().isAnnotationPresent(External.class)) {
            logger.debug("Decorate external " + adaptee + " with importer");
            result = new ExternalEventGeneratorAdapter(generator, importer);
            register(result);
        }

        return result;
    }

    /**
     * Return an EventSource wrapping the given generator
     */
    protected EventSource adapt(StatelessEventGenerator generator) {
        logger.debug("Adapt " + generator + " to EventSource");
        EventSource result = new EventSourceStatelessGeneratorAdapter(generator);
        register(result);
        return result;
    }

    /**
     * Add an EventSource to the process, registering supplementary interfaces
     * and wrapping it with helper sources if necessary
     * 
     * @param source
     */
    public void add(EventSource source) {
        register(source);
        EventSource result = decorate(source, source);
        logger.debug("Add EventSource " + result);
        process.addEventSource(result);
    }

    /**
     * Add a generator to the process, registering supplementary interfaces and
     * wrapping it with helper sources.
     * 
     * @param source
     */
    public void add(StatefulEventGenerator generator) {
        register(generator);
        EventSource source = adapt(decorate(generator, generator));
        EventSource result = decorate(source, generator);
        logger.debug("Add EventSource " + result);
        process.addEventSource(result);
    }

    /**
     * Add a generator to the process, registering supplementary interfaces and
     * wrapping it with helper sources.
     * 
     * @param source
     */
    public void add(StatelessEventGenerator generator) {
        register(generator);
        EventSource source = adapt(generator);
        EventSource result = decorate(source, generator);
        logger.debug("Add EventSource " + result);
        process.addEventSource(result);
    }

    /**
     * Adapt the given EventSink according to the properties of the adaptee. If
     * the adaptee is Blocking, a ThreadedEventSinkRunner is wrapped around the
     * source.
     * 
     * @param source
     *            the event source to decorate if necessary
     * @param adaptee
     *            the original object, possibly equal to source
     * @return
     */
    protected EventSink decorate(EventSink sink, Object adaptee) {
        EventSink result = sink;

        if (adaptee.getClass().isAnnotationPresent(External.class)) {
            logger.debug("Decorate external " + adaptee + " with exporter");
            result = new ExternalEventSinkAdapter(result, exporter);
            register(result);
        }

        if (adaptee.getClass().isAnnotationPresent(Blocking.class)) {
            logger.debug("Decorate blocking " + adaptee + " with worker thread");
            result = new ThreadedEventSinkRunner(result);
            register(result);
        }

        return result;
    }

    /**
     * Add an EventSink to the process, registering supplementary interfaces and
     * decorating it with helper sinks if necessary
     * 
     * @param sink
     */
    public void add(EventSink sink) {
        register(sink);
        EventSink result = decorate(sink, sink);
        logger.debug("Add EventSink " + result);
        process.addEventSink(result);
    }

    /**
     * Add an EventSink to the process with the given filter.
     * 
     * @param sink
     */
    public void add(EventSink sink, EventCondition filter) {
        register(sink);
        EventSink result = decorate(sink, sink);
        result = new FilteredEventSink(filter, result);
        register(result);
        logger.debug("Add EventSink " + result + " with filter " + filter);
        process.addEventSink(result);
    }

    /**
     * Adapt the given EventDispatcher according to the properties of the
     * adaptee.
     * 
     * @param dispatcher
     *            the event source to decorate if necessary
     * @param adaptee
     *            the original object, possibly equal to source
     * @return
     */
    protected EventDispatcher decorate(EventDispatcher dispatcher,
            Object adaptee) {
        return dispatcher;
    }

    /**
     * Add an EventDispatcher to the process, registering supplementary
     * interfaces and decorating it with helper sinks if necessary
     * 
     * @param sink
     */
    public void add(EventDispatcher dispatcher) {
        register(dispatcher);
        EventDispatcher result = decorate(dispatcher, dispatcher);
        logger.debug("Add EventDispatcher " + result);
        process.addEventDispatcher(result);
    }

    /**
     * Add a child process, registering supplementary interfaces and decorating
     * it if necessary.
     *
     * @param process child process
     */
    public void add(DiscreteEventProcess process) {
        register(process);

        // same as add(EventSource source) without register
        EventSource source = decorate((EventSource) process,
                (EventSource) process);
        logger.debug("Add EventSource " + source);
        this.process.addEventSource(source);

        // same as add(EventSink sink) without register
        EventSink sink = decorate((EventSink) process, (EventSink) process);
        logger.debug("Add EventSink " + sink);
        this.process.addEventSink(sink);

        // same as add(EventDispatcher dispatcher) without register
        EventDispatcher dispatcher = decorate((EventDispatcher) process,
                (EventDispatcher) process);
        logger.debug("Add EventDispatcher " + sink);
        this.process.addEventDispatcher(dispatcher);
    }
}
