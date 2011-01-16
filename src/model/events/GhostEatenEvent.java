package model.events;

import model.sprites.Ghost;
import model.sprites.Pacman;
import deism.core.Event;

@SuppressWarnings("serial")
public class GhostEatenEvent extends Event {
    private final Pacman pac;
    private final Ghost ghost;

    public GhostEatenEvent(Pacman pac, Ghost ghost, long simtime) {
        super(simtime);
        
        this.pac = pac;
        this.ghost = ghost;
    }
    
    public Pacman getPacman() {
        return this.pac;
    }
    
    public Ghost getGhost() {
        return this.ghost;
    }

}
