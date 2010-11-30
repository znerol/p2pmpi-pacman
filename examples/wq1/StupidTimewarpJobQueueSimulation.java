package wq1;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

import util.StateHistoryLogger;
import util.TerminateAfterDuration;

import deism.Event;
import deism.EventDispatcher;
import deism.EventCondition;
import deism.EventRunloop;
import deism.EventRunloopRecoveryStrategy;
import deism.EventSource;
import deism.EventSourceCollection;
import deism.ExecutionGovernor;
import deism.FastForwardRunloop;
import deism.RealtimeClock;
import deism.RealtimeExecutionGovernor;
import deism.StateHistory;
import deism.TimewarpEventSource;
import deism.TimewarpRunloopRecoveryStrategy;
import deism.TimewarpEventSourceAdapter;

public class StupidTimewarpJobQueueSimulation {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Random rng = new Random(1234);
        
        /* exit simulation after n units of simulation time */
        EventCondition termCond = new TerminateAfterDuration(1000 * 50);
        
        String speedString = System.getProperty("simulationSpeed", "1.0");
        double speed = Double.valueOf(speedString).doubleValue();
        
        ExecutionGovernor governor;
        RealtimeClock clock = new RealtimeClock(speed);
        governor = new RealtimeExecutionGovernor(clock);            

        EventSource clientSource;
        RunnableClientArrivedSource<Long> runnableClientSource = 
            new RunnableClientArrivedSource<Long>(rng, governor, 1000, 1600);
        clientSource = runnableClientSource;
        
        Thread producer = new Thread(runnableClientSource);
        producer.start();
        
        PriorityBlockingQueue<ClientArrivedEvent> jobs =
            new PriorityBlockingQueue<ClientArrivedEvent>();
        
        /* Define as many customer/clerk sources as you wish */
        EventSource[] sources = {
                clientSource,
                new ClerkSource(jobs),
                new ClerkSource(jobs)
        };
        
        TimewarpEventSource timewarpSources = 
            new TimewarpEventSourceAdapter(new EventSourceCollection(sources));

        EventCondition snapshotAll = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return true;
            }
        };

        EventDispatcher disp = new JobAggregator(jobs);

        ArrayList<StateHistory<Long>> stateObjects =
            new ArrayList<StateHistory<Long>>();
        stateObjects.add(new StateHistoryLogger());
        stateObjects.add(timewarpSources);

        EventRunloopRecoveryStrategy recoveryStrategy =
            new TimewarpRunloopRecoveryStrategy(stateObjects);

        EventRunloop runloop = new FastForwardRunloop(governor, termCond,
                recoveryStrategy, snapshotAll);
        
        runloop.run(timewarpSources, disp);
        
        runnableClientSource.stop();
        producer.interrupt();
        try {
            producer.join();
        }
        catch (InterruptedException e1) {
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

    private static class RunnableClientArrivedSource<K>
            implements EventSource, Runnable {
        private long mtbca;
        private long mstpc;
        private ExecutionGovernor mainGovernor;
        private ExecutionGovernor myGovernor;
        private final Random rng;
        private final Queue<Event> events;
        private boolean done = false;

        public RunnableClientArrivedSource(
                Random rng,
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
                throw new Error("Classes implementing ExecutionGovernor must be clonable");
            }
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
        
        @Override
        public void run() {
            long currentSimtime = 0;
                        
            while(!done) {
                long arrivalTime;
                long serviceTime;
                synchronized(rng) {
                    arrivalTime = currentSimtime
                            + (long) (mtbca * -Math.log(rng.nextDouble()));
                    serviceTime = (long) (mstpc * -Math.log(rng.nextDouble()));
                }
                Event e = new ClientArrivedEvent(arrivalTime, serviceTime);
                events.offer(e);
                mainGovernor.resume(e.getSimtime());
                
                long now = 0;
                while(now < arrivalTime && !done) {
                    now = myGovernor.suspendUntil(arrivalTime);
                };
                
                currentSimtime = arrivalTime;
            }
        }

        public void stop() {
            done = true;
        }
    }
    
    private static class ClerkSource implements EventSource {
        private final Queue<ClientArrivedEvent> jobs;
        private Event currentEvent = null;

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
