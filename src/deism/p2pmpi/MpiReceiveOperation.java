package deism.p2pmpi;

import org.apache.log4j.Logger;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;
import deism.ipc.async.BlockingReceiveOperation;

/**
 * Implementation of {@link deism.ipc.async.BlockingReceiveOperation} for p2pmpi
 * 
 * @param <T> Type of message
 */
public class MpiReceiveOperation<T> implements BlockingReceiveOperation<T> {
    private final int mpisender;
    private final int mpitag;
    private final IntraComm mpicomm;
    private final static Logger logger = Logger
            .getLogger(MpiReceiveOperation.class);

    public MpiReceiveOperation(IntraComm comm, int mpiroot, int mpitag) {
        this.mpicomm = comm;
        this.mpisender = mpiroot;
        this.mpitag = mpitag;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T receive() {
        Object[] recvBuffer = { null };
        logger.debug("Start receiving from " + mpisender);
        mpicomm.Recv(recvBuffer, 0, 1, MPI.OBJECT, mpisender, mpitag);
        logger.debug("Received from " + mpisender + " " + recvBuffer[0]);
        return (T) recvBuffer[0];
    }
}
