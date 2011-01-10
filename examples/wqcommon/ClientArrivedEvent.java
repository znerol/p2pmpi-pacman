package wqcommon;

import deism.core.Event;

@SuppressWarnings("serial")
public class ClientArrivedEvent extends Event {
    long serviceTime;

    public ClientArrivedEvent(long arrivalTime, long serviceTime) {
        super(arrivalTime);
        this.serviceTime = serviceTime;
    }

    public long getServiceTime() {
        return serviceTime;
    }

    @Override
    public String toString() {
        return "[ClientArrivedEvent arrivalTime=" + this.getSimtime()
                + " serviceTime=" + serviceTime + "]";
    }
}
