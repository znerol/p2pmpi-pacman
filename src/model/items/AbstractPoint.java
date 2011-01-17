package model.items;

import model.Pair;
import model.events.DirectionEvent;
import model.events.EventVisitor;
import model.events.GhostEatenEvent;
import model.events.HappyPillEatenEvent;
import model.events.HappyPillTimeOutEvent;
import model.events.PacmanEatenEvent;
import model.events.PointEatenEvent;
import model.events.SpriteStoppedEvent;
import paclib.GamePlay;

@SuppressWarnings("serial")
public abstract class AbstractPoint implements Item, EventVisitor {
    private int owner;
    private Long time;
    private int id;
    private static int ID_OFFSET = Integer.MIN_VALUE;
    private Pair<Integer, Integer> position;
    
    protected AbstractPoint(int x, int y) {
        this.time = 0L;
        this.owner = Integer.MIN_VALUE;
        id = ID_OFFSET++;
        position = new Pair<Integer, Integer>(x, y);
    }
    
    protected AbstractPoint(AbstractPoint point, int ownerId, Long time) {
        this.owner = ownerId;
        this.time = time;
        this.id = point.id;
        this.position = point.position;
    }
    
    @Override
    public Pair<Integer, Integer> getPosition() {
        return this.position;
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
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractPoint p = (AbstractPoint)super.clone();
        p.owner = owner;
        p.time = time;
        p.id = id;
        p.position = position;
        return p;
    }
}
