package wqcommon;

import java.util.Random;

import org.apache.log4j.Logger;


import deism.core.Event;
import deism.core.EventSource;

public class ClientArrivedSource implements EventSource {
    /** Last customer arrival time */
    private long lcat;
    /** Mean time between customer arrival */
    private final long mtbca;
    /** Mean service time per customer */
    private final long mstpc;
    private Event currentEvent = null;
    private final Random rng;
    private static final Logger logger = Logger.getLogger(ClientArrivedSource.class);

    public ClientArrivedSource(Random rng,
            long mean_time_between_customer_arrival,
            long mean_service_time_per_customer) {
        super();
        this.rng = rng;
        this.mtbca = mean_time_between_customer_arrival;
        this.mstpc = mean_service_time_per_customer;
    }

    @Override
    public Event peek(long currentSimtime) {
        if (currentEvent == null) {
            long arrivalTime;
            long serviceTime;
            synchronized(rng) {
                arrivalTime = lcat
                        + (long) (mtbca * -Math.log(rng.nextDouble()));
                serviceTime = (long) (mstpc * -Math.log(rng.nextDouble()));
            }
            currentEvent = new ClientArrivedEvent(arrivalTime, serviceTime);
            logger.debug("New Event: " + currentEvent);
            lcat = arrivalTime;
        }
        
        return currentEvent;
    }

    @Override
    public void remove(Event event) {
        assert(currentEvent == event);
        currentEvent = null;
    }

    @Override
    public void start(long startSimtime) {
        lcat = startSimtime;
    }

    @Override
    public void stop() {
    }
}
