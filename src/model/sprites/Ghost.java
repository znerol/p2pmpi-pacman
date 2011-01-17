package model.sprites;

import model.Direction;
import model.Waypoint;
import model.events.DirectionEvent;
import model.events.EventVisitor;
import model.events.GhostEatenEvent;
import model.events.HappyPillEatenEvent;
import model.events.HappyPillTimeOutEvent;
import model.events.PacmanEatenEvent;
import model.events.PointEatenEvent;
import model.events.SpriteStoppedEvent;
import deism.core.Event;


@SuppressWarnings("serial")
public class Ghost extends AbstractSpriteState implements EventVisitor {
    private int id;
    private int points;
    
    public Ghost(Direction currentDir, Direction nextDir, Waypoint waypoint, int id) {
        super(currentDir, nextDir, waypoint, 0L);
        this.id = id;
        this.points = 0;
    }
    
    public Ghost(Ghost Ghost, Event event) {
        super(Ghost, event);
        this.points += Ghost.points;
        this.id = Ghost.id;
    }
    
    public Ghost(Ghost Ghost, boolean move) {
        super(Ghost);
        this.id = Ghost.id;
        if (move) {
            this.points += Ghost.points;
            move();
        }
        else
            this.points = Ghost.points;
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
    public void visit(GhostEatenEvent event) {
        
    }

    @Override
    public void visit(HappyPillEatenEvent event) {
        
    }

    @Override
    public void visit(HappyPillTimeOutEvent event) {
        
    }

    @Override
    public void visit(PacmanEatenEvent event) {
        
    }

    @Override
    public void visit(SpriteStoppedEvent event) {
        
    }

    @Override
    public void visit(PointEatenEvent event) {
        
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Ghost(this, false);
    }
}
