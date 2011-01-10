package model.sprites;

import model.Direction;
import model.Waypoint;


public interface State extends Cloneable {
    public Direction getCurrentDirection();
    public Direction getNextDirection();
    //public void setNextDirection(Direction dir, int time);
    public Waypoint getCurrentWaypoint();
    public int getTimestamp();
    public void happyPillEaten();
    public int getHappyPillSteps();
    public State getState(int time);
}
