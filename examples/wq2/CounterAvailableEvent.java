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

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }

        CounterAvailableEvent otherEvent=(CounterAvailableEvent)other;
        return this.counter.equals(otherEvent.counter);
    }
}
