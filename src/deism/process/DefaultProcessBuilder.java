package deism.process;

import org.apache.log4j.Logger;

import deism.adapter.EventSourceStatefulGeneratorAdapter;
import deism.adapter.EventSourceStatelessGeneratorAdapter;
import deism.adapter.ExternalEventGeneratorAdapter;
import deism.adapter.ExternalEventSinkAdapter;
import deism.adapter.FilteredEventDispatcher;
import deism.adapter.FilteredEventSink;
import deism.core.EventCondition;
import deism.core.EventDispatcher;
import deism.core.EventSink;
import deism.core.EventSource;
import deism.core.External;
import deism.core.Stateful;
import deism.core.StatefulEventGenerator;
import deism.core.StatelessEventGenerator;
import deism.run.Service;
import deism.stateful.StateHistory;
import deism.stateful.TimewarpEventSinkAdapter;
import deism.stateful.TimewarpEventSourceAdapter;

/**
 * Builder class for constructing {@link DiscreteEventProcess} easily.
 */
public class DefaultProcessBuilder {
    private final DefaultDiscreteEventProcess process;
    private final Service service;
    private final static Logger logger = Logger
            .getLogger(DefaultProcessBuilder.class);

    public DefaultProcessBuilder(Service service) {
        this(new DefaultDiscreteEventProcess(), service);
    }

    public DefaultProcessBuilder(DefaultDiscreteEventProcess process,
            Service service) {
        this.process = process;
        this.service = service;
    }

    /**
     * Return built process
     */
    public DiscreteEventProcess getProcess() {
        return process;
    }

    /**
     * Decorate the given EventSource according to the properties of the
     * adaptee. If the adaptee does not record the state history itself, wrap it
     * into a TimewarpEventSourceAdapter.
     * 
     * @param source
     *            the event source to decorate if necessary
     * @param adaptee
     *            the original object, possibly equal to source
     * @return decorated EventSource
     */
    protected EventSource decorate(EventSource source, Object adaptee) {
        EventSource result = source;

        if (!(adaptee instanceof StateHistory<?>)
                && (adaptee.getClass().isAnnotationPresent(Stateful.class))) {
            logger.debug("Decorate stateful " + adaptee
                    + " with timewarp source adapter");
            result = new TimewarpEventSourceAdapter(result);
            service.register(result);
        }

        return result;
    }

    /**
     * Adapt a StatefulEventGenerator to an EventSource
     * 
     * @return EventSourceStatefulGeneratorAdapter wrapping the given generator
     */
    protected EventSource adapt(StatefulEventGenerator generator) {
        logger.debug("Adapt " + generator + " to EventSource");
        EventSource result = new EventSourceStatefulGeneratorAdapter(generator);
        service.register(result);
        return result;
    }

    /**
     * Decorate the given generator with an ExternalEventGeneratorAdapter if
     * 
     * {@link deism.core.External} annotation is present on the original object.
     * 
     * @param generator
     *            the event generator to decorate if necessary
     * @param adaptee
     *            the original object, possibly equal to generator
     * @return decorated generator
     */
    protected StatefulEventGenerator decorate(StatefulEventGenerator generator,
            Object adaptee) {
        StatefulEventGenerator result = generator;
        if (adaptee.getClass().isAnnotationPresent(External.class)) {
            logger.debug("Decorate external " + adaptee + " with importer");
            result = new ExternalEventGeneratorAdapter(generator, service);
            service.register(result);
        }

        return result;
    }

    /**
     * Adapt a StatelessEventGenerator to an EventSource
     * 
     * @return EventSourceStatelessGeneratorAdapter wrapping the given generator
     */
    protected EventSource adapt(StatelessEventGenerator generator) {
        logger.debug("Adapt " + generator + " to EventSource");
        EventSource result =
                new EventSourceStatelessGeneratorAdapter(generator);
        service.register(result);
        return result;
    }

    /**
     * Add an EventSource to the process, register it with the service and
     * decorate it with helper sources if necessary.
     * 
     * @param source
     *            EventSource which should be added to the process
     */
    public void add(EventSource source) {
        service.register(source);
        EventSource result = decorate(source, source);
        logger.debug("Add EventSource " + result);
        process.addEventSource(result);
    }

    /**
     * Add a StatefulEventGenerator to the process, register it with the
     * service, and decorate it with helper sources if necessary.
     * 
     * @param generator
     *            StatefulEventGenerator which should be added to the process
     */
    public void add(StatefulEventGenerator generator) {
        service.register(generator);
        EventSource source = adapt(decorate(generator, generator));
        EventSource result = decorate(source, generator);
        logger.debug("Add EventSource " + result);
        process.addEventSource(result);
    }

