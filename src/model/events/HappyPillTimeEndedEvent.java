package model.events;

import deism.core.Event;
@SuppressWarnings("serial")
public class HappyPillTimeEndedEvent extends Event {
    
    public HappyPillTimeEndedEvent(long simtime) {
        super(simtime);
    }

    @Override
    public String toString() {
        return (isAntimessage() ? "-" : "+") + "HappyPillTimeEndedEvent [simtime = "
                + getSimtime() + "]";
    }
}
