package model.sprites;

import model.Board;
import model.Direction;
import model.Pair;
import model.Triple;
import model.Waypoint;
import model.events.VisitableEvent;
import deism.core.Event;

@SuppressWarnings("serial")
public abstract class AbstractSpriteState implements MoveableSpriteState {
    protected Direction currentDirection;
    protected Direction nextDirection;
    protected int x;
    protected int y;
    protected Long timestamp;
    protected final int id;
    protected int wallDistance;
    
    protected AbstractSpriteState(Direction currentDir, Direction nextDir, Waypoint waypoint, Long time, int id) {
        this.currentDirection = currentDir;
        this.nextDirection = nextDir;
        this.x = waypoint.getAbsoluteX();
        this.y = waypoint.getAbsoluteY();
        this.timestamp = time;
        this.id = id;
    }
    
    protected AbstractSpriteState(AbstractSpriteState state, Event event) {
        this.currentDirection = state.currentDirection;
        this.nextDirection = state.nextDirection;
        this.x = state.x;
        this.y = state.y;
        this.timestamp = event.getSimtime();
        this.id = state.id;
        
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
        this.id = state.id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Triple<Direction, Integer, Integer> nextPosition(Long simTime) {
        assert(simTime > timestamp);
        
        // Distance equals to the elapsed time since v = 1s/t
        int distance = (int)(simTime - this.timestamp);
        
        // Max distance is the wall distance. There is no brick missing in the wall... ;-)
        // Wall distance is sufficient. If this distance will be disturbed, an other event
        // would break this.
        distance = distance > wallDistance ? wallDistance : distance;
        switch(currentDirection) {
        case North:
            return new Triple<Direction, Integer, Integer>(currentDirection, x, y - distance);
        case East:
            return new Triple<Direction, Integer, Integer>(currentDirection, x + distance, y);
        case South:
            return new Triple<Direction, Integer, Integer>(currentDirection, x, y + distance);
        case West:
            return new Triple<Direction, Integer, Integer>(currentDirection, x - distance, y);
            default:
        return new Triple<Direction, Integer, Integer>(currentDirection, x, y);
        }
    }
    
    @Override
    public void updateToTime(Long simTime) {
        assert(simTime > timestamp);
        
        Triple<Direction, Integer, Integer> nextPos = nextPosition(simTime);
        this.x = nextPos.b;
        this.y = nextPos.c;
        
        Waypoint currentWaypoint = Board.getBoard().getWaypoint(this.x, this.y);
        this.wallDistance = currentWaypoint.getDistanceToWall(this.currentDirection);
        
        this.timestamp = simTime;
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
}
