package deism.p2pmpi;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;

import deism.core.Event;
import deism.core.EventSource;

public class MpiEventSource implements EventSource {

    private final int mpisender;
    private final int mpitag;
    private final IntraComm mpicomm;

    public MpiEventSource(IntraComm comm, int mpisender, int mpitag) {
        this.mpicomm = comm;
        this.mpisender = mpisender;
        this.mpitag = mpitag;
    }

    @Override
    public Event peek(long currentSimtime) {
        Event[] recvBuffer = { null };
        mpicomm.Recv(recvBuffer, 0, 1, MPI.OBJECT, mpisender, mpitag);
        return recvBuffer[0];
    }

    @Override
    public void remove(Event event) {
    }

    @Override
    public void start(long startSimtime) {
    }

    @Override
    public void stop() {
    }
}
