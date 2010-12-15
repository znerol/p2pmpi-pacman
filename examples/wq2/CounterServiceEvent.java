package wq2;

import wqcommon.ClientArrivedEvent;
import deism.core.Event;

@SuppressWarnings("serial")
public class CounterServiceEvent extends Event {
    public final CounterAvailableEvent counterAvailableEvent;
    public final ClientArrivedEvent clientArrivedEvent;
    
    public CounterServiceEvent(CounterAvailableEvent counter,
            ClientArrivedEvent client) {
        super(Math.max(counter.getSimtime(), client.getSimtime()));
        this.counterAvailableEvent = counter;
        this.clientArrivedEvent = client;
    }
    
    @Override
    public String toString() {
        return "[CounterServiceEvent time=" + this.getSimtime() + " " +
            counterAvailableEvent + " " + clientArrivedEvent + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }

        CounterServiceEvent otherEvent = (CounterServiceEvent)other;
        return
            this.counterAvailableEvent.equals(otherEvent.counterAvailableEvent)
            && this.clientArrivedEvent.equals(otherEvent.clientArrivedEvent);
    }
}

