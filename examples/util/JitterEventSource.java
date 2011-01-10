package util;

import java.util.Random;

import deism.core.Event;
import deism.core.EventSource;
import deism.core.Stateful;

@Stateful
public class JitterEventSource implements EventSource {
    private Event currentEvent;
    private final Random rng = new Random(0);
    
    @Override
    public Event peek(long currentSimtime) {
        if (currentEvent == null && currentSimtime > 1024) {
            if (rng.nextInt(32) == 0) {
                currentEvent = new Event(currentSimtime - rng.nextInt(1024));
            }
        }
        
        return currentEvent;
    }

    @Override
    public void remove(Event event) {
        assert(currentEvent == event);
        currentEvent = null;
    }
}
