package deism.stateful;

import java.util.ArrayList;
import java.util.List;

import deism.process.DefaultDiscreteEventProcess;

public class DefaultTimewarpDiscreteEventProcess extends
        DefaultDiscreteEventProcess implements TimewarpDiscreteEventProcess {
    private final List<StateHistory<Long>> statefulObjects =
        new ArrayList<StateHistory<Long>>();

    public void addStatefulObject(StateHistory<Long> statefulObject) {
        statefulObjects.add(statefulObject);
    }

    public void removeStatefulObject(StateHistory<Long> statefulObject) {
        statefulObjects.remove(statefulObject);
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
