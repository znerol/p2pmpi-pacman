package model.events;

import deism.core.Event;

@SuppressWarnings("serial")
public class PointEatenEvent extends Event {

    public PointEatenEvent(long simtime) {
        super(simtime);
    }

}
