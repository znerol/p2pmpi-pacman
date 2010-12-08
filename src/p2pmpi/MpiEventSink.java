package p2pmpi;


import java.util.List;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;

import deism.AbstractStateHistory;
import deism.Event;
import deism.EventArrayDeque;
import deism.EventCondition;
import deism.EventQueue;
import deism.EventSink;

public class MpiEventSink extends AbstractStateHistory<Long, Event>
        implements EventSink {

    private final int mpireceiver;
    private final int mpitag;
    private final IntraComm mpicomm;
    private final EventCondition filter;
    private final EventQueue<Event> events = new EventArrayDeque<Event>();
    private final Worker worker;
    private long maxSimtime = 0;

    public MpiEventSink(IntraComm comm, int mpisender, int mpireceiver,
            int mpitag, EventCondition filter) {
        this.mpicomm = comm;
        this.mpireceiver = mpireceiver;
        this.mpitag = mpitag;
        this.filter = filter;

        if (mpicomm.Rank() == mpisender) {
            worker = new Worker();
        }
        else {
            worker = null;
        }
    }

    @Override
    public void revertHistory(List<Event> tail) {
        if (worker != null) {
            synchronized (worker) {
                for (Event event : tail) {
                    events.offer(event.inverseEvent());
                }
            }
        }
    }

    @Override
    public boolean offer(Event event) {
        boolean result = false;

        if (worker != null && filter.match(event)) {
            synchronized (worker) {
                events.offer(event);

                // We only tell the worker to actually send out the events in
                // the queue when the simulation is progressing forward in time.
                if (maxSimtime < event.getSimtime()) {
                    maxSimtime = event.getSimtime();
                    worker.notify();
                }
            }

            result = true;
            pushHistory(event);
        }

        return result;
    }

    @Override
    public void remove(Event event) {
        if (worker != null && filter.match(event)) {
            Event inverseEvent = event.inverseEvent();
            synchronized (worker) {
                events.offer(inverseEvent);
            }
            pushHistory(inverseEvent);
        }
    }

    @Override
    public void start(long startSimtime) {
        if (worker != null) {
            synchronized (worker) {
                worker.start();
            }
        }
    }

    @Override
    public void stop() {
        if (worker != null) {
            synchronized (worker) {
                worker.terminate();
            }
        }
    }

    private class Worker extends Thread {
        private boolean done = false;

        @Override
        public void run() {
            while (!done) {
                Event event = null;
                synchronized(this) {
                    for(event = events.poll();
                            event == null && !done;
                            event = events.poll()) {
                        try {
                            this.wait();
                        }
                        catch(InterruptedException e) {
                            // ignore
                        }
                    }
                }

                if (event != null) {
                    Event[] buffer = {event};
                    mpicomm.Send(buffer, 0, 1, MPI.OBJECT, mpireceiver, mpitag);
                }
            }
        }

        public void terminate() {
            done = true;
            if (isAlive()) {
                interrupt();
            }
        }
    }

}
