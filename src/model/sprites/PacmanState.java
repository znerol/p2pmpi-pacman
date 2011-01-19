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
public class PacmanState extends AbstractSpriteState implements EventVisitor {
    
    public PacmanState(Direction currentDir, Direction nextDir, Waypoint waypoint, int id) {
        super(currentDir, nextDir, waypoint, 0L, id);
    }
    
    public PacmanState(PacmanState pacman, Event event) {
        super(pacman, event);
    }
    
    public PacmanState(PacmanState pacman) {
        super(pacman);
    }

    @Override
    public String toString() {
        return "p";
    }

    @Override
    public void visit(DirectionEvent event) {
        if (event.getSprite() != this.getId())
            return;
        
        // TODO
    }

    @Override
    public void visit(CollisionEvent event) {
        // TODO
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
        return new PacmanState(this);
    }

    @Override
    public Event getEvent() {
        // TODO Auto-generated method stub
        return null;
    }
}
