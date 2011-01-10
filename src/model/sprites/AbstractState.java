package model.sprites;

import paclib.GamePlay;
import model.Direction;
import model.Waypoint;

public abstract class AbstractState implements State {
    private Direction currentDirection;
    private Direction nextDirection;
    private Waypoint currentWaypoint;
    private int timestamp;
    private State prev;
    private State next;
    private int happyPillSteps = 0;
    
    public AbstractState(Direction currentDir, Direction nextDir, Waypoint waypoint, int time) {
        this.currentDirection = currentDir;
        this.nextDirection = nextDir;
        this.currentWaypoint = waypoint;
        this.timestamp = time;
    }
    
    public AbstractState(AbstractState state) {
        if (isMovingAllowed() && state.currentWaypoint.isDirectionAvailable(state.nextDirection)) {
            this.currentDirection = state.nextDirection;
            this.nextDirection = state.nextDirection;
        } else {
            this.currentDirection = state.currentDirection;
            this.nextDirection = state.nextDirection;
        }
        
        this.currentWaypoint = state.currentWaypoint.getNextWaypoint(this.currentDirection);
        this.timestamp = state.timestamp + 1;
        this.happyPillSteps = state.happyPillSteps - 1;
        this.prev = state;
        state.next = this;
    }
    
    protected abstract boolean isMovingAllowed();
    
    public void happyPillEaten() {
        this.happyPillSteps = GamePlay.SPECIAL_HAPPYPILL_STEPS;
    }
    
    public boolean isOnHappyPill() {
        return this.happyPillSteps > 0;
    }
    
    public int getHappyPillSteps() {
        return this.happyPillSteps;
    }
    
    public State getState(int time) {
        if (time == timestamp) 
            return this;
        else if (time > timestamp)
            return getNextState().getState(time);
        else if (time < timestamp) 
            return prev.getState(time);
        return null;
    }
    
    public State getPreviousState() {
        if (this.prev == null)
            throw new RuntimeException();
        return this.prev;
    }
    
    public State getNextState() {
        if (this.next == null)
            generateNextState();
        return this.next;
    }
    
    protected abstract void generateNextState();
    
    protected void setNextState(State state) {
        this.next = state;
    }
    
    protected void setPriviousState(State state) {
        this.prev = state;
    }
    
    public void setCurrentDirection(Direction dir) {
        this.currentDirection = dir;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }
    
    public void setNextDirection(Direction nextDir) {
        this.nextDirection = nextDir;
    }
    
    public Direction getNextDirection() {
        return nextDirection;
    }
    
    public void setCurrentWaypoint(Waypoint waypoint) {
        this.currentWaypoint = waypoint;
    }
    
    public Waypoint getCurrentWaypoint() {
        return currentWaypoint;
    }
    
    public void setTimestamp(int time) {
        this.timestamp = time;
    }
    
    public int getTimestamp() {
        return timestamp;
    }
}
