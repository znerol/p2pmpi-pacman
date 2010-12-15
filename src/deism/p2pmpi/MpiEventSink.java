package deism.p2pmpi;


import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;

import deism.core.Event;
import deism.core.EventSink;
import deism.run.Blocking;

public class MpiEventSink implements EventSink, Blocking {

    private final int mpireceiver;
    private final int mpitag;
    private final IntraComm mpicomm;

    public MpiEventSink(IntraComm comm, int mpireceiver, int mpitag) {
        this.mpicomm = comm;
        this.mpireceiver = mpireceiver;
        this.mpitag = mpitag;
    }

    @Override
    public void offer(Event event) {
        Event[] buffer = {event};
        mpicomm.Send(buffer, 0, 1, MPI.OBJECT, mpireceiver, mpitag);
    }
}
