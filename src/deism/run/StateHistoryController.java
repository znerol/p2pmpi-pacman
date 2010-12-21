package deism.run;

import java.util.SortedSet;
import java.util.TreeSet;

import deism.stateful.StateHistory;
import deism.stateful.StateHistoryException;

public class StateHistoryController implements StateController {

    private SortedSet<Long> snapshots;
    private SortedSet<Long> unusableSnapshots = new TreeSet<Long>();
    private StateHistory<Long> stateObject;

    public StateHistoryController(StateHistory<Long> stateObject) {
        this.stateObject = stateObject;
        this.snapshots = new TreeSet<Long>();
    }

    @Override
    public void save(Long timestamp) {
        if (unusableSnapshots.contains(timestamp)) {
            return;
        }
        else if (snapshots.contains(timestamp)) {
            unusableSnapshots.add(timestamp);
            snapshots.remove(timestamp);
            return;
        }

        stateObject.save(timestamp);
        snapshots.add(timestamp);
    }

    @Override
    public void rollback(Long timestamp) {
        snapshots.tailSet(timestamp).clear();
        if (snapshots.size() == 0) {
            throw new StateHistoryException(
                    "Attempt to rollback to a timestamp which was never recorded");
        }

        Long snapshotKey = snapshots.last();
        stateObject.rollback(snapshotKey);
    }

    @Override
    public void commit(Long timestamp) throws StateHistoryException {
        snapshots.headSet(timestamp).clear();
        if (snapshots.size() == 0) {
            throw new StateHistoryException(
                    "Attempt to rollback to a timestamp which was never recorded");
        }

        Long snapshotKey = snapshots.first();
        stateObject.commit(snapshotKey);
    }
}
