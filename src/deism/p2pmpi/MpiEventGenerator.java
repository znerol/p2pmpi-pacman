package deism.p2pmpi;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;

import deism.core.Event;
import deism.core.StatefulEventGenerator;

public class MpiEventGenerator implements StatefulEventGenerator {

    private final int mpisender;
    private final int mpitag;
    private final IntraComm mpicomm;

    public MpiEventGenerator(IntraComm comm, int mpisender, int mpitag) {
        this.mpicomm = comm;
        this.mpisender = mpisender;
        this.mpitag = mpitag;
    }

    @Override
    public Event poll() {
        Event[] recvBuffer = { null };
        mpicomm.Recv(recvBuffer, 0, 1, MPI.OBJECT, mpisender, mpitag);
        return recvBuffer[0];
    }
}
