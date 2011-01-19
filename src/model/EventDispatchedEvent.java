package model;

import java.util.EventObject;

import model.sprites.Sprite;
import deism.core.Event;

@SuppressWarnings("serial")
public class EventDispatchedEvent extends EventObject {

    private final Event event;
    
    public EventDispatchedEvent(Sprite source, Event event) {
        super(source);
        
        this.event = event;
    }
    
    public Event getEvent() {
        return this.event;
    }

}
