package deism;

import java.util.SortedSet;
import java.util.TreeSet;

public class TimewarpRunloopRecoveryStrategy implements
        EventRunloopRecoveryStrategy {

    private EventMatcher snapshotCondition;
    private SortedSet<Long> snapshots;
    private Iterable<StateHistory<Long>> stateObjects;
    private long lastEventTimestamp;

    public TimewarpRunloopRecoveryStrategy(EventMatcher snapshotCondition,
            Iterable<StateHistory<Long>> stateObjects) {
        this.snapshotCondition = snapshotCondition;
        this.stateObjects = stateObjects;
        this.snapshots = new TreeSet<Long>();
        this.lastEventTimestamp = 0;
    }
        
    @Override
    public boolean shouldSave(Event e) {
        lastEventTimestamp = e.getSimtime();
        return snapshotCondition.match(e);
    }
    
    @Override
    public void save(Long timestamp) {
        for (StateHistory<Long> s : stateObjects) {
            s.save(timestamp);
        }
        snapshots.add(timestamp);
    }

    @Override
    public boolean shouldRollback(Event e) {
        return e.getSimtime() < lastEventTimestamp;
    }

    @Override
    public void rollback(Long timestamp) {
        snapshots.tailSet(timestamp).clear();
        Long snapshotKey = snapshots.last();
        
        for (StateHistory<Long> s : stateObjects) {
            s.rollback(snapshotKey);
        }
        
        lastEventTimestamp = snapshotKey;
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
