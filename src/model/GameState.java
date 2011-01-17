package model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import model.items.AbstractPoint;
import model.sprites.GhostState;
import model.sprites.PacmanState;

@SuppressWarnings("serial")
public class GameState implements Serializable {
    
    private final Map<Integer, PacmanState> pacmanStates;
    private final Map<Integer, GhostState> ghostStates;    
    private final Map<Integer, AbstractPoint> points;
    
    public GameState(Collection<PacmanState> pacmanStates, Collection<GhostState> ghostStates, Collection<AbstractPoint> points) {
        this.pacmanStates = new HashMap<Integer, PacmanState>();
        for (PacmanState state : pacmanStates) 
            this.pacmanStates.put(state.getId(), state);
        
        this.ghostStates = new HashMap<Integer, GhostState>();
        for (GhostState state : ghostStates) 
            this.ghostStates.put(state.getId(), state);
        
        this.points = new HashMap<Integer, AbstractPoint>();
        for (AbstractPoint state : points) 
            this.points.put(state.getId(), state);
    }

    /**
     * @return the pacmanStates
     */
    public Collection<PacmanState> getPacmanStates() {
        return pacmanStates.values();
    }

    /**
     * @return the ghostStates
     */
    public Collection<GhostState> getGhostStates() {
        return ghostStates.values();
    }

    /**
     * @return the points
     */
    public Collection<AbstractPoint> getPoints() {
        return points.values();
    }
    
    @Override
    public Object clone() {
        return new GameState(this.pacmanStates.values(), this.ghostStates.values(), this.points.values());
    }
}
