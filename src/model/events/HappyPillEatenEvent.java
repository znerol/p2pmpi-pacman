package model.events;

import model.items.HappyPill;
import model.items.Point;
import model.sprites.Pacman;
import deism.core.Event;

@SuppressWarnings("serial")
public class HappyPillEatenEvent extends Event {

    private final Pacman pac;
    private final HappyPill pill;
    
    public HappyPillEatenEvent(Pacman pac, HappyPill pill, long simtime) {
        super(simtime);
        
        this.pac = pac;
        this.pill = pill;
    }
    
    public Pacman getPacman() {
        return this.pac;
    }
    
    public HappyPill getHappyPill() {
        return this.pill;
    }
}
