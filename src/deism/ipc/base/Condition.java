package deism.ipc.base;

/**
 * Implementers of the Condition interface provide methods to match an item
 * against a given rule.
 */
public interface Condition<T> {
    /**
     * Apply condition to the given item
     * 
     * @param item
     *            to check
     * @return true if item fulfills the condition, false otherwise
     */
    boolean match(T item);
}
