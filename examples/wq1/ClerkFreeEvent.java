package wq1;

import deism.Event;

@SuppressWarnings("serial")
public class ClerkFreeEvent extends Event {
    public ClerkFreeEvent(long simtime) {
        super(simtime);
    }

    @Override
    public String toString() {
        return "[ClerkFreeEvent time=" + this.getSimtime() + "]";
    }
}
