package deism.run;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import deism.core.Event;
import deism.core.Flushable;
import deism.core.Startable;
import deism.ipc.base.EventExporter;
import deism.ipc.base.EventImporter;
import deism.stateful.StateHistory;
import deism.stateful.StateHistoryException;

/**
 * Supporting services for the runloop.
 */
public class Service implements Startable, StateHistory<Long>, Flushable,
        EventImporter, EventExporter, LvtListener {

    private static final EventImporter NULL_IMPORTER = new EventImporter() {
        @Override
        public Event unpack(Event event) {
            return event;
        }
    };

    private static final EventExporter NULL_EXPORTER = new EventExporter() {
        @Override
        public Event pack(Event event) {
            return event;
        }
    };

    private final List<Startable> startableList = new ArrayList<Startable>();
    private final List<StateHistory<Long>> statefulObjects =
            new ArrayList<StateHistory<Long>>();
    private final List<Flushable> flushables = new ArrayList<Flushable>();
    private final List<LvtListener> lvtListeners = new ArrayList<LvtListener>();
    private EventImporter eventImporter = NULL_IMPORTER;
    private EventExporter eventExporter = NULL_EXPORTER;
    private final static Logger logger = Logger.getLogger(Service.class);

    /**
     * Add the given object as an observer to every possible subject known to
     * the service class
     * 
     * @param object
     */
    @SuppressWarnings("unchecked")
    public void register(Object object) {
        if (object instanceof Startable) {
            logger.debug("Register startable " + object);
            startableList.add((Startable) object);
        }
        if (object instanceof Flushable) {
            logger.debug("Register flushable " + object);
            flushables.add((Flushable) object);
        }
        if (object instanceof StateHistory<?>) {
            logger.debug("Register state aware " + object);
            statefulObjects.add((StateHistory<Long>) object);
        }
        if (object instanceof LvtListener) {
            logger.debug("Register lvt listener " + object);
            lvtListeners.add((LvtListener) object);
        }
        if (object instanceof EventImporter) {
            logger.debug("Set event importer " + object);
            this.eventImporter = (EventImporter) object;
        }
        if (object instanceof EventExporter) {
            logger.debug("Set event exporter " + object);
            this.eventExporter = (EventExporter) object;
        }
    }

    @Override
    public void start(long simtime) {
        for (Startable startable : startableList) {
            startable.start(simtime);
        }
    }

    @Override
    public void stop(long simtime) {
        for (Startable startable : startableList) {
            startable.stop(simtime);
        }
    }

    @Override
    public void join() {
        for (Startable startable : startableList) {
            startable.join();
        }
    }

    @Override
    public void save(Long key) throws StateHistoryException {
        for (StateHistory<Long> stateObject : statefulObjects) {
            stateObject.save(key);
        }
    }

    @Override
    public void commit(Long key) throws StateHistoryException {
        for (StateHistory<Long> stateObject : statefulObjects) {
            stateObject.commit(key);
        }
    }

    @Override
    public void rollback(Long key) throws StateHistoryException {
        for (StateHistory<Long> stateObject : statefulObjects) {
            stateObject.rollback(key);
        }
    }

    @Override
    public void flush(long simtime) {
        for (Flushable flushable : flushables) {
            flushable.flush(simtime);
        }
    }

    @Override
    public void update(long lvt) {
        for (LvtListener lvtListener : lvtListeners) {
            lvtListener.update(lvt);
        }
    }

    @Override
    public Event unpack(Event event) {
        return eventImporter.unpack(event);
    }

    @Override
    public Event pack(Event event) {
        return eventExporter.pack(event);
    }
}
