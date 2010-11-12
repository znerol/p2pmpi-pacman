package deism;

/**
 * Base class for events
 */
public class Event implements Comparable<Event> {
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
		return (int)(this.simtime - o.simtime);
	}
	
	@Override
	public String toString() {
		return "Event [simtime = " + simtime + "]";
	}
}
