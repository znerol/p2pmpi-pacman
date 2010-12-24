package deism.run;

import java.util.ArrayList;
import java.util.List;

import deism.core.Startable;
import deism.stateful.StateHistory;
import deism.stateful.StateHistoryException;

public class Service implements Startable, StateHistory<Long> {
    private final List<Startable> startableList = new ArrayList<Startable>();
    private final List<StateHistory<Long>> statefulObjects =
        new ArrayList<StateHistory<Long>>();

    public void addStartable(Startable startable) {
        startableList.add(startable);
    }

    public void addStatefulObject(StateHistory<Long> statefulObject) {
        statefulObjects.add(statefulObject);
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
}
