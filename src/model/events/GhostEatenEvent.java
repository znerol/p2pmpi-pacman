package model.events;

import model.sprites.Ghost;
import model.sprites.Pacman;

@SuppressWarnings("serial")
public class GhostEatenEvent extends VisitableEvent {
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

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

}
