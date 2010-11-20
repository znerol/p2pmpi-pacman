package wq1;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import deism.Event;
import deism.EventDispatcher;
import deism.EventMatcher;
import deism.EventSource;
import deism.EventSourceCollection;
import deism.ExecutionGovernor;
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
        EventMatcher termCond = new TerminateAfterDuration(1000 * 100);

        /* exit simulation after n events */
        // EventMatcher termCond = new TerminateAfterEventcount(1000 * 100);

        /* run simulation as fast as possible */
        ExecutionGovernor governor = new ImmediateExecutionGovernor();

        /* run simulation in realtime */
        // RealtimeClock clock = new RealtimeClock(10.0);
        // ExecutionGovernor governor = new RealtimeExecutionGovernor(clock);

        FastForwardRunloop runloop = new FastForwardRunloop(governor, termCond);

        PriorityQueue<ClientArrivedEvent> jobs = new PriorityQueue<ClientArrivedEvent>();

        /* Define as many customer/clerk sources as you wish */
        EventSource[] sources = {
                // new ClientArrivedSource(rng),
                // new ClientArrivedSource(rng),
                new ClientArrivedSource(rng, 1000, 1600),
                // new ClerkSource(jobs),
                new ClerkSource(jobs), new ClerkSource(jobs) };
        EventSource aggSource = new EventSourceCollection(sources);

        EventDispatcher disp = new JobAggregator(jobs);
        runloop.run(aggSource, disp);
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
            return (e.getSimtime() > duration);
        }

    }

    /**
     * TerminateAfterEventcount.match will return true after a given number of
     * events were dispatched.
     */
    private static class TerminateAfterEventcount implements EventMatcher {
        int remaining;

        public TerminateAfterEventcount(int count) {
            remaining = count;
        }

        @Override
        public boolean match(Event e) {
            return (--remaining < 0);
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

    private static class ClientArrivedSource implements EventSource {
        long mtbca;
        long mstpc;
        Event currentEvent;
        final Random rng;

        public ClientArrivedSource(Random rng,
                long mean_time_between_customer_arrival,
                long mean_service_time_per_customer) {
            super();
            this.rng = rng;
            this.mtbca = mean_time_between_customer_arrival;
            this.mstpc = mean_service_time_per_customer;
            currentEvent = null;
        }

        @Override
        public void compute(long currentSimtime) {
            if (currentEvent == null) {
                long arrivalTime = currentSimtime
                        + (long) (mtbca * -Math.log(rng.nextDouble()));
                long serviceTime = (long) (mstpc * -Math.log(rng.nextDouble()));
                currentEvent = new ClientArrivedEvent(arrivalTime, serviceTime);
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
    }
}
