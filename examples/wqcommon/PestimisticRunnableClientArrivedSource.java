package wqcommon;

import java.util.Random;

import deism.Event;
import deism.EventSource;
import deism.ExecutionGovernor;

public class PestimisticRunnableClientArrivedSource implements EventSource {
    private long mtbca;
    private long mstpc;
    private final ExecutionGovernor governor;
    private Event currentEvent = null;
    private long currentSimtime;
    private final Random rng;
    private Event eventReady;
    private final Worker worker = new Worker();

    public PestimisticRunnableClientArrivedSource(Random rng,
            ExecutionGovernor governor,
            long mean_time_between_customer_arrival,
            long mean_service_time_per_customer) {
        super();
        this.rng = rng;
        this.governor = governor;
        this.mtbca = mean_time_between_customer_arrival;
        this.mstpc = mean_service_time_per_customer;
    }

    @Override
    public void start(long startSimtime) {
        worker.start();
    }

    @Override
    public void stop() {
        worker.terminate();
    }

    @Override
    public synchronized Event receive(long currentSimtime) {
        this.currentSimtime = currentSimtime;
        if (currentEvent == null) {
            eventReady = null;
            this.notify();
            while (eventReady == null) {
                try {
                    this.wait();
                }
                catch (InterruptedException e) {
                    // do nothing
                }
            }
            currentEvent = eventReady;
        }

        return currentEvent;
    }

    @Override
    public synchronized void reject(Event event) {
    }

    @Override
    public synchronized void accept(Event event) {
        assert (currentEvent == event);
        currentEvent = null;
    }

    private class Worker extends Thread {
        private boolean done = false;

        @Override
        public void run() {
            synchronized (PestimisticRunnableClientArrivedSource.this) {
                while (!done) {
                    long arrivalTime;
                    long serviceTime;
                    synchronized (rng) {
                        arrivalTime = currentSimtime
                                + (long) (mtbca * -Math.log(rng.nextDouble()));
                        serviceTime = (long) (mstpc * -Math.log(rng
                                .nextDouble()));
                    }
                    eventReady = new ClientArrivedEvent(arrivalTime,
                            serviceTime);
                    governor.resume(eventReady.getSimtime());

                    PestimisticRunnableClientArrivedSource.this.notify();
                    while (!done && eventReady != null) {
                        try {
                            PestimisticRunnableClientArrivedSource.this.wait();
                        }
                        catch (InterruptedException e) {
                            // do nothing
                        }
                    }
                }
            }
        }

        public void terminate() {
            synchronized (PestimisticRunnableClientArrivedSource.this) {
                done = true;
                if (isAlive()) {
                    interrupt();
                }
            }
        }
    }
}
