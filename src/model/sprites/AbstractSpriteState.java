package model.sprites;

import model.Direction;
import model.Waypoint;

public abstract class AbstractSpriteState implements SpriteState {
    private final Direction currentDirection;
    private final Direction nextDirection;
    private final int x;
    private final int y;
    private final int ownerId;
    private final Long timestamp;
    
    protected AbstractSpriteState(Direction currentDir, Direction nextDir, Waypoint waypoint, Long time, int ownerId) {
        this.currentDirection = currentDir;
        this.nextDirection = nextDir;
        this.x = waypoint.getAbsoluteX();
        this.y = waypoint.getAbsoluteY();
        this.timestamp = time;
        this.ownerId = ownerId;
    }
    
    protected AbstractSpriteState(AbstractSpriteState state, Direction nextDirection, Waypoint currentWaypoint) {
        // Updating directions if it is possible at the current waypoint -> Junction
        if (currentWaypoint.isDirectionAvailable(nextDirection)) {
            this.currentDirection = nextDirection;
            this.nextDirection = nextDirection;
        } else {
            this.currentDirection = state.currentDirection;
            this.nextDirection = nextDirection;
        }
        
        // Enters next waypoint if available
        if (currentWaypoint.isDirectionAvailable(this.currentDirection)) {
            currentWaypoint = currentWaypoint.getNextWaypoint(this.currentDirection);
        }
        
        // Stores only absolute positions for easier serialisation.
        this.x = currentWaypoint.getAbsoluteX();
        this.y = currentWaypoint.getAbsoluteY();
        
        this.timestamp = state.timestamp + 1;
        this.ownerId = state.ownerId;
    }
    
    public abstract State transaction(Direction nextDirection, Waypoint currentWaypoint);

    @Override
    public Direction getCurrentDirection() {
        return currentDirection;
    }
    
    @Override
    public Direction getNextDirection() {
        return nextDirection;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    @Override
    public Long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public int getId() {
        return this.ownerId;
    }
}
