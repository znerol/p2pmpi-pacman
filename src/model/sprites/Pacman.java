package model.sprites;

import java.util.List;

import model.Model;
import model.events.DirectionEvent;
import model.events.EventVisitor;
import model.events.GhostEatenEvent;
import model.events.HappyPillEatenEvent;
import model.events.HappyPillTimeOutEvent;
import model.events.PacmanEatenEvent;
import model.events.PointEatenEvent;
import model.events.SpriteStoppedEvent;
import model.events.VisitableEvent;
import deism.core.Event;
import deism.core.EventDispatcher;
import deism.stateful.AbstractStateHistory;

public class Pacman extends AbstractStateHistory<Long, PacmanState> implements Sprite, EventDispatcher, EventVisitor {
    private int id;
    private PacmanState currentState;
    private final Model model;
    
    public Pacman(PacmanState initState, Model model) {
        this.pushHistory(initState);
        this.currentState = initState;
        this.id = initState.getId();
        this.model = model;
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
    public void revertHistory(List<PacmanState> tail) {
        if (tail.size() > 0)
            currentState = tail.get(0);
    }
    
    @Override
    public void dispatchEvent(Event e) {
        if (e instanceof VisitableEvent) {
            VisitableEvent event = (VisitableEvent) e;
            event.accept(this);
        }
    }

    @Override
    public void visit(DirectionEvent event) {
        if (event.getSprite() != this.getId())
            return;
        
        currentState = new PacmanState(currentState, event.getDirection(), model.)
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
