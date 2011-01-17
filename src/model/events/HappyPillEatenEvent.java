package model.events;

import deism.core.Event;

@SuppressWarnings("serial")
public class HappyPillEatenEvent extends Event {

    private final int pac;
    private final int pill;
    private final int points;
    
    public HappyPillEatenEvent(int pac, int pill, int points, long simtime) {
        super(simtime);
        
        this.pac = pac;
        this.pill = pill;
        this.points = points;
    }
    
    public int getPacman() {
        return this.pac;
    }
    
    public int getHappyPill() {
        return this.pill;
    }
    
    public int getPoints() {
        return this.points;
    }

    @Override
    public String toString() {
        return (isAntimessage() ? "-" : "+") + "HappyPillEatenEvent [simtime = "
                + getSimtime() + " Pacman = " + pac + " pill = "
                + pill + " points = " + points + "]";
    }
}
