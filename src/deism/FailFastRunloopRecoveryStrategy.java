package deism;

/**
 * Implementation of EventRunloopRecoveryStrategy which does nothing at all.
 */
public class FailFastRunloopRecoveryStrategy implements
        EventRunloopRecoveryStrategy {

    @Override
    public void save(Long key) throws StateHistoryException {
        // do nothing
    }

    @Override
    public void commit(Long key) throws StateHistoryException {
        // do nothing
    }

    @Override
    public void rollback(Long key) throws StateHistoryException {
        throw new StateHistoryException(
                "Event source returns events out of sequence");
    }
}
