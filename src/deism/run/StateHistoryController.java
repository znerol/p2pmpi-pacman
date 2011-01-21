package deism.run;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import deism.stateful.StateHistory;
import deism.stateful.StateHistoryException;

/**
 * A {@link StateController} implementation controlling {@link StateHistory}
 */
public class StateHistoryController implements StateController {

    private SortedSet<Long> snapshots = new TreeSet<Long>();
    private SortedSet<Long> unusableSnapshots = new TreeSet<Long>();
    private StateHistory<Long> stateObject;
    private static final Logger logger = Logger.getLogger(StateHistoryController.class);

    public StateHistory<Long> getStateObject() {
        return stateObject;
    }

    @Override
    public void setStateObject(StateHistory<Long> stateObject) {
        this.stateObject = stateObject;
    }

    @Override
    public void save(Long timestamp) {
        logger.debug("Saving timestamp: " + timestamp);
        stateObject.save(timestamp);
        snapshots.add(timestamp);
    }

    @Override
    public void rollback(Long timestamp) {
        logger.debug("Rollback timestamp: " + timestamp + " snapshots: " + snapshots);
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
        logger.debug("Comit timestamp: " + timestamp + " snapshots: " + snapshots);
        if (snapshots.size() == 1) {
            logger.debug("Not comiting, only one snapshot left");
            return;
        }

        if (timestamp > snapshots.last()) {
            logger.debug("Reducing timestamp: " + timestamp + " to: " + snapshots.last());
            timestamp = snapshots.last();
        }

        snapshots.headSet(timestamp).clear();
        if (snapshots.size() == 0) {
            throw new StateHistoryException(
                    "Attempt to commit a timestamp which was never recorded");
        }

        Long snapshotKey = snapshots.first();
        stateObject.commit(snapshotKey);
    }
}
