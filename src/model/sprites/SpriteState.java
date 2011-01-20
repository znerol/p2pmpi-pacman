package model.sprites;

import java.io.Serializable;

import deism.util.Pair;
import deism.util.Triple;

import model.Direction;
import model.events.EventVisitor;

/**
 * Interface for sprite states
 */
public interface SpriteState extends Cloneable, Serializable, EventVisitor {
    /**
     * Sprite owner id
     * 
     * @return owner id
     */
    public int getId();

    public Object clone();

    /**
     * Simulation time of state
     * 
     * @return simulation time
     */
    public Long getTimestamp();

    /**
     * Sprites origin to time of state
     * 
     * @return sprite position
     */
    public Pair<Integer, Integer> getOrigin();

    /**
     * Calculates the position in the future of this state. Used to update the GUI
     * @param simTime simulation time
     * @return Tripple with the current direction, abscissa and ordinate
     */
    public Triple<Direction, Integer, Integer> nextPosition(Long simTime);
}