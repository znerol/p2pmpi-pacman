package deism;

/**
 * EventCondition implement methods to match an event against a given rule
 */
public interface EventCondition {
    boolean match(Event e);
}