    /**
     * Add a StatelessEventGenerator to the process, register it with the
     * service, and decorate it with helper sources if necessary.
     * 
     * @param generator
     *            StatelessEventGenerator which should be added to the process
     */
    public void add(StatelessEventGenerator generator) {
        service.register(generator);
        EventSource source = adapt(generator);
        EventSource result = decorate(source, generator);
        logger.debug("Add EventSource " + result);
        process.addEventSource(result);
    }

    /**
     * Decorate the given EventSink according to the properties of the adaptee.
     * If the {@link External} annotation is present on the adaptee parameter,
     * wrap it into an {@link ExternalEventSinkAdapter}. Further if the adaptee
     * does not record the state history itself, wrap it into a
     * {@link TimewarpEventSinkAdapter}.
     * 
     * @param sink
     *            the event sink to decorate if necessary
     * @param adaptee
     *            the original object, possibly the same as the sink
     * @return decorated sink
     */

    protected EventSink decorate(EventSink sink, Object adaptee) {
        EventSink result = sink;

        if (adaptee.getClass().isAnnotationPresent(External.class)) {
            logger.debug("Decorate external " + adaptee + " with exporter");
            result = new ExternalEventSinkAdapter(result, service);
            service.register(result);
        }

        if (!(adaptee instanceof StateHistory<?>)
                && (adaptee.getClass().isAnnotationPresent(Stateful.class))) {
            logger.debug("Decorate stateful " + adaptee
                    + " with timewarp sink adapter");
            result = new TimewarpEventSinkAdapter(result);
            service.register(result);
        }

        return result;
    }

    /**
     * Add an EventSink to the process, register it with the service and
     * decorate it with helper sinks if necessary
     * 
     * @param sink
     *            EventSink which should be added to the process
     */
    public void add(EventSink sink) {
        add(sink, null);
    }

    /**
     * Add an EventSink with a filter to the process, register it with the
     * service and decorate it with helper sinks if necessary
     * 
     * @param sink
     *            EventSink which should be added to the process
     * @param filter
     *            Only deliver events matched by EventCondition to the event
     *            sink
     */
    public void add(EventSink sink, EventCondition filter) {
        service.register(sink);
        EventSink result = decorate(sink, sink);

        if (filter != null) {
            result = new FilteredEventSink(filter, result);
            service.register(result);
            logger.debug("Add EventSink " + result + " with filter " + filter);
        }
        else {
            logger.debug("Add EventSink " + result);
        }

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
     * @return decorated dispatcher
     */
    protected EventDispatcher decorate(EventDispatcher dispatcher,
            Object adaptee) {
        return dispatcher;
    }

    /**
     * Add an EventDispatcher to the process, register it with the service and
     * decorate it with helper sinks if necessary
     * 
     * @param dispatcher
     *            EventDispatcher which should be added to the process
     */
    public void add(EventDispatcher dispatcher) {
        add(dispatcher, null);
    }

    /**
     * Add an EventDispatcher with a filter to the process, register it with the
     * service and decorate it with helper sinks if necessary
     * 
     * @param dispatcher
     *            EventDispatcher which should be added to the process
     * @param filter
     *            Only deliver events matched by EventCondition to the
     *            dispatcher
     */
    public void add(EventDispatcher dispatcher, EventCondition filter) {
        service.register(dispatcher);
        EventDispatcher result = decorate(dispatcher, dispatcher);

        if (filter != null) {
            result = new FilteredEventDispatcher(filter, result);
            service.register(result);
            logger.debug("Add EventDispatcher " + result + " with filter "
                    + filter);
        }
        else {
            logger.debug("Add EventDispatcher " + result);
        }

        process.addEventDispatcher(result);
    }

    /**
     * Add a child process, register it with the service and decorate its
     * source, sink and dispatcher if necessary.
     * 
     * @param process
     *            child process
     */
    public void add(DiscreteEventProcess process) {
        service.register(process);

        // same as add(EventSource source) without register
        EventSource source =
                decorate((EventSource) process, (EventSource) process);
        logger.debug("Add EventSource " + source);
        this.process.addEventSource(source);

        // same as add(EventSink sink) without register
        EventSink sink = decorate((EventSink) process, (EventSink) process);
        logger.debug("Add EventSink " + sink);
        this.process.addEventSink(sink);

        // same as add(EventDispatcher dispatcher) without register
        EventDispatcher dispatcher =
                decorate((EventDispatcher) process, (EventDispatcher) process);
        logger.debug("Add EventDispatcher " + sink);
        this.process.addEventDispatcher(dispatcher);
    }
}
