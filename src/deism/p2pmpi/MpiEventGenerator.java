package deism.p2pmpi;

import org.apache.log4j.Logger;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;

import deism.core.Blocking;
import deism.core.Event;
import deism.core.External;
import deism.core.Stateful;
import deism.core.StatefulEventGenerator;

@Stateful
@Blocking
@External
public class MpiEventGenerator implements StatefulEventGenerator {

    private final int mpisender;
    private final int mpitag;
    private final IntraComm mpicomm;
    private final static Logger logger = Logger.getLogger(MpiEventGenerator.class);

    public MpiEventGenerator(IntraComm comm, int mpisender, int mpitag) {
        this.mpicomm = comm;
        this.mpisender = mpisender;
        this.mpitag = mpitag;
    }

    @Override
    public Event poll() {
        Event[] recvBuffer = { null };
        mpicomm.Recv(recvBuffer, 0, 1, MPI.OBJECT, mpisender, mpitag);
        logger.debug("Received from " + mpisender + " event " + recvBuffer[0]);
        return recvBuffer[0];
    }
}
