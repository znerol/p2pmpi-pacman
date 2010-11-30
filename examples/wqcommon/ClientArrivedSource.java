package wqcommon;

import java.util.Random;


import deism.Event;
import deism.EventSource;

public class ClientArrivedSource implements EventSource {
    private long mtbca;
    private long mstpc;
    private Event currentEvent = null;
    private final Random rng;

    public ClientArrivedSource(Random rng,
            long mean_time_between_customer_arrival,
            long mean_service_time_per_customer) {
        super();
        this.rng = rng;
        this.mtbca = mean_time_between_customer_arrival;
        this.mstpc = mean_service_time_per_customer;
    }

    @Override
    public Event receive(long currentSimtime) {
        if (currentEvent == null) {
            long arrivalTime;
            long serviceTime;
            synchronized(rng) {
                arrivalTime = currentSimtime
                        + (long) (mtbca * -Math.log(rng.nextDouble()));
                serviceTime = (long) (mstpc * -Math.log(rng.nextDouble()));
            }
            currentEvent = new ClientArrivedEvent(arrivalTime, serviceTime);
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
