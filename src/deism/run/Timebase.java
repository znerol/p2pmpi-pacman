package deism.run;

/**
 * Convert between different time bases
 */
public class Timebase {
    private long base;
    private double scale;

    public Timebase(long base, double scale) {
        this.base = base;
        this.scale = scale;
    }

    public Timebase(long base) {
        this(base, 1.0);
    }

    public Timebase(double scale) {
        this(0, scale);
    }

    public Timebase() {
        this(0, 1.0);
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
    public void setTimebase(long base) {
        this.base = base;
    }

    /**
     * Return time base
     */
    public long getTimebase() {
        return base;
    }

    /**
     * Return simulation time represented by the given walltime
     * 
     * @param currentTimeMillis
     *            walltime
     * @return walltime converted to simtime
     */
    public long convert(long timestamp, Timebase target) {
        long dt = (long) (target.scale / this.scale * (timestamp - this.base));
        return target.base + dt;
    }
}
