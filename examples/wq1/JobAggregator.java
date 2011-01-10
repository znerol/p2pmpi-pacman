package wq1;

import java.util.Queue;

import wqcommon.ClientArrivedEvent;

import deism.core.Event;
import deism.core.EventDispatcher;

public class JobAggregator implements EventDispatcher {
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
