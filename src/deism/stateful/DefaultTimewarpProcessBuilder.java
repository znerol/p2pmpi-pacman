package deism.stateful;

import org.apache.log4j.Logger;

import deism.core.EventSink;
import deism.core.EventSource;
import deism.core.Stateful;
import deism.ipc.base.EventExporter;
import deism.ipc.base.EventImporter;
import deism.process.DefaultProcessBuilder;
import deism.run.Service;
import deism.run.StateHistoryController;

public class DefaultTimewarpProcessBuilder extends DefaultProcessBuilder {
    private DefaultTimewarpDiscreteEventProcess timewarpProcess;
    private final static Logger logger = Logger
            .getLogger(DefaultTimewarpProcessBuilder.class);

    public DefaultTimewarpProcessBuilder(
            DefaultTimewarpDiscreteEventProcess process,
            StateHistoryController stateHistoryController,
            EventImporter importer, EventExporter exporter, Service service) {
        super(process, importer, exporter, service);
        timewarpProcess = process;
        stateHistoryController.setStateObject(process);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void register(Object object) {
        super.register(object);

        if (object instanceof StateHistory<?>) {
            logger.debug("Register state aware " + object);
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
            logger.debug("Decorate stateful " + adaptee
                    + " with timewarp source adapter");
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
            logger.debug("Decorate stateful " + adaptee
                    + " with timewarp sink adapter");
            result = new TimewarpEventSinkAdapter(result);
            register(result);
        }

        return result;
    }
}
