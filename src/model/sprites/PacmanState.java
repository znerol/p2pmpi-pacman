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
        
        this.nextDirection = event.getDirection();
        this.timestamp = event.getSimtime();
    }

    @Override
    public void visit(CollisionEvent event) {
        // TODO
    }

    @Override
    public void visit(ChangeViewEvent event) {
        // has to do nothing.
        // On event dispatching, all other sprites will get informed
        // and they will all update their behaviour if necessary
    }

    @Override
    public void visit(EnterJunctionEvent event) {
        if (event.getSprite() != this.getId())
            return;
        
        this.x = event.getX();
        this.y = event.getY();
        
        this.timestamp = event.getSimtime();
        
        this.currentDirection = this.nextDirection;
    }
    
    @Override
    public Object clone() {
        return new PacmanState(this);
    }

    @Override
    public Event getEvent() {
        // TODO Auto-generated method stub
        // Nächstes Event berechnen
        return null;
    }
}
