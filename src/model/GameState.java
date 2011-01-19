package model;

import java.io.Serializable;

import model.events.ChangeViewEvent;
import model.events.CollisionEvent;
import model.events.DirectionEvent;
import model.events.EnterJunctionEvent;
import model.events.EventVisitor;
import model.events.VisitableEvent;
import model.sprites.Ghost;
import model.sprites.Pacman;
import model.sprites.Sprite;

@SuppressWarnings("serial")
public class GameState implements EventVisitor, Serializable {
    private final Sprite[] sprites;
    //private final VisitableEvent event;
    
    public GameState(Sprite[] sprites) {
        if (sprites == null)
            this.sprites = new Sprite[0];
        else
            this.sprites = cloneSprites(sprites);
    }
    
    /**
     * Creates the next simulation step
     * @param state
     */
    public GameState(GameState state) {
        this.sprites = cloneSprites(state.sprites);
    }
    
    private Sprite[] cloneSprites(Sprite[] sprites) {
        Sprite[] result = new Sprite[sprites.length];
        for (int i = 0; i < sprites.length; i++) {
            result[i] = (Sprite)sprites[i].clone();
        }
        
        return result;
    }
    
    public Pacman getPacman(int id) {
        Sprite sprite = this.sprites[id];
        if (sprite instanceof Pacman)
            return (Pacman) sprite;
        return null;
    }
    
    public Ghost getGhost(int id) {
        Sprite sprite = this.sprites[id];
        if (sprite instanceof Ghost)
            return (Ghost) sprite;
        return null;
    }
    
    public Sprite getSprite(int id) {
        if (id < 0 || this.sprites.length <= id)
            throw new IndexOutOfBoundsException("sprites");
        
        return this.sprites[id];
    }
    
    @Override
    public Object clone() {
        return new GameState(this);
    }

    @Override
    public void visit(DirectionEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(CollisionEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(ChangeViewEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(EnterJunctionEvent event) {
        // TODO Auto-generated method stub
        
    }
}
