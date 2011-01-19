package model.sprites;

import model.Direction;
import model.Waypoint;
import model.events.ChangeViewEvent;
import model.events.CollisionEvent;
import model.events.DirectionEvent;
import model.events.EnterJunctionEvent;
import model.events.EventVisitor;
import deism.core.Event;

@SuppressWarnings("serial")
public class Pacman extends AbstractSpriteState implements EventVisitor {
    private int id;
    private int points;
    
    public Pacman(Direction currentDir, Direction nextDir, Waypoint waypoint, int id) {
        super(currentDir, nextDir, waypoint, 0L);
        this.id = id;
        this.points = 0;
    }
    
    public Pacman(Pacman pacman, Event event) {
        super(pacman, event);
        this.points += pacman.points;
        this.id = pacman.id;
    }
    
    public Pacman(Pacman pacman, boolean move) {
        super(pacman);

        if (move) {
            this.points += pacman.points;
            move();
        } else 
            this.points = pacman.points;
        this.id = pacman.id;
    }

    @Override
    public String toString() {
        return "p";
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void visit(DirectionEvent event) {
        if (event.getSprite() != this.getId())
            return;
    }

    @Override
    public void visit(CollisionEvent event) {
        
    }

    @Override
    public void visit(ChangeViewEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(EnterJunctionEvent event) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Object clone() {
        return new Pacman(this, false);
    }
}
