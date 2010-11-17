package deism;

public interface Clock {

    /**
     * Return current walltime converted to simulation time
     * 
     * @return current walltime converted to simtime
     */
    public long getSimtime();

    /**
     * Return simulation time represented by the given walltime
     * 
     * @param currentTimeMillis
     *            walltime
     * @return walltime converted to simtime
     */
    public long getSimtime(long currentTimeMillis);

    /**
     * Return current walltime
     * 
     * @return current walltime
     */
    public long getRealtime();

    /**
     * Return simulation time converted to walltime units (milliseconds)
     * 
     * @param simtime
     *            simulation time
     * @return walltime
     */
    public long getRealtime(long simtime);

}