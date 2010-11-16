package deism;

public abstract class AbstractGeneraterorEventSource implements EventSource {
    private Event currentEvent = null;
    private long lastEventSimtime = 0;

    @Override
    public Event peek() {
        if (currentEvent == null) {
            currentEvent = nextEvent();
            if (currentEvent == null) {
                return null;
            }
            lastEventSimtime = currentEvent.getSimtime();
        }
        return currentEvent;
    }

    @Override
    public Event poll() {
        Event e = peek();
        currentEvent = null;
        return e;
    }

    public long getLastEventSimtime() {
        return lastEventSimtime;
    }

    public abstract Event nextEvent();
}
