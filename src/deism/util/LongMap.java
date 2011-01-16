package deism.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Helper class for a map of counters.
 * 
 * @param <K>
 *            Type of key
 * @see <a
 *      href="http://stackoverflow.com/questions/81346/most-efficient-way-to-increment-a-map-value-in-java">Stackoverflow.com:
 *      Most efficient way to increment a Map value in Java</a>
 */
public class LongMap<K> extends HashMap<K, MutableLong> {
    private static final long serialVersionUID = -3512436972868685812L;

    /**
     * Return element identified by key or a newly created element with given
     * default value if key does not exist.
     * 
     * @param key
     * @param defaultValue
     * @return MutableLong element for the given key
     */
    public MutableLong get(K key, long defaultValue) {
        MutableLong element = get(key);

        if (element == null) {
            element = new MutableLong(defaultValue);
            put(key, element);
        }

        return element;
    }

    /**
     * Return a HashMap containing all the keys and the long values.
     * 
     * @return HashMap with keys and values of the contained MutableLong
     *         elements
     */
    public Map<K, Long> valueMap() {
        Map<K, Long> result = new HashMap<K, Long>(this.size());

        for (Entry<K, MutableLong> entry : entrySet()) {
            result.put(entry.getKey(), entry.getValue().get());
        }

        return result;
    }
}
