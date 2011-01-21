package model.sprites;

import java.util.List;

import model.DispatchedListener;
import model.EventDispatchedEvent;
import model.Model;
import model.events.VisitableEvent;
import deism.core.Event;
import deism.process.DiscreteEventProcess;
import deism.stateful.AbstractStateHistory;

/**
 * Implements a ghost respectively a pacman with its state history. Further
 * provides the implementation a {@link deism.core.EventSource} and
 * {@link deism.core.EventDispatcher}.
 */
public class Sprite extends AbstractStateHistory<Long, MovableSpriteState>
        implements DiscreteEventProcess, DispatchedListener {
    private final int spriteId;
    private MovableSpriteState currentState;
    private final MovableSpriteState initState;
    private Event currentEvent;

    public Sprite(MovableSpriteState initState) {
        this.spriteId = initState.getId();
        this.currentState = initState;
        this.initState = initState;
        this.currentEvent = initState.getEvent();
        pushHistory(currentState);
    }

    /**
     * The sprites id
     * 
     * @return sprite id
     */
    public int getSpriteId() {
        return this.spriteId;
    }

    /**
     * Getter for initial state to reset sprite
     * 
     * @return init state
     */
    public MovableSpriteState getInitState() {
        return this.initState;
    }

    /**
     * True if own state is instanceof {@link model.sprites.PacmanState}
     * 
     * @return true if pacman.
     */
    public boolean isPacman() {
        return (currentState instanceof PacmanState);
    }

    /**
     * True if own state is instanceof {@link model.sprites.GhostState}
     * 
     * @return true if ghost.
     */
    public boolean isGhost() {
        return (currentState instanceof GhostState);
    }

    @Override
    public void revertHistory(List<MovableSpriteState> tail) {
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
            VisitableEvent ve = (VisitableEvent) e;
            currentState = (MovableSpriteState) currentState.clone();
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

        if (this.currentState.getTimestamp() > event.getEvent().getSimtime())
            return;
        
        if (this.currentEvent.getSimtime() > event.getEvent().getSimtime()) {
            currentState = (MovableSpriteState) currentState.clone();
            currentState.updateToTime(event.getEvent().getSimtime());
            pushHistory(currentState);
            currentEvent = currentState.getEvent();
        }

    }

    /**
     * Getter for current state.
     * 
     * @return current state
     */
    public MovableSpriteState getCurrentState() {
        return currentState;
    }
}
