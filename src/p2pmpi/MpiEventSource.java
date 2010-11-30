package p2pmpi;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;

import deism.Event;
import deism.EventSource;
import deism.ExecutionGovernor;

public class MpiEventSource implements EventSource {

    private final int mpisender;
    private final int mpitag;
    private final IntraComm mpicomm;
    private final Queue<Event> events = new PriorityBlockingQueue<Event>();
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
            worker.start();
        }
    }

    @Override
    public void stop() {
        if (worker != null) {
            worker.terminate();
        }
    }

    @Override
    public Event receive(long currentSimtime) {
        return events.peek();
    }

    @Override
    public void accept(Event event) {
        events.remove(event);
    }

    @Override
    public void reject(Event event) {
    }

    private class Worker extends Thread {
        private boolean done = false;

        @Override
        public void run() {
            while (!done) {
                Event[] recvBuffer = { null };
                mpicomm.Recv(recvBuffer, 0, 1, MPI.OBJECT, mpisender, mpitag);
                events.offer(recvBuffer[0]);
                governor.resume(events.peek().getSimtime());
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
