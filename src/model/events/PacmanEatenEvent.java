package model.events;


@SuppressWarnings("serial")
public class PacmanEatenEvent extends VisitableEvent {
    private final int pac;
    private final int ghost;
    
    public PacmanEatenEvent(int pac, int ghost, long simtime) {
        super(simtime);
        
        this.pac = pac;
        this.ghost = ghost;
    }
    
    public int getPacman() {
        return this.pac;
    }
    
    public int getGhost() {
        return this.ghost;
    }

    @Override
    public String toString() {
        return (isAntimessage() ? "-" : "+") + "PacmanEatenEvent [simtime = "
                + getSimtime() + " pacman = " + pac + " ghost = "
                + ghost + "]";
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

}
