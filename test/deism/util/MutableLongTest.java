package deism.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class MutableLongTest {
    @Test
    public void testOneMutableLong() {
        MutableLong one = new MutableLong(1);
        assertEquals(1L, one.get());
    }

    @Test
    public void testAdd() {
        MutableLong two = new MutableLong(2);
        two.add(3);
        assertEquals(5L, two.get());
    }

    @Test
    public void testMin() {
        MutableLong ten = new MutableLong(10);
        ten.min(7);
        assertEquals(7L, ten.get());
    }
}
