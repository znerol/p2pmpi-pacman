package deism.p2pmpi;

import java.util.ArrayDeque;
import java.util.Queue;

import p2pmpi.mpi.IntraComm;

import deism.core.Event;
import deism.core.External;
import deism.core.Startable;
import deism.core.Stateful;
import deism.core.StatefulEventGenerator;
import deism.ipc.async.BlockingReceiveOperation;
import deism.ipc.async.ReceiveThread;
import deism.ipc.base.Endpoint;
import deism.run.ExecutionGovernor;

@Stateful
@External
public class MpiEventGenerator implements StatefulEventGenerator, Startable,
        Endpoint<Event> {

    private final ExecutionGovernor governor;
    private final ReceiveThread<Event> receiver;
    private final Queue<Event> events = new ArrayDeque<Event>();

    public MpiEventGenerator(IntraComm comm, int mpisender, int mpitag,
            ExecutionGovernor governor) {
        BlockingReceiveOperation<Event> operation = new MpiReceiveOperation<Event>(
                comm, mpisender, mpitag);
        this.governor = governor;
        this.receiver = new ReceiveThread<Event>(operation, this);
    }

    @Override
    public Event poll() {
        synchronized (events) {
            return events.poll();
        }
    }

    @Override
    public void send(Event item) {
        Event peekEvent;
        synchronized (events) {
            events.offer(item);
            peekEvent = events.peek();
        }

        governor.resume(peekEvent.getSimtime());
    }

    @Override
    public void start(long simtime) {
        receiver.start();
    }

    @Override
    public void stop(long simtime) {
        receiver.terminate();
    }
}
