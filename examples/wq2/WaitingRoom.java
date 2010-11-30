package wq2;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import wqcommon.ClientArrivedEvent;

import deism.AbstractStateHistory;
import deism.Event;
import deism.EventDispatcher;
import deism.TimewarpEventSource;

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
    
    public class Source
            extends AbstractStateHistory<Long, CounterServiceEvent>
            implements TimewarpEventSource {
        
        private CounterServiceEvent currentEvent;
        
        @Override
        public Event receive(long currentSimtime) {
            if (currentEvent == null) {
                ClientArrivedEvent client = waitingQueue.peek();
                CounterAvailableEvent counter = availableCounters.peek();
                if (client != null && counter != null) {
                    currentEvent = new CounterServiceEvent(
                            availableCounters.poll(), waitingQueue.poll());
                }
            }

            return currentEvent;
        }

        @Override
        public void accept(Event event) {
            assert(event == currentEvent);
            waitingQueue.remove(currentEvent.clientArrivedEvent);
            availableCounters.remove(currentEvent.counterAvailableEvent);
            pushHistory(currentEvent);
            currentEvent = null;
        }

        @Override
        public void reject(Event event) {
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
                if (event instanceof ClientArrivedEvent) {
                    waitingQueue.remove((ClientArrivedEvent) event);
                }
                else if (event instanceof CounterAvailableEvent) {
                    availableCounters.remove((CounterAvailableEvent) event);
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