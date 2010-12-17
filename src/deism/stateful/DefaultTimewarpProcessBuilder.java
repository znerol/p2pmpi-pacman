package deism.stateful;

import deism.core.EventSink;
import deism.core.EventSource;
import deism.core.Stateful;
import deism.process.DefaultProcessBuilder;
import deism.run.ExecutionGovernor;

public class DefaultTimewarpProcessBuilder extends DefaultProcessBuilder {
    DefaultTimewarpDiscreteEventProcess timewarpProcess;

    public DefaultTimewarpProcessBuilder(
            DefaultTimewarpDiscreteEventProcess process,
            ExecutionGovernor governor) {
        super(process, governor);
        timewarpProcess = process;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void register(Object object) {
        super.register(object);

        if (object instanceof StateHistory<?>) {
            timewarpProcess.addStatefulObject((StateHistory<Long>) object);
        }
    }

    /**
     * Adapt the given EventSource according to the properties of the adaptee.
     * If the adaptee does not record the state history itself, wrap it into a
     * TimewarpEventSourceAdapter.
     * 
     * @param source
     *            the event source to decorate if necessary
     * @param adaptee
     *            the original object, possibly equal to source
     * @return
     */
    @Override
    protected EventSource decorate(EventSource source, Object adaptee) {
        EventSource result = super.decorate(source, adaptee);

        if (!(adaptee instanceof StateHistory<?>)
                && (adaptee.getClass().isAnnotationPresent(Stateful.class))) {
            result = new TimewarpEventSourceAdapter(result);
            register(result);
        }

        return result;
    }

    /**
     * Adapt the given EventSink according to the properties of the adaptee. If
     * the adaptee does not record the state history itself, wrap it into a
     * TimewarpEventSourceAdapter.
     * 
     * @param source
     *            the event source to decorate if necessary
     * @param adaptee
     *            the original object, possibly equal to source
     * @return
     */
    protected EventSink decorate(EventSink sink, Object adaptee) {
        EventSink result = super.decorate(sink, sink);

        if (!(adaptee instanceof StateHistory<?>)
                && (adaptee.getClass().isAnnotationPresent(Stateful.class))) {
            result = new TimewarpEventSinkAdapter(result);
            register(result);
        }

        return result;
    }
}
