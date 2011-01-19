package model.sprites;

import model.Direction;
import model.Model;
import model.Pair;
import model.Waypoint;
import model.events.ChangeViewEvent;
import model.events.CollisionEvent;
import model.events.DirectionEvent;
import model.events.EnterJunctionEvent;
import model.events.EventVisitor;
import model.events.VisitableEvent;
import deism.core.Event;

@SuppressWarnings("serial")
public abstract class AbstractSpriteState implements MoveableSprite, EventVisitor {
    private Direction currentDirection;
    private Direction nextDirection;
    private int x;
    private int y;
    private Long timestamp;
    
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
        this.timestamp = state.timestamp;
    }

    @Override
    public Sprite nextPosition(Long simTime) {
        assert(simTime > timestamp);
        
        Sprite newSprite = (Sprite)this.clone();
        
        while(newSprite.getTimestamp() < simTime) {
            move();
        }
        
        return newSprite;
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
        this.timestamp++;
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
    
    public Pair<Integer, Integer> getPosition() {
        return new Pair<Integer, Integer>(x, y);
    }
    
    @Override
    public Long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public abstract Object clone();

    @Override
    public int getId() {
        // TODO Auto-generated method stub
        return 0;
    }
}
