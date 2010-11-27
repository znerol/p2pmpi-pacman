package wq1;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import deism.AbstractStateHistory;
import deism.Event;
import deism.EventDispatcher;
import deism.EventCondition;
import deism.EventDispatcherCollection;
import deism.EventRunloop;
import deism.EventRunloopRecoveryStrategy;
import deism.EventSource;
import deism.EventSourceCollection;
import deism.ExecutionGovernor;
import deism.FastForwardRunloop;
import deism.RealtimeClock;
import deism.RealtimeExecutionGovernor;
import deism.StateHistory;
import deism.StateHistoryException;
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
        EventCondition termCond = new TerminateAfterDuration(1000 * 50);
        
        String speedString = System.getProperty("simulationSpeed", "1.0");
        double speed = Double.valueOf(speedString).doubleValue();
        
        ExecutionGovernor governor;
        RealtimeClock clock = new RealtimeClock(speed);
        governor = new RealtimeExecutionGovernor(clock);            

//        RunnableClientArrivedSource<Long> runnableClientSource = 
//            new RunnableClientArrivedSource<Long>(rng, governor, 1000, 1600);
//        Thread producer = new Thread(runnableClientSource);
//        producer.start();
        
        EventSource clientSource = new ClientArrivedSource(rng, 1000, 1600);
        TimewarpEventSource timewarpClientSource =
            new TimewarpEventSourceAdapter(clientSource);
        
        WaitingRoom waitingRoom = new WaitingRoom();
        Counter counterOne = new Counter();
        Counter counterTwo = new Counter();
        
        TimewarpEventSource jitterSource = new TimewarpEventSourceAdapter(
                new JitterEventSource());

        EventSource[] sources = {
                timewarpClientSource,
                waitingRoom.source,
                counterOne.source,
                counterTwo.source,
                jitterSource,
        };
        
        EventDispatcher eventLogger = new EventLogger(waitingRoom);
        EventDispatcher[] dispatchers = {
                waitingRoom.dispatcher,
                counterOne.dispatcher,
                counterTwo.dispatcher,
                eventLogger,
        };
        
        EventCondition snapshotAll = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return true;
            }
        };
        
        ArrayList<StateHistory<Long>> stateObjects =
            new ArrayList<StateHistory<Long>>();
        stateObjects.add(new StateHistoryLogger());
        stateObjects.add(timewarpClientSource);
        stateObjects.add(waitingRoom.dispatcher);
        stateObjects.add(waitingRoom.source);
        stateObjects.add(counterOne.dispatcher);
        stateObjects.add(counterOne.source);
        stateObjects.add(counterTwo.dispatcher);
        stateObjects.add(counterTwo.source);
        stateObjects.add(jitterSource);

        EventRunloopRecoveryStrategy recoveryStrategy =
            new TimewarpRunloopRecoveryStrategy(stateObjects);

        EventRunloop runloop = new FastForwardRunloop(governor, termCond,
                recoveryStrategy, snapshotAll);
        
        EventSource eventSource = new EventSourceCollection(sources);
        EventDispatcher eventDispatcher =
            new EventDispatcherCollection(dispatchers);
        
        runloop.run(eventSource, eventDispatcher);
        
