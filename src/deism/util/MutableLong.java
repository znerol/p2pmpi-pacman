package deism.util;

/**
 * Simple wrapper for a mutable long integer which is ideal for storing it in a
 * collection while maintaining mutability.
 */
public class MutableLong {
    private long value = 0;

    public MutableLong(long value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public long get() {
        return value;
    }

    /**
     * Add given summand to stored value
     * 
     * @param summand
     */
    public void add(long summand) {
        value = value + summand;
    }

    /**
     * Store the minimum of the given parameter and the stored value
     * 
     * @param min
     */
    public void min(long min) {
        value = Math.min(value, min);
    }
}
