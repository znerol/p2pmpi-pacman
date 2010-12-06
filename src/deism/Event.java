package deism;

import java.io.Serializable;

/**
 * Base class for events
 */
public class Event implements Comparable<Event>, Serializable, Cloneable {
    /**
     * 
     */
    private static final long serialVersionUID = 5763192535418365665L;

    /**
     * Simulation time
     */
    private final long simtime;

    /**
     * True if this Event is an antimessage, false otherwise
     */
    private boolean antimessage;

    public Event(long simtime, boolean antimessage) {
        this.simtime = simtime;
        this.antimessage = antimessage;
    }

    public Event(long simtime) {
        this(simtime, false);
    }

    /**
     * Return timestamp of Event in simulation time units
     */
    public long getSimtime() {
        return simtime;
    }

    /**
     * Return true if this Event is an antimessage
     */
    public boolean isAntimessage() {
        return antimessage;
    }

    /**
     * Returns a clone of this Event with an inverted antimessage flag.
     */
    public final Event inverseEvent() {
        Event result;

        try {
            result = (Event)clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error("Events must be cloneable");
        }

        result.antimessage = !result.antimessage;
        return result;
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
