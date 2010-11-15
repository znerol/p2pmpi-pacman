package deism;

/**
 * EventMatcher implement methods to match an event against a given rule
 */
public interface EventMatcher {
    boolean match(Event e);
}
