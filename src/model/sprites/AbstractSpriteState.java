package model.sprites;

import model.Board;
import model.Direction;
import model.Waypoint;
import model.events.VisitableEvent;
import deism.core.Event;
import deism.util.Pair;
import deism.util.Triple;

/**
 * Implements the sprite state structure. Every state is immutable after
 * processing the current event
 */
@SuppressWarnings("serial")
public abstract class AbstractSpriteState implements MovableSpriteState {
    protected Direction currentDirection;
    protected Direction nextDirection;
    protected int x;
    protected int y;
    protected Long timestamp;
    protected final int id;
    protected int wallDistance;

    protected AbstractSpriteState(Direction currentDir, Direction nextDir,
            Waypoint waypoint, Long time, int id) {
        this.currentDirection = currentDir;
        this.nextDirection = nextDir;
        this.x = waypoint.getAbsoluteX();
        this.y = waypoint.getAbsoluteY();
        this.timestamp = time;
        this.id = id;
        this.wallDistance = waypoint.getDistanceToWall(this.currentDirection);
    }

    protected AbstractSpriteState(AbstractSpriteState state, Event event) {
        this.currentDirection = state.currentDirection;
        this.nextDirection = state.nextDirection;
        this.x = state.x;
        this.y = state.y;
        this.timestamp = event.getSimtime();
        this.id = state.id;
        this.wallDistance = state.wallDistance;

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
        this.wallDistance = state.wallDistance;
    }

    @Override
    public Pair<Integer, Integer> getOrigin() {
        return new Pair<Integer, Integer>(this.x, this.y);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Triple<Direction, Integer, Integer> nextPosition(Long simTime) {
        if (simTime < this.timestamp)
            return new Triple<Direction, Integer, Integer>(currentDirection, x, y);
        //assert(simTime >= this.timestamp);
        
        // Distance equals to the elapsed time since v = 1s/t
        int distance = (int) (simTime - this.timestamp);

        // Max distance is the wall distance. There is no brick missing in the
        // wall... ;-)
        // Wall distance is sufficient. If this distance will be disturbed, an
        // other event
        // would break this.
        distance = distance > wallDistance ? wallDistance : distance;
        switch (currentDirection) {
        case North:
            return new Triple<Direction, Integer, Integer>(currentDirection, x,
                    y - distance);
        case East:
            return new Triple<Direction, Integer, Integer>(currentDirection, x
                    + distance, y);
        case South:
            return new Triple<Direction, Integer, Integer>(currentDirection, x,
                    y + distance);
        case West:
            return new Triple<Direction, Integer, Integer>(currentDirection, x
                    - distance, y);
        default:
            return new Triple<Direction, Integer, Integer>(currentDirection, x,
                    y);
        }
    }

    @Override
    public void updateToTime(Long simTime) {
        Triple<Direction, Integer, Integer> nextPos = nextPosition(simTime);
        this.x = nextPos.b;
        this.y = nextPos.c;

        Waypoint currentWaypoint = Board.getBoard().getWaypoint(this.x, this.y);
        this.wallDistance = currentWaypoint
                .getDistanceToWall(this.currentDirection);

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

    /**
     * Current abscissa
     * 
     * @return current position on board
     */
    public int getX() {
        return this.x;
    }

    /**
     * Current ordinate
     * 
     * @return current position on board
     */
    public int getY() {
        return this.y;
    }

    /**
     * Current position
     * 
     * @return current position on board
     */
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
