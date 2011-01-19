package model.sprites;

import model.Direction;
import model.Model;
import model.Waypoint;
import model.events.ChangeViewEvent;
import model.events.CollisionEvent;
import model.events.DirectionEvent;
import model.events.EnterJunctionEvent;
import model.events.EventVisitor;
import deism.core.Event;

@SuppressWarnings("serial")
public class Ghost extends AbstractSpriteState implements EventVisitor {
    
    public Ghost(Direction currentDir, Direction nextDir, Waypoint waypoint, int id) {
        super(currentDir, nextDir, waypoint, 0L, id);
    }
    
    public Ghost(Ghost Ghost, Event event) {
        super(Ghost, event);
    }
    
    public Ghost(Ghost Ghost) {
        super(Ghost);
    }

    @Override
    public String toString() {
        return "p";
    }

    @Override
    public void visit(DirectionEvent event) {

    }

    @Override
    public void visit(CollisionEvent event) {
        // do nothing, pac thing
    }

    @Override
    public void visit(ChangeViewEvent event) {
    }

    @Override
    public void visit(EnterJunctionEvent event) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Object clone() {
        return new Ghost(this);
    }
}
