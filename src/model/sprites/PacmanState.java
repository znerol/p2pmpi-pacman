package model.sprites;

import model.Direction;
import model.Waypoint;

public class PacmanState extends AbstractSpriteState {
    private int points;

    public PacmanState(Direction currentDir, Direction nextDir,
            Waypoint waypoint) {
        super(currentDir, nextDir, waypoint, 0);
        
    }

    public PacmanState(PacmanState state) {
        super(state);
        
        this.points = state.points;
    }
    
    @Override
    protected void generateNextState() {
        new PacmanState(this);
    }

    @Override
    protected boolean isMovingAllowed() {
        return true;
    }   
}
