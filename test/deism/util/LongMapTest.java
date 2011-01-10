package deism.util;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class LongMapTest {
    private LongMap<String> longMap = new LongMap<String>();

    @Test
    public void testSimplePutGet() {
        MutableLong one = new MutableLong(1);
        longMap.put("one", one);
        MutableLong result = longMap.get("one");
        assertEquals(one, result);
    }

    @Test
    public void testGetWithDefault() {
        MutableLong one = longMap.get("one", 1L);
        assertEquals(one.get(), 1L);
    }

    @Test
    public void testGetValueMap() {
        longMap.get("one", 1);
        longMap.get("two", 2);

        Map<String, Long> expect = new HashMap<String, Long>();
        expect.put("one", 1L);
        expect.put("two", 2L);

        Map<String, Long> result = longMap.valueMap();
        assertEquals(expect, result);
    }
}
