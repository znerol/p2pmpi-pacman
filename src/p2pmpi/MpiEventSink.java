package p2pmpi;


import java.util.ArrayDeque;
import java.util.Queue;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;

import deism.Event;
import deism.EventSink;

public class MpiEventSink implements EventSink {

    private final int mpireceiver;
    private final int mpitag;
    private final IntraComm mpicomm;
    private final Queue<Event> events = new ArrayDeque<Event>();
    private final Worker worker;

    public MpiEventSink(IntraComm comm, int mpisender, int mpireceiver,
            int mpitag) {
        this.mpicomm = comm;
        this.mpireceiver = mpireceiver;
        this.mpitag = mpitag;

        if (mpicomm.Rank() == mpisender) {
            worker = new Worker();
        }
        else {
            worker = null;
        }
    }

    @Override
    public void offer(Event event) {
        if (worker != null) {
            synchronized (worker) {
                events.offer(event);
                worker.notify();
            }
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
                            // ignore and just retry
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
