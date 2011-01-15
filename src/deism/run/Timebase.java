package deism.run;

/**
 * Provide scalable and convertible time system. Reference time units are
 * milliseconds.
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

    /**
     * Return time scale factor. Reference time units are milliseconds.
     * 
     * @return time scale factor
     */
    public double getScale() {
        return scale;
    }

    /**
     * Set time scale factor. Reference time units are milliseconds.
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * Set start time to given timestamp
     */
    public void setTimebase(long base) {
        this.base = base;
    }

    /**
     * Return start time
     */
    public long getTimebase() {
        return base;
    }

    /**
     * Convert timestamp in units of this timebase to a timestamp in units of
     * the target timebase.
     * 
     * @param timestamp
     *            in units of this timebase
     * @param target
     *            other timebase
     * @return timestamp in units of the target timebase
     */
    public long convert(long timestamp, Timebase target) {
        long dt = (long) (target.scale / this.scale * (timestamp - this.base));
        return target.base + dt;
    }
}
