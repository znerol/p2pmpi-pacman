package deism;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class EventTest {
    @Test
    public void testConstructor() {
        Event event = new Event(7L);
        assertEquals(7L, event.getSimtime());
    }

    @Test
    public void testComparable() {
        Event soon = new Event(1L);
        Event late = new Event(7L);

        assertTrue(soon.compareTo(soon) == 0);
        assertTrue(soon.compareTo(late) < 0);
    }

    @Test
    public void testSerialize() throws IOException, ClassNotFoundException {
        Event event = new Event(7L);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        assertEquals(0, outstream.size());

        ObjectOutputStream out = new ObjectOutputStream(outstream);
        out.writeObject(event);
        assertTrue(0 < outstream.size());

        byte[] buffer = outstream.toByteArray();

        ByteArrayInputStream instream = new ByteArrayInputStream(buffer);
        ObjectInputStream in = new ObjectInputStream(instream);

        Event result = (Event)in.readObject();
        assertEquals(result,event);
    }

    @Test
    public void testEqualityAndHashcode() {
        Event one = new Event(8L);
        Event two = new Event(7L);
        Event three = new Event(7L);

        assertEquals(one,one);
        assertThat(one,is(not(two)));
        assertEquals(two,three);

        assertEquals(two.hashCode(), three.hashCode());
    }

    @Test
    public void testInvertEvent() {
        Event event = new Event(7L);
        Event inverse = event.inverseEvent();

        assertEquals(event.getSimtime(), inverse.getSimtime());
        assertEquals(true, inverse.isAntimessage());
        assertEquals(false, event.isAntimessage());

        Event doubleinverse = inverse.inverseEvent();
        assertEquals(event, doubleinverse);

        assertEquals(event.hashCode(), doubleinverse.hashCode());
    }

    @SuppressWarnings("serial")
    private class Subevent extends Event {
        private final long subvalue;

        public Subevent(long timestamp, long subvalue, boolean antimessage) {
            super(timestamp, antimessage);
            this.subvalue = subvalue;
        }

        public long getSubvalue() {
            return subvalue;
        }
    }

    @Test
    public void testSubeventInvert() {
        Subevent event = new Subevent(7L, 8L, false);
        Event inverse = event.inverseEvent();

        assertTrue(inverse instanceof Subevent);
        Subevent invsub = (Subevent)inverse;
        assertEquals(8L, invsub.getSubvalue());
    }
}
