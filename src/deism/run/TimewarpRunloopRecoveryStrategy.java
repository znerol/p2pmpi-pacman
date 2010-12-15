package deism.run;

import java.util.SortedSet;
import java.util.TreeSet;

import deism.stateful.StateHistory;
import deism.stateful.StateHistoryException;

public class TimewarpRunloopRecoveryStrategy
        implements EventRunloopRecoveryStrategy {

    private SortedSet<Long> snapshots;
    private SortedSet<Long> unusableSnapshots = new TreeSet<Long>();
    private Iterable<StateHistory<Long>> stateObjects;
    public TimewarpRunloopRecoveryStrategy(
            Iterable<StateHistory<Long>> stateObjects) {
        this.stateObjects = stateObjects;
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
        
        for (StateHistory<Long> s : stateObjects) {
            s.save(timestamp);
        }
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
        for (StateHistory<Long> s : stateObjects) {
            s.rollback(snapshotKey);
        }
    }

    @Override
    public void commit(Long timestamp) throws StateHistoryException {
        snapshots.headSet(timestamp).clear();
        if (snapshots.size() == 0) {
            throw new StateHistoryException(
                    "Attempt to rollback to a timestamp which was never recorded");
        }
        
        Long snapshotKey = snapshots.first();
        for (StateHistory<Long> s : stateObjects) {
            s.commit(snapshotKey);
        }
    }
}
