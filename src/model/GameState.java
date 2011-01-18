package model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import model.items.AbstractPoint;
import model.sprites.Ghost;
import model.sprites.Pacman;

@SuppressWarnings("serial")
public class GameState implements Serializable {
    private final Map<Integer, Pacman> pacmans = new HashMap<Integer, Pacman>();
    private final Map<Integer, Ghost> ghosts = new HashMap<Integer, Ghost>();
    private final Map<Pair<Integer, Integer>, AbstractPoint> points = new HashMap<Pair<Integer, Integer>, AbstractPoint>();
    private final int happyPillSteps;
    
    public GameState(Collection<Pacman> pacmans, Collection<Ghost> ghosts, Collection<AbstractPoint> points, int happyPillSteps) {
        for (Pacman state : pacmans) 
            this.pacmans.put(state.getId(), state);
        
        for (Ghost state : ghosts) 
            this.ghosts.put(state.getId(), state);
        
        for (AbstractPoint state : points) 
            this.points.put(state.getPosition(), state);
        
        this.happyPillSteps = happyPillSteps;
    }
    
    /**
     * Creates the next simulation step
     * @param state
     */
    public GameState(GameState state) {
        for (Map.Entry<Integer, Pacman> pac : state.pacmans.entrySet())
            pacmans.put(pac.getKey(), new Pacman(pac.getValue(), true));

        for (Map.Entry<Integer, Ghost> ghost : state.ghosts.entrySet())
            ghosts.put(ghost.getKey(), new Ghost(ghost.getValue(), true));
        
        for (Map.Entry<Pair<Integer, Integer>, AbstractPoint> point : state.points.entrySet())
            try {
                points.put(point.getKey(), (AbstractPoint)(point.getValue().clone()));
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        
        this.happyPillSteps = state.happyPillSteps == 0 ? 0 : state.happyPillSteps - 1;
    }

    /**
     * @return the Pacmans
     */
    public Collection<Pacman> getPacmans() {
        return pacmans.values();
    }
    
    public Pacman getPacman(int id) {
        return pacmans.get(id);
    }
    
    public Ghost getGhost(int id) {
        return ghosts.get(id);
    }
    
    public AbstractPoint getPoint(int id) {
        return points.get(id);
    }

    /**
     * @return the Ghosts
     */
    public Collection<Ghost> getGhosts() {
        return ghosts.values();
    }

    /**
     * @return the points
     */
    public Collection<AbstractPoint> getPoints() {
        return points.values();
    }
    
    @Override
    public Object clone() {
        return new GameState(this.pacmans.values(), this.ghosts.values(), this.points.values(), happyPillSteps);
    }
}
