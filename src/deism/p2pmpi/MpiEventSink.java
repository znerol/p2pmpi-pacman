package deism.p2pmpi;


import org.apache.log4j.Logger;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;

import deism.core.Blocking;
import deism.core.Event;
import deism.core.EventSink;
import deism.core.Stateful;

@Stateful
@Blocking
public class MpiEventSink implements EventSink {

    private final int mpireceiver;
    private final int mpitag;
    private final IntraComm mpicomm;
    private final static Logger logger = Logger.getLogger(MpiEventSink.class);

    public MpiEventSink(IntraComm comm, int mpireceiver, int mpitag) {
        this.mpicomm = comm;
        this.mpireceiver = mpireceiver;
        this.mpitag = mpitag;
    }

    @Override
    public void offer(Event event) {
        Event[] buffer = {event};
        logger.debug("Start sending to " + mpireceiver + " event " + event);
        mpicomm.Send(buffer, 0, 1, MPI.OBJECT, mpireceiver, mpitag);
        logger.debug("Completed send to " + mpireceiver + " event " + event);
    }
}
