package deism;

/**
 * Clock for RealtimeExecutionGovernor
 * 
 * Convert between walltime and simulation time
 */
public class RealtimeClock implements Clock {
    private long startRealtime;
    private long startSimtime;
    private double scale;

    public RealtimeClock() {
        this(1.0);
    }

    public RealtimeClock(double scale) {
        this.scale = scale;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * Set simulation time baseline to given timestamp
     */
    public void setSimtime(long simtime) {
        startSimtime = simtime;
    }

    /**
     * Return current walltime converted to simulation time
     * 
     * @return current walltime converted to simtime
     */
    @Override
    public long getSimtime() {
        return getSimtime(getRealtime());
    }

    /**
     * Return simulation time represented by the given walltime
     * 
     * @param currentTimeMillis
     *            walltime
     * @return walltime converted to simtime
     */
    @Override
    public long getSimtime(long currentTimeMillis) {
        long duration = (long) (scale * (currentTimeMillis - startRealtime));
        return startSimtime + duration;
    }

    /**
     * Set wallclock baseline to given timestamp
     */
    public void setRealtime(long realtime) {
        startRealtime = realtime;
    }

    /**
     * Return current walltime
     * 
     * @return current walltime
     */
    @Override
    public long getRealtime() {
        return RealtimeClock.getWallclock();
    }

    /**
     * Return simulation time converted to walltime units (milliseconds)
     * 
     * @param simtime
     *            simulation time
     * @return walltime
     */
    @Override
    public long getRealtime(long simtime) {
        long duration = (long) ((simtime - startSimtime) / scale);
        return startRealtime + duration;
    }
    
    /**
     * Return current system time in milliseconds
     * @return walltime
     */
    public static long getWallclock() {
        return System.currentTimeMillis();
    }
}
