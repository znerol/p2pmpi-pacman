package util;

import deism.StateHistory;
import deism.StateHistoryException;

public class StateHistoryLogger implements StateHistory<Long> {

    @Override
    public void save(Long timestamp) throws StateHistoryException {
    }
    
    @Override
    public void rollback(Long timestamp) {
        System.out.println("** Rollback time=" + timestamp);
    }

    @Override
    public void commit(Long timestamp) {
        System.out.println("** Commit time=" + timestamp);
    }
}
