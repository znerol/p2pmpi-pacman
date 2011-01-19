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
public class Ghost extends AbstractSpriteState implements EventVisitor {
    private int id;
    
    public Ghost(Direction currentDir, Direction nextDir, Waypoint waypoint, int id) {
        super(currentDir, nextDir, waypoint, 0L);
        this.id = id;
    }
    
    public Ghost(Ghost Ghost, Event event) {
        super(Ghost, event);
        this.id = Ghost.id;
    }
    
    public Ghost(Ghost Ghost, boolean move) {
        super(Ghost);
        this.id = Ghost.id;
        if (move) 
            move();
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
        return new Ghost(this, false);
    }
}
