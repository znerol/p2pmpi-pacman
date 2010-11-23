package deism;

@SuppressWarnings("serial")
public class StateHistoryException extends RuntimeException {
    public StateHistoryException(String reason) {
        super(reason);
    }
}
