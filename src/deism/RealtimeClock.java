package deism;

/**
 * Clock for RealtimEventTimer
 * 
 * Convert between walltime and simulation time
 */
public class RealtimeClock implements Clock {
    long startRealtime;
    long startSimtime;
    double scale;

    public RealtimeClock() {
        this(RealtimeClock.getWallclock(), 0L, 1.0);
    }

    public RealtimeClock(double scale) {
        this(RealtimeClock.getWallclock(), 0L, scale);
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
    private static long getWallclock() {
        return System.currentTimeMillis();
    }
}
