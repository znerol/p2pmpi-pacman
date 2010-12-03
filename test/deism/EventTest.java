package deism;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;
import static org.junit.Assert.*;

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
        assertTrue(result.compareTo(event) == 0);
    }
}
