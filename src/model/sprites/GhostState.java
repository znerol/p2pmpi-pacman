package model.sprites;

import model.Direction;
import model.Waypoint;

public class GhostState extends AbstractState {

    public GhostState(Direction currentDir, Direction nextDir,
            Waypoint waypoint) {
        super(currentDir, nextDir, waypoint, 0);
    }

    public GhostState(GhostState state) {
        super(state);
    }

    @Override
    protected void generateNextState() {
        new GhostState(this);        
    }

    @Override
    protected boolean isMovingAllowed() {
        return getHappyPillSteps() % 2 == 0;
    }
}
