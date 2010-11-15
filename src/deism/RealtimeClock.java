package deism;

/**
 * Clock for RealtimEventTimer
 * 
 * Convert between walltime and simulation time
 */
public class RealtimeClock {
    long startRealtime;
    long startSimtime;
    double scale;

    public RealtimeClock() {
        this(getRealtime(), 0L, 1.0);
    }

    public RealtimeClock(double scale) {
        this(getRealtime(), 0L, scale);
    }

    public RealtimeClock(long startRealtime, long startSimtime, double scale) {
        this.startRealtime = startRealtime;
        this.startSimtime = startSimtime;
        this.scale = scale;
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
     * @param currentTimeMillis
     *            walltime
     * @return walltime converted to simtime
     */
    public long getSimtime(long currentTimeMillis) {
        long duration = (long) (scale * (currentTimeMillis - startRealtime));
        return startSimtime + duration;
    }

    /**
     * Return current walltime
     * 
     * @return current walltime
     */
    public static long getRealtime() {
        return System.currentTimeMillis();
    }

    /**
     * Return simulation time converted to walltime units (milliseconds)
     * 
     * @param simtime
     *            simulation time
     * @return walltime
     */
    public long getRealtime(long simtime) {
        long duration = (long) ((simtime - startSimtime) / scale);
        return startRealtime + duration;
    }

    /**
     * Return simulation time delta in walltime units (milliseconds)
     * 
     * @param simtimeDifference
     *            simulation time delta
     * @return simulation time delta converted to walltime units
     */
    public long getRealtimeDifference(long simtimeDifference) {
        return (long) (simtimeDifference / scale);
    }
}
