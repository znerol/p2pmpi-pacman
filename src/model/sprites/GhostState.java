package model.sprites;

import model.Direction;
import model.Waypoint;
import deism.core.Event;
import deism.process.DiscreteEventProcess;

public class GhostState extends AbstractSpriteState implements DiscreteEventProcess {
    
    private final int originX;
    private final int originY;

    public GhostState(Direction currentDir, Direction nextDir,
            Waypoint waypoint, int id) {
        super(currentDir, nextDir, waypoint, 0L, id);
        originX = waypoint.getAbsoluteX();
        originY = waypoint.getAbsoluteY();
    }

    public GhostState(GhostState state, Direction nextDirection, Waypoint currentWaypoint) {
        super(state, nextDirection, currentWaypoint);
        
        this.originX = state.originX;
        this.originY = state.originY;
    }

    @Override
    public State transaction(Direction nextDirection, Waypoint currentWaypoint) {
        return new GhostState(this, nextDirection, currentWaypoint);        
    }

    @Override
    public Event peek(long currentSimtime) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(Event event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void offer(Event event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void dispatchEvent(Event e) {
        // TODO Auto-generated method stub
        
    }
}
