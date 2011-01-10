package deism.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LongMap<K> extends HashMap<K, MutableLong> {
    private static final long serialVersionUID = -3512436972868685812L;

    /**
     * Return element identified by key or a newly created element with given
     * default value if key does not exist.
     * 
     * @param key
     * @param defaultValue
     * @return
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
     * @return
     */
    public Map<K, Long> valueMap() {
        Map<K, Long> result = new HashMap<K, Long>(this.size());

        for (Entry<K, MutableLong> entry : entrySet()) {
            result.put(entry.getKey(), entry.getValue().get());
        }

        return result;
    }
}
