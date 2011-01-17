package model.sprites;

import model.Direction;
import model.Waypoint;

public interface SpriteState extends State {
    public Direction getCurrentDirection();
    public Direction getNextDirection();
}
