package p2pmpi;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;

import deism.Event;
import deism.EventSource;

public class MpiEventSource implements EventSource {

    private final int mpisender;
    private final int mpireceiver;
    private final int mpitag;
    private final IntraComm mpicomm;
    private Event currentEvent;

    public MpiEventSource(IntraComm comm, int mpisender, int mpireceiver,
            int mpitag) {
        this.mpicomm = comm;
        this.mpisender = mpisender;
        this.mpireceiver = mpireceiver;
        this.mpitag = mpitag;
    }

    @Override
    public Event receive(long currentSimtime) {
        if (currentEvent == null && mpicomm.Rank() == mpireceiver) {
            Event[] buffer = new Event[1];
            mpicomm.Irecv(buffer, 0, 1, MPI.OBJECT, mpisender, mpitag);
            currentEvent = buffer[0];
        }

        return currentEvent;
    }

    @Override
    public void accept(Event event) {
        assert (event == currentEvent);
        currentEvent = null;
    }

    @Override
    public void reject(Event event) {
    }
}
