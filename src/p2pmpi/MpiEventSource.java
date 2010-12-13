package p2pmpi;

import java.util.ArrayDeque;
import java.util.Queue;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;

import deism.Event;
import deism.EventSource;
import deism.ExecutionGovernor;

public class MpiEventSource implements EventSource {

    private final int mpisender;
    private final int mpitag;
    private final IntraComm mpicomm;
    private final Queue<Event> events = new ArrayDeque<Event>();
    private final ExecutionGovernor governor;
    private final Worker worker;

    public MpiEventSource(IntraComm comm, int mpisender, int mpireceiver,
            int mpitag, ExecutionGovernor governor) {
        this.mpicomm = comm;
        this.mpisender = mpisender;
        this.mpitag = mpitag;
        this.governor = governor;

        if (mpicomm.Rank() == mpireceiver) {
            worker = new Worker();
        }
        else {
            worker = null;
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

    @Override
    public Event peek(long currentSimtime) {
        Event result = null;

        if (worker != null) {
            synchronized (worker) {
                result = events.peek();
            }
        }

        return result;
    }

    @Override
    public void remove(Event event) {
        if (worker != null) {
            synchronized (worker) {
                events.remove(event);
            }
        }
    }

    private class Worker extends Thread {
        private boolean done = false;

        @Override
        public void run() {
            while (!done) {
                Event[] recvBuffer = { null };
                mpicomm.Recv(recvBuffer, 0, 1, MPI.OBJECT, mpisender, mpitag);

                Event peekEvent = null;
                synchronized (this) {
                    events.offer(recvBuffer[0]);
                    peekEvent = events.peek();
                }

                if (peekEvent != null) {
                    governor.resume(peekEvent.getSimtime());
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
