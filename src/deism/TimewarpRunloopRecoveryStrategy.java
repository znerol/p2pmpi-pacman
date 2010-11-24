package deism;

import java.util.SortedSet;
import java.util.TreeSet;

public class TimewarpRunloopRecoveryStrategy
        implements EventRunloopRecoveryStrategy {

    private SortedSet<Long> snapshots;
    private Iterable<StateHistory<Long>> stateObjects;
    public TimewarpRunloopRecoveryStrategy(
            Iterable<StateHistory<Long>> stateObjects) {
        this.stateObjects = stateObjects;
        this.snapshots = new TreeSet<Long>();
    }
        
    @Override
    public void save(Long timestamp) {
        for (StateHistory<Long> s : stateObjects) {
            s.save(timestamp);
        }
        snapshots.add(timestamp);
    }

    @Override
    public void rollback(Long timestamp) {
        snapshots.tailSet(timestamp).clear();
        Long snapshotKey = snapshots.last();
        
        for (StateHistory<Long> s : stateObjects) {
            s.rollback(snapshotKey);
        }
    }

    @Override
    public void commit(Long timestamp) throws StateHistoryException {
        snapshots.headSet(timestamp).clear();
        Long snapshotKey = snapshots.first();
        
        for (StateHistory<Long> s : stateObjects) {
            s.commit(snapshotKey);
        }
    }
}
