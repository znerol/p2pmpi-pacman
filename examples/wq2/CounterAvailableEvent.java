package wq2;

import deism.Event;

@SuppressWarnings("serial")
public class CounterAvailableEvent extends Event {
    public final Counter counter;
    
    public CounterAvailableEvent(long simtime, Counter counter) {
        super(simtime);
        this.counter = counter;
    }

    @Override
    public String toString() {
        return "[CounterAvailableEvent time=" + this.getSimtime() + "]";
    }
}
