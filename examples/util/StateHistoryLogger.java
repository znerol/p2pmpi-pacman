package util;

import org.apache.log4j.Logger;

import deism.stateful.StateHistory;
import deism.stateful.StateHistoryException;

public class StateHistoryLogger implements StateHistory<Long> {
    private final static Logger logger = Logger.getLogger(StateHistoryLogger.class);

    @Override
    public void save(Long timestamp) throws StateHistoryException {
        logger.info("Save time=" + timestamp);
    }
    
    @Override
    public void rollback(Long timestamp) {
        logger.info("Rollback time=" + timestamp);
    }

    @Override
    public void commit(Long timestamp) {
        logger.info("Commit time=" + timestamp);
    }
}
