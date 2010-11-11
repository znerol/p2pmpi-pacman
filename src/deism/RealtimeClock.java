package deism;

/**
 * Clock for RealtimEventMonitor
 * 
 * Convert between walltime and simulation time
 */
public class RealtimeClock {
	long startRealtime;
	long startSimtime;
	double scale;
	
	public RealtimeClock() {
		startRealtime = this.getRealtime();
		startSimtime = 0;
		scale = 1.0;
	}
	
	/**
	 * Return current walltime converted to simulation time
	 * 
	 * @return current walltime converted to simtime
	 */
	public long getSimtime() {
		return getSimtime(System.currentTimeMillis());
	}
	
	/**
	 * Return simulation time represented by the given walltime
	 * 
	 * @param currentTimeMillis walltime
	 * @return walltime converted to simtime
	 */
	public long getSimtime(long currentTimeMillis) {
		long duration = (long)(scale * (currentTimeMillis - startRealtime));
		return startSimtime + duration;
	}
	
	/**
	 * Return current walltime
	 * 
	 * @return current walltime
	 */
	public long getRealtime() {
		return System.currentTimeMillis();
	}
	
	/**
	 * Return simulation time converted to walltime units (milliseconds)
	 * 
	 * @param simtime simulation time
	 * @return walltime
	 */
	public long getRealtime(long simtime) {
		long duration = (long)((simtime - startSimtime)/scale);
		return startRealtime + duration;
	}
	
	/**
	 * Return simulation time delta in walltime units (milliseconds)
	 * 
	 * @param simtimeDifference simulation time delta
	 * @return simulation time delta converted to walltime units
	 */
	public long getRealtimeDifference(long simtimeDifference) {
		return (long)(simtimeDifference/scale);
	}
}
