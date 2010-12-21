package deism.p2pmpi;

import org.apache.log4j.Logger;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;
import deism.ipc.async.BlockingSendOperation;

public class MpiSendOperation<T> implements BlockingSendOperation<T> {
    private final int mpireceiver;
    private final int mpitag;
    private final IntraComm mpicomm;
    private final static Logger logger = Logger
    .getLogger(MpiEventGenerator.class);

    public MpiSendOperation(IntraComm comm, int mpireceiver, int mpitag) {
        this.mpicomm = comm;
        this.mpireceiver = mpireceiver;
        this.mpitag = mpitag;
    }

    @Override
    public void send(T message) {
        Object[] buffer = { message };
        logger.debug("Start sending to " + mpireceiver + " message "
                + message);
        mpicomm.Send(buffer, 0, 1, MPI.OBJECT, mpireceiver, mpitag);
        logger.debug("Completed send to " + mpireceiver + " message "
                + message);
    }
}
