package deism.run;

import deism.stateful.StateHistoryException;

/**
 * Implementation of {@link StateController} which does nothing at all.
 */
public class NoStateController implements
        StateController {

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
