package pingpong;

import deism.core.Event;
import deism.core.EventSource;

public class BallEventSource implements EventSource {

    private final long t0;
    private final long dt;
    private final int sender;
    private final int receiver;
    
    public BallEventSource(long start, long interval, int sender, int receiver) {
        this.t0 = start;
        this.dt = interval;
        this.sender = sender;
        this.receiver = receiver;
    }
    
    @Override
    public Event peek(long currentSimtime) {
        Event result = null;
        
        long n = Math.max(0, (currentSimtime - t0) / dt + 1);
        long next = t0 + n * dt;
        result = new BallEvent(next, sender, receiver);
        
        return result;
    }

    @Override
    public void remove(Event event) {
    }

    @Override
    public void start(long startSimtime) {
    }

    @Override
    public void stop() {
    }
}
