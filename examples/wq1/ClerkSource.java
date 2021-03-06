package wq1;

import java.util.Queue;

import wqcommon.ClientArrivedEvent;

import deism.core.Event;
import deism.core.EventSource;

public class  ClerkSource implements EventSource {
    Queue<ClientArrivedEvent> jobs;
    Event currentEvent = null;

    public ClerkSource(Queue<ClientArrivedEvent> jobs) {
        this.jobs = jobs;
    }

    @Override
    public Event peek(long currentSimtime) {
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
    public void remove(Event event) {
        assert(currentEvent == event);
        currentEvent = null;
    }
}
