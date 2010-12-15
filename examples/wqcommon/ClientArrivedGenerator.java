package wqcommon;

import java.util.Random;

import org.apache.log4j.Logger;


import deism.core.Event;
import deism.core.StatefulEventGenerator;

public class ClientArrivedGenerator implements StatefulEventGenerator {
    /** Last customer arrival time */
    private long lcat;
    /** Mean time between customer arrival */
    private final long mtbca;
    /** Mean service time per customer */
    private final long mstpc;
    private final Random rng;
    private static final Logger logger = Logger.getLogger(ClientArrivedGenerator.class);

    public ClientArrivedGenerator(Random rng,
            long mean_time_between_customer_arrival,
            long mean_service_time_per_customer) {
        super();
        this.rng = rng;
        this.mtbca = mean_time_between_customer_arrival;
        this.mstpc = mean_service_time_per_customer;
    }

    @Override
    public Event poll() {
        Event event;
        long arrivalTime;
        long serviceTime;
        synchronized (rng) {
            arrivalTime = lcat + (long) (mtbca * -Math.log(rng.nextDouble()));
            serviceTime = (long) (mstpc * -Math.log(rng.nextDouble()));
        }
        event = new ClientArrivedEvent(arrivalTime, serviceTime);
        logger.debug("New Event: " + event);
        lcat = arrivalTime;

        return event;
    }
}
