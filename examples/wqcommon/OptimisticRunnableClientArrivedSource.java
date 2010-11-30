package wqcommon;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import deism.Event;
import deism.EventSource;
import deism.ExecutionGovernor;

public class OptimisticRunnableClientArrivedSource implements EventSource {
    private long mtbca;
    private long mstpc;
    private ExecutionGovernor mainGovernor;
    private ExecutionGovernor myGovernor;
    private final Random rng;
    private final Queue<Event> events;
    private final Worker worker = new Worker();

    public OptimisticRunnableClientArrivedSource(Random rng,
            ExecutionGovernor governor,
            long mean_time_between_customer_arrival,
            long mean_service_time_per_customer) {
        super();
        this.rng = rng;
        this.mainGovernor = governor;
        this.mtbca = mean_time_between_customer_arrival;
        this.mstpc = mean_service_time_per_customer;
        this.events = new ConcurrentLinkedQueue<Event>();
        try {
            this.myGovernor = (ExecutionGovernor) governor.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error(
                    "Classes implementing ExecutionGovernor must be clonable");
        }
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
    public Event receive(long currentSimtime) {
        return events.peek();
    }

    @Override
    public void reject(Event event) {
    }

    @Override
    public void accept(Event event) {
        events.remove(event);
    }

    private class Worker extends Thread {
        private boolean done = false;

        @Override
        public void run() {
            long currentSimtime = 0;

            while (!done) {
                long arrivalTime;
                long serviceTime;
                synchronized (rng) {
                    arrivalTime = currentSimtime
                            + (long) (mtbca * -Math.log(rng.nextDouble()));
                    serviceTime = (long) (mstpc * -Math.log(rng.nextDouble()));
                }
                Event e = new ClientArrivedEvent(arrivalTime, serviceTime);
                events.offer(e);
                mainGovernor.resume(e.getSimtime());

                long now = 0;
                while (now < arrivalTime && !done) {
                    now = myGovernor.suspendUntil(arrivalTime);
                }

                currentSimtime = arrivalTime;
            }
        }

        public void terminate() {
            done = true;
            if (isAlive()) {
                interrupt();
            }
        }
    }
}