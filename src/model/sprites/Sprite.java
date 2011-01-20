package model.sprites;

import java.util.List;

import model.DispatchedListener;
import model.EventDispatchedEvent;
import model.Model;
import model.events.VisitableEvent;
import deism.core.Event;
import deism.process.DiscreteEventProcess;
import deism.stateful.AbstractStateHistory;

public class Sprite extends AbstractStateHistory<Long, MoveableSpriteState> implements DiscreteEventProcess, DispatchedListener {
    private MoveableSpriteState currentState;
    private final MoveableSpriteState initState;
    private Event currentEvent;
    
    public Sprite(MoveableSpriteState initState) {
        this.currentState = initState;
        this.initState = initState;
        this.currentEvent = initState.getEvent();
        pushHistory(currentState);
    }
    
    public MoveableSpriteState getInitState() {
        return this.initState;
    }
    
    public boolean isPacman() {
        return (currentState instanceof PacmanState);
    }
    
    public boolean isGhost() {
        return (currentState instanceof GhostState);
    }
    
    @Override
    public void revertHistory(List<MoveableSpriteState> tail) {
        if (tail.size() > 0) {
            currentState = tail.get(0);
            currentEvent = currentState.getEvent();
        }
    }

    @Override
    public Event peek(long currentSimtime) {
        return this.currentEvent;
    }

    @Override
    public void remove(Event event) {
        this.currentEvent = null;
        
    }

    @Override
    public void offer(Event event) {
        // not used
    }

    @Override
    public void dispatchEvent(Event e) {
        if (e instanceof VisitableEvent) {// && currentState.getEvent() == e) {
            VisitableEvent ve = (VisitableEvent)e;
            currentState = (MoveableSpriteState)currentState.clone();
            ve.accept(currentState);
            pushHistory(currentState);
            currentEvent = currentState.getEvent();
            Model.getModel().eventDispatched(this, e);
        }
    }

    @Override
    public void eventDispatched(EventDispatchedEvent event) {
        if (event.getSource() == this || this.currentEvent == null)
            return;
        
        if (this.currentEvent.getSimtime() > event.getEvent().getSimtime()) {
            currentState = (MoveableSpriteState)currentState.clone();
            currentState.updateToTime(event.getEvent().getSimtime());
            pushHistory(currentState);
            currentEvent = currentState.getEvent();
        }
            
    }

    public MoveableSpriteState getCurrentState() {
        return currentState;
    }
}
