package deism.p2pmpi;

import org.apache.log4j.Logger;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;
import deism.ipc.async.BlockingReceiveOperation;

public class MpiReceiveOperation<T> implements BlockingReceiveOperation<T> {
    private final int mpisender;
    private final int mpitag;
    private final IntraComm mpicomm;
    private final static Logger logger = Logger
            .getLogger(MpiEventGenerator.class);

    public MpiReceiveOperation(IntraComm comm, int mpisender, int mpitag) {
        this.mpicomm = comm;
        this.mpisender = mpisender;
        this.mpitag = mpitag;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T receive() {
        Object[] recvBuffer = { null };
        logger.debug("Start receiving from " + mpisender);
        mpicomm.Recv(recvBuffer, 0, 1, MPI.OBJECT, mpisender, mpitag);
        logger.debug("Received from " + mpisender + " message "
                + recvBuffer[0]);
        return (T) recvBuffer[0];
    }
}
