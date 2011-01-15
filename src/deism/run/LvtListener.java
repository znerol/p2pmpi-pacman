package deism.run;

/**
 * Registered lvt listeners will get notified whenever the local virtual time
 * changes.
 * 
 * @see {@link Service}
 */
public interface LvtListener {
    /**
     * Local virtual time changed to the given value in this simulation
     * 
     * @param lvt
     *            new local virtual time
     */
    public void update(long lvt);
}
