package deism.run;

import deism.stateful.StateHistoryException;

public interface StateController {
    /**
     * Take a snapshot of the current state of the simulation for the given
     * timestamp and store it for later rollback or commit operations.
     */
    public void save(Long timestamp) throws StateHistoryException;

    /**
     * States up to but not including the one associated with the given
     * timestamp are not subject to any operation anymore and may be disposed.
     * Subsequent rollbacks may not refer to a state preceding the given
     * timestamp.
     *
     * If there is no state associated with this timestamp the next older state
     * must be used.
     */
    public void commit(Long timestamp) throws StateHistoryException;

    /**
     * Restore the state associated with the given timestamp.
     *
     * If there is no state associated with this timestamp the next older state
     * must be used.
     */
    public void rollback(Long timestamp) throws StateHistoryException;
}
