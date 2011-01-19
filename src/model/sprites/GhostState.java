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
public class GhostState extends AbstractSpriteState implements EventVisitor {
    
    public GhostState(Direction currentDir, Direction nextDir, Waypoint waypoint, int id) {
        super(currentDir, nextDir, waypoint, 0L, id);
    }
    
    public GhostState(GhostState Ghost, Event event) {
        super(Ghost, event);
    }
    
    public GhostState(GhostState Ghost) {
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
        return new GhostState(this);
    }

    @Override
    public Event getEvent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateTo(Long simTime) {
        // TODO Auto-generated method stub
        
    }
}
