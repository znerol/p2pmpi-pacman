package model.sprites;

import model.Board;
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
        // Will never happen for a ghost
    }

    @Override
    public void visit(CollisionEvent event) {
        // do nothing, pac's thing
    }

    @Override
    public void visit(ChangeViewEvent event) {
        // Ghosts can not walk through walls but see...
        if (event.getSprite() != getId())
            return;
        
        updateToTime(event.getSimtime());
    }

    @Override
    public void visit(EnterJunctionEvent event) {
        if (event.getSprite() != getId())
            return;
        
        updateToTime(event.getSimtime());
        
        if (nextDirection != Direction.None) {
            currentDirection = nextDirection;
        } else {
            currentDirection = Model.getModel().getRandomDirection(x, y, currentDirection);
        }
        nextDirection = Direction.None;
    }
    
    @Override
    public Object clone() {
        return new GhostState(this);
    }

    @Override
    public Event getEvent() {
        Waypoint current = Board.getBoard().getWaypoint(x, y);
        Waypoint next = current;
        
        if (!next.isDirectionAvailable(currentDirection)) 
            return null;
        
        do {
            next = next.getNextPointOfInterest(currentDirection);
        } while (next != null && (!next.isJunction()));
                
        int distance = current.getDistance(next).b;
        
        return new EnterJunctionEvent(getId(), next.getAbsoluteX(), next.getAbsoluteY(), distance + getTimestamp());
    }
}
