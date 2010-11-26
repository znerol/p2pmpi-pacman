package wq1;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

import deism.AbstractStateHistory;
import deism.Event;
import deism.EventDispatcher;
import deism.EventMatcher;
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

public class TimewarpJobQueueSimulation {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Random rng = new Random(1234);
        
        /* exit simulation after n units of simulation time */
        EventMatcher termCond = new TerminateAfterDuration(1000 * 50);
        
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

        EventMatcher snapshotAll = new EventMatcher() {
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

    private static class StateHistoryLogger
            extends AbstractStateHistory<Long, Object> {

        @Override
        public void rollback(Long timestamp) {
            super.rollback(timestamp);
            System.out.println("Rollback time=" + timestamp);
        }

        @Override
        public void commit(Long timestamp) {
            super.rollback(timestamp);
            System.out.println("Commit time=" + timestamp);
        }
        
        @Override
        public void addPending(List<Object> pending) {
        }
    }
    /**
     * TerminateAfterDuration.match will return true after given amount of
     * simulation time elapsed.
     */
    private static class TerminateAfterDuration implements EventMatcher {
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
        long mtbca;
        long mstpc;
        ExecutionGovernor mainGovernor;
        ExecutionGovernor myGovernor;
        final Random rng;
        final Queue<Event> events;
        boolean done = false;

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
        public void compute(long currentSimtime) {
        }

        @Override
        public Event peek() {
            return events.peek();
        }

        @Override
        public Event poll() {
            return events.poll();
        }

        @Override
        public void offer(Event event) {
            events.offer(event);
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
        Queue<ClientArrivedEvent> jobs;
        Event currentEvent;

        public ClerkSource(Queue<ClientArrivedEvent> jobs) {
            super();
            this.jobs = jobs;
            currentEvent = null;
        }

        @Override
        public void compute(long currentSimtime) {
            if (currentEvent == null) {
                ClientArrivedEvent job = jobs.poll();
                if (job != null) {
                    long nextClerkFreeTime = job.getServiceTime()
                            + Math.max(currentSimtime, job.getSimtime());
                    currentEvent = new ClerkFreeEvent(nextClerkFreeTime);
                }
            }
        }

        @Override
        public Event peek() {
            return currentEvent;
        }

        @Override
        public Event poll() {
            Event e = currentEvent;
            currentEvent = null;
            return e;
        }

        @Override
        public void offer(Event event) {
            assert(currentEvent == null);
            currentEvent = event;
        }
    }
}
