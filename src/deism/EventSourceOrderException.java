package deism;

/**
 * Exception thrown by EventRunloop.run whenever events can not be processed
 * because they were out of sequence
 */
@SuppressWarnings("serial")
public class EventSourceOrderException extends RuntimeException {
	public EventSourceOrderException(String message) {
		super(message);
	}
}
