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
        long mtbca;
        long mstpc;
        Event rejectedEvent;
        final Random rng;

        public ClientArrivedSource(Random rng,
                long mean_time_between_customer_arrival,
                long mean_service_time_per_customer) {
            super();
            this.rng = rng;
            this.mtbca = mean_time_between_customer_arrival;
            this.mstpc = mean_service_time_per_customer;
            rejectedEvent = null;
        }

        @Override
        public Event receive(long currentSimtime) {
            Event result = rejectedEvent;
            
            if (result == null) {
                long arrivalTime;
                long serviceTime;
                synchronized(rng) {
                    arrivalTime = currentSimtime
                            + (long) (mtbca * -Math.log(rng.nextDouble()));
                    serviceTime = (long) (mstpc * -Math.log(rng.nextDouble()));
                }
                result = new ClientArrivedEvent(arrivalTime, serviceTime);
                rejectedEvent = result;
            }
            
            rejectedEvent = null;
            return result;
        }

        @Override
        public void reject(Event event) {
            assert(rejectedEvent == null);
            rejectedEvent = event;
        }
    }

    private static class RunnableClientArrivedSource
            implements EventSource, Runnable {
        boolean done = false;
        long mtbca;
        long mstpc;
        ExecutionGovernor governor;
        Event rejectedEvent;
        long currentSimtime;
        final Random rng;
        Event eventReady;

        public RunnableClientArrivedSource(Random rng,
                ExecutionGovernor governor,
                long mean_time_between_customer_arrival,
                long mean_service_time_per_customer) {
            super();
            this.rng = rng;
            this.governor = governor;
            this.mtbca = mean_time_between_customer_arrival;
            this.mstpc = mean_service_time_per_customer;
            rejectedEvent = null;
        }

        @Override
        public synchronized Event receive(long currentSimtime) {
            Event result = rejectedEvent;

            this.currentSimtime = currentSimtime;
            if (result == null) {
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
                result = eventReady;
            }
            
            rejectedEvent = null;
            return result;
        }
        
        @Override
        public synchronized void reject(Event event) {
            assert(rejectedEvent == null);
            rejectedEvent = event;
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
                governor.resume(rejectedEvent.getSimtime());
                
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
        Event rejectedEvent;

        public ClerkSource(Queue<ClientArrivedEvent> jobs) {
            super();
            this.jobs = jobs;
            rejectedEvent = null;
        }

        @Override
        public Event receive(long currentSimtime) {
            Event result = rejectedEvent;
            
            if (result == null) {
                ClientArrivedEvent job = jobs.poll();
                if (job != null) {
                    System.out.println("[ClerkAccept: time=" + currentSimtime
                            + " " + job + "]");
                    System.out.println("Queue Length: " + jobs.size());
                    long nextClerkFreeTime = job.getServiceTime()
                            + Math.max(currentSimtime, job.getSimtime());
                    result = new ClerkFreeEvent(nextClerkFreeTime);
                }
            }
            
            rejectedEvent = null;
            return result;
        }

        @Override
        public void reject(Event event) {
            assert(rejectedEvent == null);
            rejectedEvent = event;
        }
    }
}
