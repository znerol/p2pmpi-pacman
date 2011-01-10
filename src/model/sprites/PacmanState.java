package model.sprites;

import java.util.HashSet;
import java.util.Set;

import model.Direction;
import model.Waypoint;
import model.items.Item;

public class PacmanState extends AbstractState {
    private int points;
    private final Set<Item> items;

    public PacmanState(Direction currentDir, Direction nextDir,
            Waypoint waypoint) {
        super(currentDir, nextDir, waypoint, 0);
        
        this.items = new HashSet<Item>();
    }

    public PacmanState(PacmanState state) {
        super(state);
        
        this.items = new HashSet<Item>(state.items);
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
