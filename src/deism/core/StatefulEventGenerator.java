package deism.core;

/**
 * An event generator is a special kind of event source. A generator emits
 * events which do not depend on external state, i.e. the simulation state and
 * especially simulation time. This kind of generator will likely maintain
 * internal state like an event queue. Generators are useful as end points of
 * realtime communication channels between logical simulation processes or as
 * playback device for prerecorded event sequences stored on disk.
 */
public interface StatefulEventGenerator extends Stateful {
    /**
     * Receive an event from the generator.
     * 
     * @return next event or null if none is available
     */
    public Event poll();
}
