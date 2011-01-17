package model.sprites;

import model.Direction;
import model.Waypoint;

public class PacmanState extends AbstractSpriteState {
    private int points;

    public PacmanState(Direction currentDir, Direction nextDir,
            Waypoint waypoint, int id) {
        super(currentDir, nextDir, waypoint, 0L, id);
        
    }

    public PacmanState(PacmanState state, Direction nextDirection, Waypoint currentWaypoint) {
        super(state, nextDirection, currentWaypoint);
        
        // Super contructor has may changed the current waypoint and has now to be updated as well.
        if (currentWaypoint.getAbsoluteX() != getX() || currentWaypoint.getAbsoluteY() != getY()) {
            currentWaypoint = currentWaypoint.getNextWaypoint(getCurrentDirection());
            // TODO
        }
        
        this.points = state.points;
    }
    
    @Override
    public State transaction(Direction nextDirection, Waypoint currentWaypoint) {
        return new PacmanState(this, nextDirection, currentWaypoint);
    }
}
