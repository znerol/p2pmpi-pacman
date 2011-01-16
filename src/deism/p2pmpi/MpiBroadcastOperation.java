package deism.p2pmpi;

import org.apache.log4j.Logger;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;
import deism.ipc.async.BlockingReceiveOperation;
import deism.ipc.async.BlockingSendOperation;

/**
 * Implementation of an p2pmpi bcast either as
 * {@link deism.ipc.async.BlockingSendOperation} if the current rank is mpiroot
 * or {@link deism.ipc.async.BlockingReceiveOperation} otherwise.
 * 
 * @param <T> Type of message
 */
public class MpiBroadcastOperation<T> implements BlockingReceiveOperation<T>,
        BlockingSendOperation<T> {
    private final int mpiroot;
    private final IntraComm mpicomm;
    private final static Logger logger = Logger
            .getLogger(MpiBroadcastOperation.class);

    public MpiBroadcastOperation(IntraComm comm, int mpiroot) {
        this.mpicomm = comm;
        this.mpiroot = mpiroot;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T receive() {
        Object[] buffer = { null };
        logger.debug("Start listening for broadcast from " + mpiroot);
        mpicomm.Bcast(buffer, 0, 1, MPI.OBJECT, mpiroot);
        logger.debug("Received broadcast from " + mpiroot + " " + buffer[0]);
        return (T) buffer[0];
    }

    @Override
    public void send(T item) {
        Object[] buffer = { item };
        logger.debug("Start broadcast " + item);
        mpicomm.Bcast(buffer, 0, 1, MPI.OBJECT, mpiroot);
        logger.debug("Completed broadcast " + item);
    }
}
