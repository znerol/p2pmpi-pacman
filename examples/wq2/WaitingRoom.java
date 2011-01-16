package wq2;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import wqcommon.ClientArrivedEvent;

import deism.core.Event;
import deism.core.EventDispatcher;
import deism.core.EventSource;
import deism.core.Stateful;
import deism.stateful.AbstractStateHistory;

public class WaitingRoom {
    private Queue<ClientArrivedEvent> waitingQueue;
    private Queue<CounterAvailableEvent> availableCounters;
    public final WaitingRoom.Source source = new WaitingRoom.Source();
    public final WaitingRoom.Dispatcher dispatcher =
        new WaitingRoom.Dispatcher();
    public final WaitingRoom.StatisticsLogger statisticsLogger = 
        new WaitingRoom.StatisticsLogger();

    public WaitingRoom() {
        waitingQueue = new PriorityQueue<ClientArrivedEvent>();
        availableCounters = new PriorityQueue<CounterAvailableEvent>();
    }

    @Stateful
    public class Source
            extends AbstractStateHistory<Long, CounterServiceEvent>
            implements EventSource {
        
        private CounterServiceEvent currentEvent;
        
        @Override
        public Event peek(long currentSimtime) {
            if (currentEvent == null) {
                ClientArrivedEvent client = waitingQueue.peek();
                CounterAvailableEvent counter = availableCounters.peek();
                if (client != null && counter != null) {
                    currentEvent = new CounterServiceEvent(counter, client);
                }
            }

            return currentEvent;
        }

        @Override
        public void remove(Event event) {
            assert(event == currentEvent);
            boolean result;
            result = waitingQueue.remove(currentEvent.clientArrivedEvent);
            assert(result);
            result = availableCounters.remove(currentEvent.counterAvailableEvent);
            assert(result);
            pushHistory(currentEvent);
            currentEvent = null;
        }

        @Override
        public void revertHistory(List<CounterServiceEvent> tail) {
            for (CounterServiceEvent event : tail) {
                CounterServiceEvent serviceEvent =
                    (CounterServiceEvent) event;
                waitingQueue.offer(serviceEvent.clientArrivedEvent);
                availableCounters.offer(serviceEvent.counterAvailableEvent);
            }
        }
    }

    @Stateful
    public class Dispatcher extends AbstractStateHistory<Long, Event>
            implements EventDispatcher {
        
        @Override
        public void dispatchEvent(Event event) {
            if (event instanceof ClientArrivedEvent) {
                waitingQueue.offer((ClientArrivedEvent) event);
                pushHistory(event);
            }
            else if (event instanceof CounterAvailableEvent) {
                availableCounters.offer((CounterAvailableEvent) event);
                pushHistory(event);
            }
        }

        @Override
        public void revertHistory(List<Event> tail) {
            for (Event event : tail) {
                boolean result;
                if (event instanceof ClientArrivedEvent) {
                    result = waitingQueue.remove((ClientArrivedEvent) event);
                    assert(result);
                }
                else if (event instanceof CounterAvailableEvent) {
                    result = 
                        availableCounters.remove((CounterAvailableEvent) event);
                    assert(result);
                }
            }
        }
    }
    
    public class StatisticsLogger implements EventDispatcher {        
        @Override
        public void dispatchEvent(Event e) {
            System.out.println("Queue Length: " + waitingQueue.size());
        }
    }
}