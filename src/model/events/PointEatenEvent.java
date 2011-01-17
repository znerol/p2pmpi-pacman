package model.events;

import deism.core.Event;

@SuppressWarnings("serial")
public class PointEatenEvent extends Event {

    private final int pac;
    private final int point;
    private final int points;
    
    public PointEatenEvent(int pac, int point, int points, long simtime) {
        super(simtime);
        
        this.pac = pac;
        this.point = point;
        this.points = points;
    }
    
    public int getPacman() {
        return this.pac;
    }
    
    public int getPoint() {
        return this.point;
    }
    
    public int getPoints() {
        return this.points;
    }

    @Override
    public String toString() {
        return (isAntimessage() ? "-" : "+") + "PointEatenEvent [simtime = "
                + getSimtime() + " pacman = " + pac + " point = "
                + point + " points = " + points + "]";
    }

}
