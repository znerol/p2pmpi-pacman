package deism;

import org.junit.Test;
import static org.junit.Assert.*;

public class TimebaseTest {
    @Test
    public void testConstructor() {
        Timebase timebase;

        timebase = new Timebase();
        assertEquals(1.0, timebase.getScale(), 0.0);
        assertEquals(0L, timebase.getTimebase());

        timebase = new Timebase(42L);
        assertEquals(1.0, timebase.getScale(), 0.0);
        assertEquals(42L, timebase.getTimebase());

        timebase = new Timebase(3.14);
        assertEquals(3.14, timebase.getScale(), 0.0);
        assertEquals(0L, timebase.getTimebase());

        timebase = new Timebase(42L, 3.14);
        assertEquals(3.14, timebase.getScale(), 0.0);
        assertEquals(42L, timebase.getTimebase());

        timebase.setScale(0.0);
        assertEquals(0.0, timebase.getScale(), 0.0);
        timebase.setTimebase(0L);
        assertEquals(0L, timebase.getTimebase());
    }

    @Test
    public void testConvertSelf() {
        Timebase timebase = new Timebase(42L, 0.1234);

        long result = timebase.convert(7L, timebase);
        assertEquals(7L, result);
    }

    @Test
    public void testConvert() {
        Timebase slow = new Timebase(0.5);
        Timebase fast = new Timebase(2.0);

        long result;

        result = slow.convert(10L, fast);
        assertEquals(40L, result);

        result = fast.convert(40L, slow);
        assertEquals(10L, result);
    }

    @Test
    public void testConvertWithBase() {
        Timebase slow = new Timebase(4, 0.5);
        Timebase fast = new Timebase(1, 2.0);

        long result;

        result = slow.convert(10L, fast);
        assertEquals(25L, result);

        result = fast.convert(25L, slow);
        assertEquals(10L, result);
    }
}
