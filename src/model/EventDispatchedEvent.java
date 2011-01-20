package model;

import java.util.EventObject;

import model.sprites.Sprite;
import deism.core.Event;

/**
 * Notifiert object for dispatched events on the local instance
 */
@SuppressWarnings("serial")
public class EventDispatchedEvent extends EventObject {

    private final Event event;

    public EventDispatchedEvent(Sprite source, Event event) {
        super(source);

        this.event = event;
    }

    /**
     * Dispatched event by sprite.
     * 
     * @return event that got dispatched
     */
    public Event getEvent() {
        return this.event;
    }

}
