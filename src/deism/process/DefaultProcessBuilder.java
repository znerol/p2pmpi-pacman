package deism.process;

import deism.adapter.EventSourceStatefulGeneratorAdapter;
import deism.adapter.EventSourceStatelessGeneratorAdapter;
import deism.adapter.FilteredEventSink;
import deism.core.EventCondition;
import deism.core.EventDispatcher;
import deism.core.EventSink;
import deism.core.EventSource;
import deism.core.StatefulEventGenerator;
import deism.core.StatelessEventGenerator;
import deism.run.Blocking;
import deism.run.ExecutionGovernor;
import deism.run.Startable;
import deism.run.ThreadedEventSinkRunner;
import deism.run.ThreadedEventSourceRunner;

public class DefaultProcessBuilder {
    private final DefaultDiscreteEventProcess process;
    private final ExecutionGovernor governor;

    public DefaultProcessBuilder(DefaultDiscreteEventProcess process,
            ExecutionGovernor governor) {
        this.process = process;
        this.governor = governor;
    }

    /**
     * Register secondary interfaces of the given object with the process.
     * 
     * @param object
     */
    protected void register(Object object) {
        if (object instanceof Startable) {
            process.addStartable((Startable) object);
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
            result = new ThreadedEventSourceRunner(governor, source);
            register(result);
        }

        return result;
    }

    /**
     * Return an EventSource wrapping the given generator
     */
    protected EventSource adapt(StatefulEventGenerator generator) {
        EventSource result = new EventSourceStatefulGeneratorAdapter(generator);
        register(result);
        return result;
    }

    /**
     * Return an EventSource wrapping the given generator
     */
    protected EventSource adapt(StatelessEventGenerator generator) {
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
        EventSource source = adapt(generator);
        EventSource result = decorate(source, generator);
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
        if (adaptee instanceof Blocking) {
            result = new ThreadedEventSinkRunner(sink);
            register(result);
        }

        return sink;
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
        this.process.addEventSource(source);

        // same as add(EventSink sink) without register
        EventSink sink = decorate((EventSink) process, (EventSink) process);
        this.process.addEventSink(sink);

        // same as add(EventDispatcher dispatcher) without register
        EventDispatcher dispatcher = decorate((EventDispatcher) process,
                (EventDispatcher) process);
        this.process.addEventDispatcher(dispatcher);
    }
}
