package wq1;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import deism.Event;
import deism.EventDispatcher;
import deism.EventCondition;
import deism.EventRunloopRecoveryStrategy;
import deism.EventSource;
import deism.EventSourceCollection;
import deism.ExecutionGovernor;
import deism.FailFastRunloopRecoveryStrategy;
import deism.FastForwardRunloop;
import deism.ImmediateExecutionGovernor;
import deism.RealtimeClock;
import deism.RealtimeExecutionGovernor;

public class JobQueueSimulation {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Random rng = new Random(1234);
        
        /* exit simulation after n units of simulation time */
        EventCondition termCond = new TerminateAfterDuration(1000 * 50);
        
        String speedString = System.getProperty("simulationSpeed", "0");
        double speed = Double.valueOf(speedString).doubleValue();
        
        ExecutionGovernor governor;
        if (speed > 0) {
            /* run simulation in realtime */
            RealtimeClock clock = new RealtimeClock(10.0);
            governor = new RealtimeExecutionGovernor(clock);            
        }
        else {
            /* run simulation as fast as possible */
            governor = new ImmediateExecutionGovernor();
        }

        boolean multithread = Boolean.getBoolean("simulationMultithread");
        EventSource clientSource;
        Thread producer = null;
        RunnableClientArrivedSource runnableClientSource = null;
        if (multithread) {
            runnableClientSource = 
                new RunnableClientArrivedSource(rng, governor, 1000, 1600);
            clientSource = runnableClientSource;
        
            producer = new Thread(runnableClientSource);
            producer.start();
        }
        else {
            clientSource = new ClientArrivedSource(rng, 1000, 1600);
        }
        
        PriorityBlockingQueue<ClientArrivedEvent> jobs =
            new PriorityBlockingQueue<ClientArrivedEvent>();
        /* Define as many customer/clerk sources as you wish */
        EventSource[] sources = {
                clientSource,
                new ClerkSource(jobs),
                new ClerkSource(jobs)
        };
        
        EventSource aggSource = new EventSourceCollection(sources);
        
        EventRunloopRecoveryStrategy recoveryStrategy =
            new FailFastRunloopRecoveryStrategy();

        EventCondition noSnapshots = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return false;
            }
        };
        
        FastForwardRunloop runloop = new FastForwardRunloop(governor, termCond,
                recoveryStrategy, noSnapshots);
        EventDispatcher disp = new JobAggregator(jobs);
        runloop.run(aggSource, disp);
        
        if (producer != null && runnableClientSource != null) {
            runnableClientSource.stop();
            producer.interrupt();
            try {
                producer.join();
            }
            catch (InterruptedException e1) {
            }
        }
    }

    /**
     * TerminateAfterDuration.match will return true after given amount of
     * simulation time elapsed.
     */
    private static class TerminateAfterDuration implements EventCondition {
        long duration;

        public TerminateAfterDuration(long duration) {
            this.duration = duration;
        }

        @Override
        public boolean match(Event e) {
            boolean result = false;
            if (e != null) {
                result = (e.getSimtime() > duration);
            }
            return result;
        }

    }

    private static class JobAggregator implements EventDispatcher {
        Queue<ClientArrivedEvent> waitingQueue;

        public JobAggregator(Queue<ClientArrivedEvent> events) {
            waitingQueue = events;
        }

        @Override
        public void dispatchEvent(Event e) {
            System.out.println(e);
            if (e instanceof ClientArrivedEvent) {
                waitingQueue.offer((ClientArrivedEvent) e);
            }
            System.out.println("Queue Length: " + waitingQueue.size());
        }
    }

    @SuppressWarnings("serial")
    private static class ClientArrivedEvent extends Event {
        long serviceTime;

        public ClientArrivedEvent(long arrivalTime, long serviceTime) {
            super(arrivalTime);
            this.serviceTime = serviceTime;
        }

        long getServiceTime() {
            return serviceTime;
        }

        @Override
        public String toString() {
            return "[ClientArrivedEvent arrivalTime=" + this.getSimtime()
                    + " serviceTime=" + serviceTime + "]";
        }
    }

    @SuppressWarnings("serial")
    private static class ClerkFreeEvent extends Event {
        public ClerkFreeEvent(long simtime) {
            super(simtime);
        }

        @Override
        public String toString() {
            return "[ClerkFreeEvent time=" + this.getSimtime() + "]";
        }
    }

    private static class ClientArrivedSource implements EventSource {
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

    private static class RunnableClientArrivedSource
            implements EventSource, Runnable {
        private boolean done = false;
        private long mtbca;
        private long mstpc;
        private final ExecutionGovernor governor;
        private Event currentEvent = null;
        private long currentSimtime;
        private final Random rng;
        private Event eventReady;

        public RunnableClientArrivedSource(Random rng,
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
        public synchronized Event receive(long currentSimtime) {
            this.currentSimtime = currentSimtime;
            if (currentEvent == null) {
                eventReady = null;
                this.notify();
                while (!done && eventReady == null) {
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
            assert(currentEvent == event);
            currentEvent = null;
        }

        @Override
        public synchronized void run() {
            while(!done) {
                long arrivalTime;
                long serviceTime;
                synchronized(rng) {
                    arrivalTime = currentSimtime
                            + (long) (mtbca * -Math.log(rng.nextDouble()));
                    serviceTime = (long) (mstpc * -Math.log(rng.nextDouble()));
                }
                eventReady = new ClientArrivedEvent(arrivalTime, serviceTime);
                governor.resume(eventReady.getSimtime());

                this.notify();
                while (!done && eventReady != null) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException e) {
                        // do nothing
                    }
                }
            }
        }

        public void stop() {
            done = true;
        }
    }

    private static class ClerkSource implements EventSource {
        Queue<ClientArrivedEvent> jobs;
        Event currentEvent = null;

        public ClerkSource(Queue<ClientArrivedEvent> jobs) {
            this.jobs = jobs;
        }

        @Override
        public Event receive(long currentSimtime) {
            if (currentEvent == null) {
                ClientArrivedEvent job = jobs.poll();
                if (job != null) {
                    System.out.println("[ClerkAccept: time=" + currentSimtime
                            + " " + job + "]");
                    System.out.println("Queue Length: " + jobs.size());
                    long nextClerkFreeTime = job.getServiceTime()
                            + Math.max(currentSimtime, job.getSimtime());
                    currentEvent = new ClerkFreeEvent(nextClerkFreeTime);
                }
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
}
