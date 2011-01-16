package model.events;

import deism.core.Event;

@SuppressWarnings("serial")
public class SpriteChangedDirectionEvent extends Event {

    public SpriteChangedDirectionEvent(long simtime) {
        super(simtime);
    }

}
