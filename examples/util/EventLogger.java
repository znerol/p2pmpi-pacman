package util;

import deism.Event;
import deism.EventDispatcher;

public class EventLogger implements EventDispatcher {

    @Override
    public void dispatchEvent(Event e) {
        System.out.println(e);
    }

}
