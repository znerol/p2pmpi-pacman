package model.sprites;

import model.Direction;
import deism.core.Event;

public interface MoveableSpriteState extends SpriteState {
    public Direction getCurrentDirection();
    public Direction getNextDirection();
    public Event getEvent();
    public void updateTo(Long simTime);

}
