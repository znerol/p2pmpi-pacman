package p2pmpi;

import java.util.ArrayList;
import java.util.List;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;

import deism.AbstractStateHistory;
import deism.Event;
import deism.EventDispatcher;

public class MpiEventDispatcher extends AbstractStateHistory<Long, Event>
        implements EventDispatcher {

    private final int mpisender;
    private final int mpireceiver;
    private final int mpitag;
    private final IntraComm mpicomm;
    private final List<Event> pending = new ArrayList<Event>();

    public MpiEventDispatcher(IntraComm comm, int mpisender, int mpireceiver,
            int mpitag) {
        this.mpicomm = comm;
        this.mpisender = mpisender;
        this.mpireceiver = mpireceiver;
        this.mpitag = mpitag;
    }

    @Override
    public void dispatchEvent(Event event) {
        if (mpicomm.Rank() == mpisender) {
            if (pending.contains(event)) {
                pending.remove(event);
            }
            else {
                Event[] buffer = new Event[1];
                buffer[0] = event;
                mpicomm.Isend(buffer, 0, 1, MPI.OBJECT, mpireceiver, mpitag);
            }
            pushHistory(event);
        }
    }

    @Override
    public void revertHistory(List<Event> tail) {
        pending.addAll(tail);
    }
}
