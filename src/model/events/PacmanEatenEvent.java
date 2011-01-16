package model.events;

import deism.core.Event;

@SuppressWarnings("serial")
public class PacmanEatenEvent extends Event {

    public PacmanEatenEvent(long simtime) {
        super(simtime);
    }

}
