package deism;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Base class for events
 */
public class Event implements Comparable<Event>, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 3899689741217915272L;
    
    /**
     * Simulation time
     */
    private long simtime;

    public Event(long simtime) {
        this.simtime = simtime;
    }

    public long getSimtime() {
        return simtime;
    }

    @Override
    public int compareTo(Event o) {
        return (int) (this.simtime - o.simtime);
    }

    @Override
    public String toString() {
        return "Event [simtime = " + simtime + "]";
    }

    protected void writeObject(ObjectOutputStream out) throws IOException {
        out.writeLong(simtime);
    }

    protected void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        simtime = in.readLong();
    }
}
