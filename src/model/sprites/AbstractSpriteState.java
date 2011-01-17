package model.sprites;

import model.Direction;
import model.Model;
import model.Waypoint;
import model.events.EventVisitor;
import model.events.VisitableEvent;
import deism.core.Event;

@SuppressWarnings("serial")
public abstract class AbstractSpriteState implements MoveableSprite, EventVisitor {
    private Direction currentDirection;
    private Direction nextDirection;
    private int x;
    private int y;
    private final Long timestamp;
    
    protected AbstractSpriteState(Direction currentDir, Direction nextDir, Waypoint waypoint, Long time) {
        this.currentDirection = currentDir;
        this.nextDirection = nextDir;
        this.x = waypoint.getAbsoluteX();
        this.y = waypoint.getAbsoluteY();
        this.timestamp = time;
    }
    
    protected AbstractSpriteState(AbstractSpriteState state, Event event) {
        this.currentDirection = state.currentDirection;
        this.nextDirection = state.nextDirection;
        this.x = state.x;
        this.y = state.y;
        this.timestamp = event.getSimtime();
        
        if (event instanceof VisitableEvent) {
            VisitableEvent vEvent = (VisitableEvent) event;
            vEvent.accept(this);
        }
    }
    
    protected AbstractSpriteState(AbstractSpriteState state) {
        this.currentDirection = state.currentDirection;
        this.nextDirection = state.nextDirection;
        this.x = state.x;
        this.y = state.y;
        this.timestamp = state.timestamp + 1;
    }
    
    public void move() {
        Waypoint currentWaypoint = Model.getModel().getBoard().getWaypoint(this.x, this.y);
        
        // Updating directions if it is possible at the current waypoint -> Junction
        if (currentWaypoint.isDirectionAvailable(nextDirection)) {
            this.currentDirection = nextDirection;
        } 
        
        // Enters next waypoint if available
        if (currentWaypoint.isDirectionAvailable(this.currentDirection)) {
            currentWaypoint = currentWaypoint.getNextWaypoint(this.currentDirection);
        }
        
        // Stores only absolute positions for easier serialisation.
        this.x = currentWaypoint.getAbsoluteX();
        this.y = currentWaypoint.getAbsoluteY();
    }

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
    public abstract Object clone() throws CloneNotSupportedException;
}
