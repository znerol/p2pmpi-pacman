package deism;

/**
 * 
 */
public interface StateHistory <T> {
    /**
     * Take a snapshot of the current state and store it associated with the
     * given key.
     */
    public void save(T key) throws StateHistoryException;
    
    /**
     * States up to but not including the one associated with the given key are
     * not subject of any operation anymore and may be disposed. Subsequent
     * rollbacks may not refer to a state preceding the given key.
     */
    public void commit(T key) throws StateHistoryException;
    
    /**
     * Restore the state associated with the given key.
     */
    public void rollback(T key) throws StateHistoryException;
}
