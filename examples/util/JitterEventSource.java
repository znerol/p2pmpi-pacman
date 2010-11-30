package util;

import java.util.Random;

import deism.Event;
import deism.EventSource;

public class JitterEventSource implements EventSource {
    private Event currentEvent;
    private final Random rng = new Random(0);
    
    @Override
    public Event receive(long currentSimtime) {
        if (currentEvent == null && currentSimtime > 1024) {
            if (rng.nextInt(32) == 0) {
                currentEvent = new Event(currentSimtime - rng.nextInt(1024));
            }
        }
        
        return currentEvent;
    }

    @Override
    public void reject(Event event) {
    }

    @Override
    public void accept(Event event) {
        assert(currentEvent == event);
        currentEvent = null;
    }
}
