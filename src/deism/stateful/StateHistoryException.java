package deism.stateful;

/**
 * Exception thrown whenever a state was not successfully saved, rolled back to
 * or committed.
 */
@SuppressWarnings("serial")
public class StateHistoryException extends RuntimeException {
    public StateHistoryException(String reason) {
        super(reason);
    }
}
