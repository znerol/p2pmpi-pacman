package deism.core;

/**
 * A stateless event generator derives events from the current simulation
 * state, especially the simulation time. A class implementing this interface
 * may not maintain any internal state.
 */
public interface StatelessEventGenerator {
    /**
     * Retrieve an event based on the current simulation state.
     *
     * @param simtime current timestamp in simulation time units
     * @return current event or null if none is available
     */
    public Event peek(long simtime);
}
