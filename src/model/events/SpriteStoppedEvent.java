package model.events;

import deism.core.Event;

@SuppressWarnings("serial")
public class SpriteStoppedEvent extends Event {

    public SpriteStoppedEvent(long simtime) {
        super(simtime);
    }

}
