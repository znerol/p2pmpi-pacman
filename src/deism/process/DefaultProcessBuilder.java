package deism.process;

import deism.adapter.EventSourceStatefulGeneratorAdapter;
import deism.adapter.EventSourceStatelessGeneratorAdapter;
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
        final EventSource result;

        if (adaptee instanceof Blocking) {
            register(source);
            result = new ThreadedEventSourceRunner(governor, source);
        }
        else {
            result = source;
        }

        register(result);
        return result;
    }

    /**
     * Return an EventSource wrapping the given generator
     */
    protected EventSource adapt(StatefulEventGenerator generator) {
        register(generator);
        return new EventSourceStatefulGeneratorAdapter(generator);
    }

    /**
     * Return an EventSource wrapping the given generator
     */
    protected EventSource adapt(StatelessEventGenerator generator) {
        register(generator);
        return new EventSourceStatelessGeneratorAdapter(generator);
    }

    /**
     * Add an EventSource to the process, registering supplementary interfaces
     * and wrapping it with helper sources if necessary
     * 
     * @param source
     */
    public void add(EventSource source) {
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
        final EventSink result;

        if (adaptee instanceof Blocking) {
            register(sink);
            result = new ThreadedEventSinkRunner(sink);
        }
        else {
            result = sink;
        }

        register(result);
        return sink;
    }

    /**
     * Add an EventSink to the process, registering supplementary interfaces and
     * decorating it with helper sinks if necessary
     * 
     * @param sink
     */
    public void add(EventSink sink) {
        EventSink result = decorate(sink, sink);
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
        register(dispatcher);
        return dispatcher;
    }

    /**
     * Add an EventDispatcher to the process, registering supplementary
     * interfaces and decorating it with helper sinks if necessary
     * 
     * @param sink
     */
    public void add(EventDispatcher dispatcher) {
        EventDispatcher result = decorate(dispatcher, dispatcher);
        process.addEventDispatcher(result);
    }
}
