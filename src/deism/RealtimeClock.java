package deism;

/**
 * Clock for RealtimeExecutionGovernor
 * 
 * Convert between walltime and simulation time
 */
public class RealtimeClock {
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
    public void setTimebase(long simtime) {
        startSimtime = simtime;
    }

    /**
     * Return simulation time represented by the given walltime
     * 
     * @param currentTimeMillis
     *            walltime
     * @return walltime converted to simtime
     */
    public long toSimulationTimeUnits(long currentTimeMillis) {
        long duration = (long) (scale * (currentTimeMillis - startRealtime));
        return startSimtime + duration;
    }

    /**
     * Set wallclock baseline to given timestamp
     */
    public void setSystemTimeBase(long realtime) {
        startRealtime = realtime;
    }

    /**
     * Return simulation time converted to walltime units (milliseconds)
     * 
     * @param simtime
     *            simulation time
     * @return walltime
     */
    public long toSystemTimeUnits(long simtime) {
        long duration = (long) ((simtime - startSimtime) / scale);
        return startRealtime + duration;
    }
}
