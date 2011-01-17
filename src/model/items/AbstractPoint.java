package model.items;

import model.events.DirectionEvent;
import model.events.EventVisitor;
import model.events.GhostEatenEvent;
import model.events.HappyPillEatenEvent;
import model.events.HappyPillTimeOutEvent;
import model.events.PacmanEatenEvent;
import model.events.PointEatenEvent;
import model.events.SpriteStoppedEvent;
import paclib.GamePlay;

public abstract class AbstractPoint implements Item, EventVisitor {
    private final int owner;
    private final Long time;
    private final int id;
    private static int ID_OFFSET = Integer.MIN_VALUE;
    
    protected AbstractPoint() {
        this.time = 0L;
        this.owner = Integer.MIN_VALUE;
        id = ID_OFFSET++;
    }
    
    protected AbstractPoint(AbstractPoint point, int ownerId, Long time) {
        this.owner = ownerId;
        this.time = time;
        this.id = point.id;
    }
    
    @Override
    public int getPoints() {
        return GamePlay.POINTS_PER_POINT;
    }

    @Override
    public int getOwnerId() {
        return owner;
    }

    @Override
    public boolean isEaten(Long time) {
        return this.owner > 0;
    }
    
    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Long getTimestamp() {
        return time;
    }

    @Override
    public void visit(DirectionEvent event) {
        
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
}