//        runnableClientSource.stop();
//        producer.interrupt();
//        try {
//            producer.join();
//        }
//        catch (InterruptedException e1) {
//        }
    }

    private static class StateHistoryLogger implements StateHistory<Long> {

        @Override
        public void save(Long timestamp) throws StateHistoryException {
        }
        
        @Override
        public void rollback(Long timestamp) {
            System.out.println("** Rollback time=" + timestamp);
        }

        @Override
        public void commit(Long timestamp) {
            System.out.println("** Commit time=" + timestamp);
        }
    }
    
    private static class JitterEventSource implements EventSource {
        private Event rejectedEvent;
        private final Random rng = new Random(0);
        
        @Override
        public Event receive(long currentSimtime) {
            Event result = rejectedEvent;
            
            if (result == null && currentSimtime > 1024) {
                if (rng.nextInt(32) == 0) {
                    result = new Event(currentSimtime - rng.nextInt(1024));
                }
            }
            
            rejectedEvent = null;
            return result;
        }

        @Override
        public void reject(Event event) {
            assert(event == null);
            rejectedEvent = event;
        }
    }
    
    private static class EventLogger implements EventDispatcher {
        private final WaitingRoom waitingRoom;
        
        public EventLogger(WaitingRoom waitingRoom) {
            this.waitingRoom = waitingRoom;
        }
        
        @Override
        public void dispatchEvent(Event e) {
            System.out.println(e);
            waitingRoom.dumpStatistics();
        }
    }
    
    /**
     * TerminateAfterDuration.match will return true after given amount of
     * simulation time elapsed.
     */
    private static class TerminateAfterDuration implements EventCondition {
        private long duration;

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
    
    @SuppressWarnings("serial")
    private static class CounterServiceEvent extends Event {
        public final CounterAvailableEvent counterAvailableEvent;
        public final ClientArrivedEvent clientArrivedEvent;
        
        public CounterServiceEvent(CounterAvailableEvent counter,
                ClientArrivedEvent client) {
            super(Math.max(counter.getSimtime(), client.getSimtime()));
            this.counterAvailableEvent = counter;
            this.clientArrivedEvent = client;
        }
        
        @Override
        public String toString() {
            return "[CounterServiceEvent time=" + this.getSimtime() + " " +
                counterAvailableEvent + " " + clientArrivedEvent + "]";
        }
    }

    private static class WaitingRoom {
        private Queue<ClientArrivedEvent> waitingQueue;
        private Queue<CounterAvailableEvent> availableCounters;
        public final WaitingRoom.Source source = new WaitingRoom.Source();
        public final WaitingRoom.Dispatcher dispatcher =
            new WaitingRoom.Dispatcher();

        public WaitingRoom() {
            waitingQueue = new PriorityQueue<ClientArrivedEvent>();
            availableCounters = new PriorityQueue<CounterAvailableEvent>();            
        }
        
        public void dumpStatistics() {
            System.out.println("Queue Length: " + waitingQueue.size());
        }
        
        public class Source extends AbstractStateHistory<Long, Event>
                implements TimewarpEventSource {
            
            private Event rejectedEvent;
            
            @Override
            public Event receive(long currentSimtime) {
                Event result = rejectedEvent;
                rejectedEvent = null;

                if (result == null && waitingQueue.peek() != null
                        && availableCounters.peek() != null) {
                    result = new CounterServiceEvent(availableCounters.poll(),
                            waitingQueue.poll());
                    addToHistory(result);
                }

                return result;
            }

            @Override
            public void reject(Event event) {
                assert (rejectedEvent == null);
                removeFromHistory(event);
                rejectedEvent = event;
            }

            @Override
            public void addPending(List<Event> pending) {
                for (Event event : pending) {
                    assert (event instanceof CounterServiceEvent);
                    CounterServiceEvent serviceEvent =
                        (CounterServiceEvent) event;
                    waitingQueue.offer(serviceEvent.clientArrivedEvent);
                    availableCounters.offer(serviceEvent.counterAvailableEvent);
                }
            }
        }
        
        public class Dispatcher extends AbstractStateHistory<Long, Event>
                implements EventDispatcher {
            
            @Override
            public void dispatchEvent(Event event) {
                if (event instanceof ClientArrivedEvent) {
                    waitingQueue.offer((ClientArrivedEvent) event);
                    addToHistory(event);
                }
                else if (event instanceof CounterAvailableEvent) {
                    availableCounters.offer((CounterAvailableEvent) event);
                    addToHistory(event);
                }
            }

            @Override
            public void addPending(List<Event> pending) {
                for (Event event : pending) {
                    if (event instanceof ClientArrivedEvent) {
                        waitingQueue.remove((ClientArrivedEvent) event);
                    }
                    else if (event instanceof CounterAvailableEvent) {
                        availableCounters.remove((CounterAvailableEvent) event);
                    }
                }
            }
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
    private static class CounterAvailableEvent extends Event {
        public final Counter counter;
        
        public CounterAvailableEvent(long simtime, Counter counter) {
            super(simtime);
            this.counter = counter;
        }

        @Override
        public String toString() {
            return "[CounterAvailableEvent time=" + this.getSimtime() + "]";
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

    private static class RunnableClientArrivedSource<K>
            implements EventSource, Runnable {
        long mtbca;
        long mstpc;
        ExecutionGovernor mainGovernor;
        ExecutionGovernor myGovernor;
        final Random rng;
        final Queue<Event> events;
        boolean done = false;
        Event lastEvent;

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
            lastEvent = events.poll();
            return lastEvent;
        }

        @Override
        public void reject(Event event) {
            assert (event == lastEvent);
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
    
    private static class Counter {
        private final Queue<CounterServiceEvent> waitingQueue =
            new PriorityQueue<CounterServiceEvent>();
        public final Counter.Source source = new Counter.Source();
        public final Counter.Dispatcher dispatcher = new Counter.Dispatcher();

        public class Source extends AbstractStateHistory<Long, Event> implements
                TimewarpEventSource {

            private Event rejectedEvent =
                new CounterAvailableEvent(0, Counter.this);

            @Override
            public Event receive(long currentSimtime) {
                Event result = rejectedEvent;
                rejectedEvent = null;

                if (result == null && waitingQueue.peek() != null) {
                    CounterServiceEvent job = waitingQueue.poll();
                    long nextClerkFreeTime =
                        job.clientArrivedEvent.getServiceTime()
                        + Math.max(currentSimtime, job.getSimtime());
                    result = new CounterAvailableEvent(
                            nextClerkFreeTime, Counter.this);
                    addToHistory(job);
                }

                return result;
            }

            @Override
            public void reject(Event event) {
                assert (rejectedEvent == null);
                removeFromHistory(event);
                rejectedEvent = event;
            }

            @Override
            public void addPending(List<Event> pending) {
                for (Event event : pending) {
                    assert (event instanceof CounterServiceEvent);
                    waitingQueue.offer((CounterServiceEvent) event);
                }
                if (pending.size() > 0) {
                    rejectedEvent = null;
                }
            }
        }

        public class Dispatcher extends AbstractStateHistory<Long, Event>
                implements EventDispatcher {

            @Override
            public void dispatchEvent(Event event) {
                if (event instanceof CounterServiceEvent) {
                    CounterServiceEvent cse = (CounterServiceEvent)event;
                    if (cse.counterAvailableEvent.counter == Counter.this) {
                        waitingQueue.offer(cse);
                        addToHistory(event);
                    }
                }
            }

            @Override
            public void addPending(List<Event> pending) {
                for (Event event : pending) {
                    if (event instanceof CounterServiceEvent) {
                        waitingQueue.remove((CounterServiceEvent) event);
                    }
                }
            }
        }
    }
}
