package model.events;

import deism.core.Event;

@SuppressWarnings("serial")
public class SpriteStoppedEvent extends Event {

    private int sprite;
    
    public SpriteStoppedEvent(int sprite, long simtime) {
        super(simtime);
        
        this.sprite = sprite;
    }
    
    public int getSprite() {
        return this.sprite;
    }

    @Override
    public String toString() {
        return (isAntimessage() ? "-" : "+") + "SpriteStoppedEvent [simtime = "
                + getSimtime() + " sprite = " + sprite + "]";
    }

}
