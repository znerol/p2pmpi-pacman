package model.sprites;

import model.Direction;
import deism.core.Event;

/**
 * Marks a sprite as movable
 */
public interface MovableSpriteState extends SpriteState {
    /**
     * Current direction of the sprite to the moment of the state
     * 
     * @return current direction
     */
    public Direction getCurrentDirection();

    /**
     * Next direction of the sprite to the moment of the state. The sprite will
     * turn on the next point where this direction is possible
     * 
     * @return next direction
     */
    public Direction getNextDirection();

    /**
     * The next predicted event at this state
     * 
     * @return next event
     */
    public Event getEvent();

    /**
     * Performs an update of the sprite to the given time. The parameter has to
     * be larger than the current time in this state.
     * 
     * @param simTime
     *            next simulation time.
     */
    public void updateToTime(Long simTime);

}
