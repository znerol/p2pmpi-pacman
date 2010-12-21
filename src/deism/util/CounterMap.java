package deism.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CounterMap<K> extends HashMap<K, CounterMap<K>.Counter> {
    private static final long serialVersionUID = -3512436972868685812L;

    public class Counter {
        private long value = 0;

        public Counter(long value) {
            this.value = value;
        }

        public long get() {
            return value;
        }

        public void increment(long by) {
            value = value + by;
        }

        public void minimize(long min) {
            value = Math.min(value, min);
        }
    }

    /**
     * Increment counter identified by key by given value. A new counter object
     * is created if it does not exist yet.
     *
     * @param key
     * @param by
     */
    public void increment(K key, long by) {
        Counter counter = get(key);
        if (counter == null) {
            counter = new Counter(by);
            put(key, counter);
        }
        else {
            counter.increment(by);
        }
    }

    /**
     * Minimize counter and given value. A new counter object is created if it
     * does not exist yet.
     */
    public void minimize(K key, long min) {
        Counter counter = get(key);
        if (counter == null) {
            counter = new Counter(min);
            put(key, counter);
        }
        else {
            counter.minimize(min);
        }
    }

    /**
     * Return a HashMap containing all the keys and counter values.
     * @return
     */
    public Map<K, Long>valueMap() {
        Map<K, Long> result = new HashMap<K, Long>(this.size());

        for (Entry<K, Counter> entry : entrySet()) {
            result.put(entry.getKey(), entry.getValue().get());
        }

        return result;
    }
}
