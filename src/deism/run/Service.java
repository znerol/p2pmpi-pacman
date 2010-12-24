package deism.run;

import java.util.ArrayList;
import java.util.List;

import deism.core.Event;
import deism.core.Startable;
import deism.ipc.base.EventExporter;
import deism.ipc.base.EventImporter;
import deism.stateful.StateHistory;
import deism.stateful.StateHistoryException;

public class Service implements Startable, StateHistory<Long>, EventImporter,
        EventExporter, LvtListener {

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

    private static final LvtListener NULL_LVT_LISTENER = new LvtListener() {
        @Override
        public void update(long lvt) {
        }
    };

    private final List<Startable> startableList = new ArrayList<Startable>();
    private final List<StateHistory<Long>> statefulObjects =
            new ArrayList<StateHistory<Long>>();
    private EventImporter eventImporter = NULL_IMPORTER;
    private EventExporter eventExporter = NULL_EXPORTER;
    private LvtListener lvtListener = NULL_LVT_LISTENER;

    public void addStartable(Startable startable) {
        startableList.add(startable);
    }

    public void addStatefulObject(StateHistory<Long> statefulObject) {
        statefulObjects.add(statefulObject);
    }

    public void setEventImporter(EventImporter eventImporter) {
        this.eventImporter = eventImporter;
    }

    public void setEventExporter(EventExporter eventExporter) {
        this.eventExporter = eventExporter;
    }

    public void setLvtListener(LvtListener lvtListener) {
        this.lvtListener = lvtListener;
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
    public Event unpack(Event event) {
        return eventImporter.unpack(event);
    }

    @Override
    public Event pack(Event event) {
        return eventExporter.pack(event);
    }

    @Override
    public void update(long lvt) {
        lvtListener.update(lvt);
    }
}
