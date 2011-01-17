package model.sprites;

import model.Direction;
import model.Waypoint;

public interface SpriteState extends State {
    public Direction getCurrentDirection();
    public Direction getNextDirection();
    public Waypoint getCurrentWaypoint();
    public void happyPillEaten();
    public int getHappyPillSteps();
}
