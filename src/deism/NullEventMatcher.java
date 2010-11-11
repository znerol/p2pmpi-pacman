package deism;

/**
 * EventMatcher implementation which matches null
 * 
 * The NullEventMatcher is especially useful as a terminationCondition for a 
 * EventRunloops.
 */
public class NullEventMatcher implements EventMatcher {
	@Override
	public boolean match(Event e) {
		return e == null;
	}
}
