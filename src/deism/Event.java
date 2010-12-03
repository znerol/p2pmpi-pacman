package deism;

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
    private final long simtime;

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
}
