package deism;

public abstract class AbstractGeneraterorEventSource implements EventSource {
    private Event currentEvent = null;
    private long lastEventSimtime = 0;
    
    @Override
    public void compute(long currentSimtime) {
        if (currentEvent == null) {
            currentEvent = nextEvent(currentSimtime);
            if (currentEvent != null) {
                lastEventSimtime = currentEvent.getSimtime();
            }
        }
    }
    
    @Override
    public Event peek(long currentSimtime) {
        return currentEvent;
    }

    @Override
    public Event poll(long currentSimtime) {
        Event e = peek(currentSimtime);
        currentEvent = null;
        return e;
    }

    public long getLastEventSimtime() {
        return lastEventSimtime;
    }

    public abstract Event nextEvent(long currentSimtime);
}
