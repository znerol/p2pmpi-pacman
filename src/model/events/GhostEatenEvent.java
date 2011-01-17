package model.events;

import model.sprites.Ghost;
import model.sprites.Pacman;
import deism.core.Event;

@SuppressWarnings("serial")
public class GhostEatenEvent extends Event {
    private final int pac;
    private final int ghost;

    public GhostEatenEvent(Pacman pac, Ghost ghost, long simtime) {
        super(simtime);
        
        this.pac = pac.getId();
        this.ghost = ghost.getId();
    }
    
    public int getPacman() {
        return this.pac;
    }
    
    public int getGhost() {
        return this.ghost;
    }

    @Override
    public String toString() {
        return (isAntimessage() ? "-" : "+") + "GhostEatenEvent [simtime = "
                + getSimtime() + " Pacman = " + pac + " ghost = "
                + ghost + "]";
    }

}
