package model.sprites;

import model.Board;
import model.Direction;
import model.Waypoint;
import model.events.ChangeViewEvent;
import model.events.CollisionEvent;
import model.events.DirectionEvent;
import model.events.EnterJunctionEvent;
import model.events.EventVisitor;
import deism.core.Event;

/**
 * Implements a pacman state which is immutable after the update to a given
 * envent.
 */
@SuppressWarnings("serial")
public class PacmanState extends AbstractSpriteState implements EventVisitor {

    public PacmanState(Direction currentDir, Direction nextDir,
            Waypoint waypoint, int id) {
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

        updateToTime(event.getSimtime());

        Waypoint waypoint = Board.getBoard().getWaypoint(this.x, this.y);

        if (waypoint.isDirectionAvailable(event.getDirection())
                || currentDirection == Direction.None) {
            this.currentDirection = event.getDirection();
            this.nextDirection = Direction.None;
        } else {
            this.nextDirection = event.getDirection();
        }

        this.timestamp = event.getSimtime();
    }

    @Override
    public void visit(CollisionEvent event) {
        // if (event.getSprite1() != this.getId() && event.getSprite2() !=
        // this.getId())
        // return;
        //
        // updateToTime(event.getSimtime());
        //
        // Sprite other = null;
        // Sprite me = Model.getModel().getSprite(getId());
        //
        // if (event.getSprite1() != this.getId())
        // other = Model.getModel().getSprite(event.getSprite1());
        // if (other == null)
        // other = Model.getModel().getSprite(event.getSprite2());
        //
        // if (other.isGhost()) {
        // this.x = me.getInitState().getOrigin().a;
        // this.y = me.getInitState().getOrigin().b;
        // }
        //
        // this.currentDirection = Direction.None;
    }

    @Override
    public void visit(ChangeViewEvent event) {
        if (event.getSprite() != getId())
            return;

        updateToTime(event.getSimtime());
        // has to do nothing.
        // On event dispatching, all other sprites will get informed
        // and they will all update their behaviour if necessary
    }

    @Override
    public void visit(EnterJunctionEvent event) {
        if (event.getSprite() != this.getId())
            return;

        updateToTime(event.getSimtime());

        if (this.nextDirection != Direction.None
                && Board.getBoard().getWaypoint(x, y)
                        .isDirectionAvailable(this.nextDirection)) {
            this.currentDirection = this.nextDirection;
            this.nextDirection = Direction.None;
        } // else if (!Board.getBoard().getWaypoint(x,
          // y).isDirectionAvailable(this.currentDirection))
          // this.currentDirection = Direction.None;

    }

    @Override
    public Object clone() {
        return new PacmanState(this);
    }

    @Override
    public Event getEvent() {
        Waypoint current = Board.getBoard().getWaypoint(x, y);
        Waypoint next = current;

        if (!next.isDirectionAvailable(currentDirection))
            return null;

        next = next.getNextPointOfInterest(currentDirection);

        if (next == null)
            return null;

        int distance = current.getDistance(next).b;

        if (next.isJunction())
            return new EnterJunctionEvent(getId(), next.getAbsoluteX(),
                    next.getAbsoluteY(), distance + getTimestamp());
        if (next.isChangingView(this.currentDirection))
            return new ChangeViewEvent(getId(), next.getAbsoluteX(),
                    next.getAbsoluteY(), distance + getTimestamp());
        return null;
    }
}