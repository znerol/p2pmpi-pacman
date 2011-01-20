package model.sprites;

import java.util.List;

import paclib.GamePlay;

import model.Board;
import model.Direction;
import model.Model;
import model.StreetSegment;
import model.Triple;
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
        
        setDirectionToNextPac();
        
        if (!next.isDirectionAvailable(currentDirection)) 
            return null;
        
        do {
            next = next.getNextPointOfInterest(currentDirection);
        } while (next != null && (!next.isJunction()));
        
        if (next == null)
            return null;
        
        int distance = current.getDistance(next).b;

        if (next.isJunction())
            return new EnterJunctionEvent(getId(), next.getAbsoluteX(), next.getAbsoluteY(), distance + getTimestamp());
        if (next.isChangingView(this.currentDirection))
            return new ChangeViewEvent(getId(), next.getAbsoluteX(), next.getAbsoluteY(), distance + getTimestamp());
        return null;
    }

    private void setDirectionToNextPac() {
        List<Sprite> sprites = Model.getModel().getSprites();
        if (sprites == null)
            return;
        
        double distance = Double.MAX_VALUE;
        Waypoint target = null;
        Waypoint myWaypoint = Board.getBoard().getWaypoint(this.x, this.y);
        
        for(Sprite sprite : sprites) {
            if (sprite.isGhost()) 
                continue;
            
            Triple<Direction, Integer, Integer> pos = sprite.getCurrentState().nextPosition(this.timestamp);
            Waypoint otherWaypoint = Board.getBoard().getWaypoint(pos.b, pos.c);
            
            if (otherWaypoint == null)
                continue;
            
            if (myWaypoint.getOwner().directConnected(otherWaypoint.getOwner())) {
                double newDistance = myWaypoint.getEuclideanDistance(otherWaypoint);
                if (newDistance > GamePlay.GUI_FIELD_SIZE / 2 && newDistance < distance) {
                    distance = newDistance;
                    target = otherWaypoint;
                }
            }
        }
        
        if (target == null)
            return;
        
        StreetSegment myStreet = myWaypoint.getOwner();
        StreetSegment otherStreet = target.getOwner();
        Direction targetDir = Direction.None;
        
        if (myStreet.getX() > otherStreet.getX()) {
            targetDir = Direction.West;
        } else if (myStreet.getX() < otherStreet.getX()) {
            targetDir = Direction.East;
        } else if (myStreet.getY() > otherStreet.getY()) {
            targetDir = Direction.North;
        } else if (myStreet.getY() < otherStreet.getY()) {
            targetDir = Direction.South;
        }
        
        if (myWaypoint.getPossibleDirections().contains(targetDir)) {
            this.currentDirection = targetDir;
            this.nextDirection = Direction.None;
        } else {
            Direction newDirection = myWaypoint.getDirectionToCentre();
            if (newDirection == Direction.None) { 
                this.currentDirection = targetDir;
                this.nextDirection = Direction.None;
            }
            else {
                this.currentDirection = newDirection;
                this.nextDirection = targetDir;
            }
        }
        
        return;
    }
}
